package com.ijse.smartqueue.service;

import com.ijse.smartqueue.dto.QueueRequestDTO;
import com.ijse.smartqueue.dto.QueueDTO;
import com.ijse.smartqueue.entity.*;
import com.ijse.smartqueue.repository.*;
import com.ijse.smartqueue.service.custom.EmailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QueueService {
    private final QueueEntityRepository queueRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final BranchRepository branchRepository;
    private final QueueHistoryRepository historyRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    public QueueDTO joinQueue(QueueRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // Calculate position based on all non-cancelled queues in the branch
        Integer position = queueRepository.countByBranchIdAndStatusIn(
                branch.getBranchId(), 
                List.of(QueueStatus.WAITING, QueueStatus.CALLED)
        ) + 1;

        // Generate unique token using branch ID, timestamp, and sequential position
        long timestamp = System.currentTimeMillis() % 100000; // Last 5 digits of timestamp
        String token = "Q" + branch.getBranchId() + "-" + String.format("%04d", position) + "-" + String.format("%05d", timestamp);

        // Check if token already exists (race condition safety)
        int retries = 0;
        while (queueRepository.findByToken(token).isPresent() && retries < 5) {
            position++;
            timestamp = System.currentTimeMillis() % 100000;
            token = "Q" + branch.getBranchId() + "-" + String.format("%04d", position) + "-" + String.format("%05d", timestamp);
            retries++;
        }

        if (queueRepository.findByToken(token).isPresent()) {
            throw new RuntimeException("Failed to generate unique token after retries");
        }

        // Create queue entry
        QueueEntity queue = new QueueEntity();
        queue.setToken(token);
        queue.setUser(user);
        queue.setService(service);
        queue.setBranch(branch);
        queue.setPosition(position);
        queue.setStatus(QueueStatus.WAITING);
        queue.setPriority(PriorityLevel.NORMAL);
        queue.setJoinedTime(LocalDateTime.now());
        queue.setEstimatedWaitTime(service.getAvgWaitTime());
        queue.setCreatedAt(LocalDateTime.now());
        queue.setUpdatedAt(LocalDateTime.now());

        QueueEntity saved = queueRepository.save(queue);
        return modelMapper.map(saved, QueueDTO.class);
    }

    public List<QueueDTO> getActiveQueues(Long branchId) {
        return queueRepository.findByBranchIdAndStatus(branchId, QueueStatus.WAITING)
                .stream()
                .map(q -> modelMapper.map(q, QueueDTO.class))
                .collect(Collectors.toList());
    }

    public QueueDTO getQueueByToken(String token) {
        QueueEntity queue = queueRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Queue entry not found"));
        return modelMapper.map(queue, QueueDTO.class);
    }

    public void updateQueueStatus(Long queueId, QueueStatus status) {
        QueueEntity queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue entry not found"));

        if (status == QueueStatus.CALLED) {
            queue.setCalledTime(LocalDateTime.now());
        } else if (status == QueueStatus.BEING_SERVED) {
            queue.setServeStartTime(LocalDateTime.now());
        } else if (status == QueueStatus.COMPLETED) {
            queue.setCompletedTime(LocalDateTime.now());
            if (queue.getServeStartTime() != null) {
                long duration = java.time.temporal.ChronoUnit.MINUTES.between(
                        queue.getServeStartTime(), queue.getCompletedTime());
                queue.setActualServiceDuration((int) duration);
            }
        }

        queue.setStatus(status);
        queue.setUpdatedAt(LocalDateTime.now());
        queueRepository.save(queue);
    }

    /**
     * Call queue - Update status to CALLED and send email notification to user
     * @param queueId Queue ID to call
     */
    public void callQueue(Long queueId) {
        try {
            System.err.println("\n🔵 START: callQueue invoked for queueId: " + queueId);
            
            QueueEntity queue = queueRepository.findById(queueId)
                    .orElseThrow(() -> new RuntimeException("Queue not found"));

            System.err.println("🔵 Queue found - Token: " + queue.getToken());

            // Get user and related information
            User user = queue.getUser();
            ServiceEntity service = queue.getService();
            Branch branch = queue.getBranch();

            if (user == null) {
                System.err.println("❌ ERROR: User is null for queue");
                throw new RuntimeException("User not found for queue");
            }

            System.err.println("🔵 User found - Email: " + user.getEmail() + ", Name: " + user.getFullName());

            // Update queue status to CALLED
            queue.setStatus(QueueStatus.CALLED);
            queue.setCalledTime(LocalDateTime.now());
            queue.setUpdatedAt(LocalDateTime.now());
            queueRepository.save(queue);
            
            System.err.println("🔵 Queue status updated to CALLED");

            // Send email notification if user has email
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                try {
                    String userName = user.getFullName() != null ? user.getFullName() : user.getPhone();
                    String serviceName = service != null ? service.getName() : "Service";
                    String branchName = branch != null ? branch.getName() : "Counter";
                    
                    System.err.println("🔵 Attempting to send email to: " + user.getEmail());
                    System.err.println("   - Service: " + serviceName + ", Branch: " + branchName);
                    
                    emailService.sendQueueCalledEmail(
                            user.getEmail(),
                            userName,
                            queue.getToken(),
                            serviceName,
                            branchName
                    );
                    System.err.println("✅ SUCCESS: Email sent to " + user.getEmail());
                } catch (Exception mailErr) {
                    System.err.println("❌ EMAIL FAILED: " + mailErr.getClass().getSimpleName());
                    System.err.println("   Message: " + mailErr.getMessage());
                    mailErr.printStackTrace();
                    // Don't throw exception - queue was already called successfully
                }
            } else {
                System.err.println("⚠️ WARNING: User email is null or empty");
            }
            System.err.println("🔵 END: callQueue completed\n");
        } catch (Exception e) {
            System.err.println("❌ CRITICAL ERROR in callQueue: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void cancelQueue(Long queueId) {
        QueueEntity queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue entry not found"));
        queue.setStatus(QueueStatus.CANCELLED);
        queue.setUpdatedAt(LocalDateTime.now());
        queueRepository.save(queue);
    }

    public List<QueueDTO> getUserQueues(Long userId) {
        return queueRepository.findByUserId(userId)
                .stream()
                .map(q -> modelMapper.map(q, QueueDTO.class))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getQueueDetails(String token) {
        QueueEntity queue = queueRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Queue entry not found"));

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("queueId", queue.getQueueId());
        details.put("token", queue.getToken());
        details.put("serviceName", queue.getService().getName());
        details.put("serviceId", queue.getService().getServiceId());
        details.put("branchName", queue.getBranch().getName());
        details.put("branchId", queue.getBranch().getBranchId());
        details.put("position", queue.getPosition());
        details.put("status", queue.getStatus().toString());
        details.put("priority", queue.getPriority().toString());
        details.put("estimatedWaitTime", queue.getEstimatedWaitTime());
        details.put("joinedTime", queue.getJoinedTime());
        details.put("calledTime", queue.getCalledTime());
        details.put("serveStartTime", queue.getServeStartTime());
        details.put("completedTime", queue.getCompletedTime());
        details.put("actualServiceDuration", queue.getActualServiceDuration());
        details.put("createdAt", queue.getCreatedAt());
        details.put("updatedAt", queue.getUpdatedAt());
        
        return details;
    }

    public List<String> getUpcomingQueueTokens(Long branchId, Long serviceId, int limit) {
        List<QueueEntity> upcomingQueues = queueRepository.findByServiceIdAndBranchIdAndStatus(serviceId, branchId, QueueStatus.WAITING)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
        
        return upcomingQueues.stream()
                .map(QueueEntity::getToken)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getActiveQueuesByBranch(Long branchId) {
        List<QueueEntity> activeQueues = queueRepository.findByBranchIdAndStatus(branchId, QueueStatus.WAITING);
        
        return activeQueues.stream()
                .map(queue -> {
                    Map<String, Object> queueInfo = new LinkedHashMap<>();
                    queueInfo.put("token", queue.getToken());
                    queueInfo.put("serviceName", queue.getService().getName());
                    queueInfo.put("position", queue.getPosition());
                    queueInfo.put("status", queue.getStatus().toString());
                    queueInfo.put("estimatedWaitTime", queue.getEstimatedWaitTime());
                    queueInfo.put("joinedTime", queue.getJoinedTime());
                    return queueInfo;
                })
                .collect(Collectors.toList());
    }

    public List<QueueDTO> getAllQueues() {
        return queueRepository.findAll()
                .stream()
                .map(q -> {
                    QueueDTO dto = new QueueDTO();
                    dto.setQueueId(q.getQueueId());
                    dto.setToken(q.getToken());
                    dto.setUserId(q.getUser().getUserId());
                    dto.setServiceId(q.getService().getServiceId());
                    dto.setBranchId(q.getBranch().getBranchId());
                    dto.setPosition(q.getPosition());
                    dto.setStatus(q.getStatus().toString());
                    dto.setPriority(q.getPriority().toString());
                    dto.setJoinedTime(q.getJoinedTime());
                    dto.setCalledTime(q.getCalledTime());
                    dto.setServeStartTime(q.getServeStartTime());
                    dto.setCompletedTime(q.getCompletedTime());
                    dto.setEstimatedWaitTime(q.getEstimatedWaitTime());
                    dto.setActualServiceDuration(q.getActualServiceDuration());
                    dto.setCreatedAt(q.getCreatedAt());
                    dto.setUpdatedAt(q.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}


