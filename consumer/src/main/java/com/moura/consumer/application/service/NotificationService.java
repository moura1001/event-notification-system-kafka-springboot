package com.moura.consumer.application.service;

import com.moura.consumer.application.service.dto.UserResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void sendNotification(UserResp user, String message) throws RuntimeException {
        String email = user.email();
        logger.info("the message '" + message + "' was sent to the email " + email);
    }
}