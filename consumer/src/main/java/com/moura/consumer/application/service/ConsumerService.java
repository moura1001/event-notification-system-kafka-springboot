package com.moura.consumer.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moura.consumer.application.service.dto.EventKafkaConsume;
import com.moura.consumer.application.service.dto.EventKafkaProduce;
import com.moura.consumer.application.service.dto.EventResp;
import com.moura.consumer.application.service.dto.UserResp;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class ConsumerService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;
    @Value("${app.api-service-url}")
    private String API_SERVICE_URL;

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics="notification-started", groupId="consumers1")
    private void onConsume(ConsumerRecord<String, String> record) {

        EventKafkaConsume eventConsume;

        try {
            eventConsume = objectMapper.readValue(record.value(), EventKafkaConsume.class);
        } catch (JsonProcessingException e) {
            //throw new RuntimeException(e);
            return;
        }

        ResponseEntity<EventResp> response;

        try {
            response = restTemplate.getForEntity(API_SERVICE_URL+"/"+eventConsume.type(), EventResp.class);
            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                throw new RuntimeException("service unavailable");
            }
            
            EventResp eventResp = response.getBody();
            for (UserResp u: eventResp.subscribedes()) {
                notificationService.sendNotification(u, eventConsume.description());
            }
        } catch (RuntimeException e) {
            return;
        }

        String message = "";

        try {
            message = objectMapper.writeValueAsString(new EventKafkaProduce(eventConsume.name(), eventConsume.type()));
        } catch (JsonProcessingException e) {
            //throw new RuntimeException("internal error: " + e.getMessage());
            return;
        }

        try {

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("notification-completed", message);
            future.whenComplete((result, ex) -> {
                if (ex != null)
                    throw new RuntimeException(ex);
            });

        } catch (RuntimeException e) {}
    }
}
