package com.ijse.smartqueue.service.custom;

public interface EmailService {
    void sendQueueCalledEmail(String userEmail, String userName, String token, String serviceName, String branchName);
    
    void sendQueueRemovedEmail(String userEmail, String userName, String token, String reason);
    
    void sendQueueConfirmationEmail(String userEmail, String userName, String token, String serviceName, String branchName, Integer position);
    
    void sendSimpleEmail(String to, String subject, String body);
}
