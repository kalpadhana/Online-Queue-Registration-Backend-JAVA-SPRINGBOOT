package com.ijse.smartqueue.service;

import com.ijse.smartqueue.dto.QueueRequestDTO;
import com.ijse.smartqueue.dto.QueueDTO;
import com.ijse.smartqueue.entity.*;
import com.ijse.smartqueue.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

    public QueueDTO joinQueue(QueueRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // Calculate position
        Integer position = queueRepository.countByBranchIdAndStatus(branch.getBranchId(), QueueStatus.WAITING) + 1;

        // Generate token
        String token = "Q" + branch.getBranchId() + "-" + String.format("%04d", position);

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
}


