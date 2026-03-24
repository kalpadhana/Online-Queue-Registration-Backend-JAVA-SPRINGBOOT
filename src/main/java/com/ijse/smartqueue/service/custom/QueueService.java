package com.ijse.smartqueue.service.custom;

import com.ijse.smartqueue.dto.QueueDTO;

import java.util.List;

public interface QueueService {

    QueueDTO joinQueue(Integer userId, Integer serviceId);

    void updateQueueStatus(Integer queueId, String status);

    QueueDTO getQueueDetails(Integer queueId);

    List<QueueDTO> getActiveQueuesByService(Integer serviceId);

    List<QueueDTO> getUserQueueHistory(Integer userId);
}