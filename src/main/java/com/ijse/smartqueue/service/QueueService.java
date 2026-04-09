package com.ijse.smartqueue.service;

import com.ijse.smartqueue.dto.QueueRequestDTO;
import com.ijse.smartqueue.dto.QueueDTO;
import com.ijse.smartqueue.dto.NotificationDTO;
import com.ijse.smartqueue.entity.*;
import com.ijse.smartqueue.repository.*;
import com.ijse.smartqueue.service.custom.EmailService;
import com.ijse.smartqueue.service.custom.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@SuppressWarnings("null")
public class QueueService {
    private final QueueEntityRepository queueRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final TwilioService twilioService;

    public QueueDTO joinQueue(QueueRequestDTO request) {
        System.out.println("\n\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  [QueueService.joinQueue] USER JOINED QUEUE              ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("🔷 Request Data:");
        System.out.println("   User ID: " + request.getUserId());
        System.out.println("   Service ID: " + request.getServiceId());
        System.out.println("   Branch ID: " + request.getBranchId());
        
        // FETCH USER - First time
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("\n👤 User Retrieved from Database (First Fetch):");
        System.out.println("   User ID: " + user.getUserId());
        System.out.println("   Email: " + (user.getEmail() != null ? user.getEmail() : "[NULL]"));
        System.out.println("   Full Name: " + user.getFullName());
        System.out.println("   Phone: " + user.getPhone());
        System.out.println("   Password: " + (user.getPassword() != null ? user.getPassword() : "[NULL]"));
        
        // Determine user type
        String userType = "REGULAR SIGNUP";
        if (user.getPassword() != null && user.getPassword().equals("GOOGLE_OAUTH")) {
            userType = "🔵 GOOGLE OAUTH";
        }
        System.out.println("   User Type: " + userType);
        
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
        
        // RE-FETCH USER FROM DATABASE to verify email is persisted
        System.out.println("\n🔄 RE-FETCHING User from Database (Verify Email Persistence):");
        User userRefresh = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found on refresh"));
        System.out.println("   User ID: " + userRefresh.getUserId());
        System.out.println("   Email from DB: " + (userRefresh.getEmail() != null ? userRefresh.getEmail() : "[NULL] ❌ EMAIL NOT SAVED!"));
        System.out.println("   Full Name: " + userRefresh.getFullName());
        
        // SEND EMAIL TO USER - Works for ALL user types (Google OAuth, regular signup, etc)
        System.out.println("\n📧 EMAIL SENDING PHASE:");
        System.out.println("   User Email Value (From First Fetch): " + (user.getEmail() != null ? user.getEmail() : "[NULL]"));
        System.out.println("   User Email Value (From Refresh): " + (userRefresh.getEmail() != null ? userRefresh.getEmail() : "[NULL]"));
        System.out.println("   Email is Null: " + (userRefresh.getEmail() == null));
        System.out.println("   Email is Empty: " + (userRefresh.getEmail() != null && userRefresh.getEmail().isEmpty()));
        
        // Use the refreshed user with fresh database data
        User finalUser = userRefresh;
        
        if (finalUser.getEmail() != null && !finalUser.getEmail().isEmpty()) {
            try {
                String userName = finalUser.getFullName() != null ? finalUser.getFullName() : finalUser.getPhone();
                String serviceName = service != null ? service.getName() : "Service";
                String branchName = branch != null ? branch.getName() : "Branch";
                
                System.out.println("   ✉️ Email Details:");
                System.out.println("      To: " + finalUser.getEmail());
                System.out.println("      User Name: " + userName);
                System.out.println("      Service: " + serviceName);
                System.out.println("      Branch: " + branchName);
                System.out.println("      Position: " + position);
                System.out.println("      Token: " + saved.getToken());
                
                System.out.println("   ⏳ Calling emailService.sendQueueConfirmationEmail()...");
                emailService.sendQueueConfirmationEmail(
                        finalUser.getEmail(),
                        userName,
                        saved.getToken(),
                        serviceName,
                        branchName,
                        position
                );
                System.out.println("   ✅ Email method completed without exception");
            } catch (Exception mailErr) {
                System.err.println("   ❌ EMAIL EXCEPTION: " + mailErr.getClass().getName());
                System.err.println("   ❌ Message: " + mailErr.getMessage());
                mailErr.printStackTrace();
            }
        } else {
            System.err.println("   ⚠️ SKIPPING EMAIL SEND:");
            System.err.println("      Reason: Email is " + (user.getEmail() == null ? "NULL" : "EMPTY"));
        }
        
        System.out.println("╔════════════════════════════════════════════════════════╗\n");
        
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
     * Call queue - Update status to CALLED and send email/SMS notification to user
     * @param queueId Queue ID to call
     */
    public void callQueue(Long queueId) {
        try {
            System.out.println("\n\n╔════════════════════════════════════════════╗");
            System.out.println("║   🔵 QUEUE SERVICE: callQueue() INVOKED    ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println("📋 Queue ID: " + queueId);
            System.out.flush();
            System.err.println("\n🔵 START: callQueue invoked for queueId: " + queueId);
            
            QueueEntity queue = queueRepository.findById(queueId)
                    .orElseThrow(() -> new RuntimeException("Queue not found"));

            System.out.println("✅ Queue found - Token: " + queue.getToken());
            System.out.flush();
            System.err.println("🔵 Queue found - Token: " + queue.getToken());

            // Get user and related information
            User user = queue.getUser();
            ServiceEntity service = queue.getService();
            Branch branch = queue.getBranch();

            if (user == null) {
                System.out.println("❌ ERROR: User is null for queue");
                System.out.flush();
                System.err.println("❌ ERROR: User is null for queue");
                throw new RuntimeException("User not found for queue");
            }

            System.out.println("✅ User found - Email: " + user.getEmail() + ", Name: " + user.getFullName() + ", Phone: " + (user.getPhone() != null ? user.getPhone() : "NULL"));
            System.out.flush();
            System.err.println("🔵 User found - Email: " + user.getEmail() + ", Name: " + user.getFullName());

            // Update queue status to CALLED
            queue.setStatus(QueueStatus.CALLED);
            queue.setCalledTime(LocalDateTime.now());
            queue.setUpdatedAt(LocalDateTime.now());
            queueRepository.save(queue);
            
            System.out.println("✅ Queue status updated to CALLED");
            System.out.flush();
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

            // Send SMS notification if user has phone number
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                System.out.println("\n\n");
                System.out.println("╔════════════════════════════════════════════╗");
                System.out.println("║        📱 SMS NOTIFICATION SECTION         ║");
                System.out.println("╚════════════════════════════════════════════╝");
                System.out.println("✅ User has phone number: " + user.getPhone());
                System.out.flush();
                
                try {
                    String serviceName = service != null ? service.getName() : "Service";
                    String branchName = branch != null ? branch.getName() : "Counter";
                    String phoneNumber = user.getPhone().trim();
                    
                    System.out.println("\n📱 ===== SMS SENDING DETAILS =====");
                    System.out.println("   Raw Phone from DB: " + phoneNumber);
                    System.out.println("   Phone Length: " + phoneNumber.length());
                    System.out.println("   Service: " + serviceName);
                    System.out.println("   Branch: " + branchName);
                    System.out.println("   Token: " + queue.getToken());
                    System.out.println("   User ID: " + user.getUserId());
                    System.out.println("===================================");
                    System.out.flush();
                    
                    System.out.println("\n🔵 Calling twilioService.sendQueueReadySMS()...");
                    System.out.flush();
                    
                    boolean smsSent = twilioService.sendQueueReadySMS(
                            phoneNumber,
                            queue.getToken(),
                            serviceName,
                            branchName
                    );
                    System.out.flush();
                    
                    if (smsSent) {
                        System.out.println("✅✅ SUCCESS: SMS sent to " + phoneNumber);
                    } else {
                        System.out.println("❌ SMS FAILED: Could not send SMS to " + phoneNumber);
                    }
                    System.out.println("╚════════════════════════════════════════════╝\n");
                    System.out.flush();
                    
                } catch (Exception smsErr) {
                    System.out.println("❌ SMS EXCEPTION: " + smsErr.getClass().getSimpleName());
                    System.out.println("   Message: " + smsErr.getMessage());
                    smsErr.printStackTrace();
                    System.out.flush();
                    // Don't throw exception - queue was already called successfully
                }
            } else {
                System.out.println("\n\n");
                System.out.println("╔════════════════════════════════════════════╗");
                System.out.println("║        📱 SMS NOTIFICATION SECTION         ║");
                System.out.println("╚════════════════════════════════════════════╝");
                System.out.println("⚠️ ❌ WARNING: User phone is NULL or EMPTY");
                System.out.println("\n❌ SMS NOT SENT - REASON: Missing Phone Number");
                System.out.println("\n📌 ACTION REQUIRED:");
                System.out.println("   User: " + user.getFullName() + " (ID: " + user.getUserId() + ")");
                System.out.println("   Email: " + user.getEmail());
                System.out.println("   Current Phone: [EMPTY or NULL]");
                System.out.println("\n💡 SOLUTION:");
                System.out.println("   1. User must log in to the app");
                System.out.println("   2. Go to Settings");
                System.out.println("   3. Click 'Edit Profile'");
                System.out.println("   4. Enter their phone number (e.g., +94779649968)");
                System.out.println("   5. Click 'Save Changes'");
                System.out.println("   6. Then SMS will be sent when they call a queue");
                System.out.println("╚════════════════════════════════════════════╝\n");
                System.out.flush();
            }

            // Create notification in a separate transaction to prevent rollback issues
            try {
                createQueueCalledNotification(user, service, branch, queue.getToken(), queueId);
            } catch (Exception notifErr) {
                System.err.println("❌ NOTIFICATION FAILED: " + notifErr.getMessage());
                notifErr.printStackTrace();
                // Don't throw exception - queue was already called successfully
            }

            System.err.println("🔵 END: callQueue completed\n");
        } catch (Exception e) {
            System.err.println("❌ CRITICAL ERROR in callQueue: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Create notification in a separate transaction
     * This prevents notification creation failures from rolling back the queue status update
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createQueueCalledNotification(User user, ServiceEntity service, Branch branch, String token, Long queueId) {
        try {
            String serviceName = service != null ? service.getName() : "Service";
            String branchName = branch != null ? branch.getName() : "Counter";
            
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setUserId(user.getUserId());
            notificationDTO.setTitle("Queue Called");
            notificationDTO.setMessage("Your queue token " + token + " is being called at " + branchName + " for " + serviceName);
            notificationDTO.setType("QUEUE_CALLED");
            notificationDTO.setQueueId(queueId);
            
            notificationService.createNotification(notificationDTO);
            System.err.println("✅ SUCCESS: Notification created for user " + user.getUserId());
        } catch (Exception e) {
            System.err.println("⚠️ WARNING: Failed to create notification: " + e.getMessage());
            // Log but don't throw - we don't want notification creation to affect queue status
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
                    dto.setIsPriority(q.getPriority() != PriorityLevel.NORMAL);
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

    public QueueDTO updateQueue(Long queueId, QueueDTO queueDTO) {
        QueueEntity queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue not found with id: " + queueId));
        
        // Update priority if isPriority flag is present
        if (queueDTO.getIsPriority() != null && queueDTO.getIsPriority()) {
            queue.setPriority(PriorityLevel.GOLD);
        } else if (queueDTO.getIsPriority() != null && !queueDTO.getIsPriority()) {
            queue.setPriority(PriorityLevel.NORMAL);
        }
        
        // Update other fields if provided
        if (queueDTO.getStatus() != null && !queueDTO.getStatus().isEmpty()) {
            try {
                queue.setStatus(QueueStatus.valueOf(queueDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                // Status not valid, skip update
            }
        }
        
        queue.setUpdatedAt(LocalDateTime.now());
        queueRepository.save(queue);
        
        // Convert back to DTO
        QueueDTO updatedDTO = new QueueDTO();
        updatedDTO.setQueueId(queue.getQueueId());
        updatedDTO.setToken(queue.getToken());
        updatedDTO.setUserId(queue.getUser().getUserId());
        updatedDTO.setServiceId(queue.getService().getServiceId());
        updatedDTO.setBranchId(queue.getBranch().getBranchId());
        updatedDTO.setPosition(queue.getPosition());
        updatedDTO.setStatus(queue.getStatus().toString());
        updatedDTO.setPriority(queue.getPriority().toString());
        updatedDTO.setIsPriority(queue.getPriority() != PriorityLevel.NORMAL);
        updatedDTO.setJoinedTime(queue.getJoinedTime());
        updatedDTO.setCalledTime(queue.getCalledTime());
        updatedDTO.setServeStartTime(queue.getServeStartTime());
        updatedDTO.setCompletedTime(queue.getCompletedTime());
        updatedDTO.setEstimatedWaitTime(queue.getEstimatedWaitTime());
        updatedDTO.setActualServiceDuration(queue.getActualServiceDuration());
        updatedDTO.setCreatedAt(queue.getCreatedAt());
        updatedDTO.setUpdatedAt(queue.getUpdatedAt());
        
        return updatedDTO;
    }

    public Map<String, Object> getQueueDetails(String token) {
        QueueEntity queue = queueRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Queue not found with token: " + token));
        
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("queueId", queue.getQueueId());
        details.put("token", queue.getToken());
        details.put("position", queue.getPosition());
        details.put("status", queue.getStatus().toString());
        details.put("priority", queue.getPriority().toString());
        details.put("user", mapUserToMap(queue.getUser()));
        details.put("service", mapServiceToMap(queue.getService()));
        details.put("branch", mapBranchToMap(queue.getBranch()));
        details.put("estimatedWaitTime", queue.getEstimatedWaitTime());
        details.put("actualServiceDuration", queue.getActualServiceDuration());
        details.put("joinedTime", queue.getJoinedTime());
        details.put("calledTime", queue.getCalledTime());
        details.put("serveStartTime", queue.getServeStartTime());
        details.put("completedTime", queue.getCompletedTime());
        details.put("createdAt", queue.getCreatedAt());
        details.put("updatedAt", queue.getUpdatedAt());
        
        return details;
    }

    private Map<String, Object> mapUserToMap(User user) {
        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("userId", user.getUserId());
        userMap.put("fullName", user.getFullName());
        userMap.put("email", user.getEmail());
        userMap.put("phone", user.getPhone());
        return userMap;
    }

    private Map<String, Object> mapServiceToMap(ServiceEntity service) {
        Map<String, Object> serviceMap = new LinkedHashMap<>();
        serviceMap.put("serviceId", service.getServiceId());
        serviceMap.put("name", service.getName());
        serviceMap.put("description", service.getDescription());
        return serviceMap;
    }

    private Map<String, Object> mapBranchToMap(Branch branch) {
        Map<String, Object> branchMap = new LinkedHashMap<>();
        branchMap.put("branchId", branch.getBranchId());
        branchMap.put("name", branch.getName());
        branchMap.put("address", branch.getAddress());
        return branchMap;
    }
}


