package com.moura.restapiproducer.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moura.restapiproducer.infra.model.Event;
import com.moura.restapiproducer.infra.model.EventType;
import com.moura.restapiproducer.infra.model.User;
import com.moura.restapiproducer.infra.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Event> getEvents() {
        try {
            return eventRepository.findAllWithtSubscribedes();
        } catch (RuntimeException e) {
            throw new RuntimeException("internal error: " + e.getMessage());
        }
    }

    public Event getEventByType(EventType type) {
        try {
            Optional<Event> e = eventRepository.findByTypeWithtSubscribedes(type);
            if (e.isPresent())
                return e.get();

            return null;
        } catch (RuntimeException e) {
            throw new RuntimeException("internal error: " + e.getMessage());
        }
    }

    public Event createEvent(Event event) {
        try {
            return eventRepository.save(event);
        } catch (RuntimeException e) {
            if (e instanceof DataIntegrityViolationException) {
                throw new RuntimeException("event already registered");
            }

            throw new RuntimeException("internal error: " + e.getMessage());
        }
    }

    public boolean addSubscriber(String email, EventType type) {
        if (isAlreadySubscribed(email, type))
            return false;

        User user = userService.getUserByEmail(email);
        try {
            Event event = eventRepository.findByTypeWithtSubscribedes(type).get();
            event.addSubscriber(user);
            eventRepository.save(event);
        } catch (RuntimeException e) {
            throw new RuntimeException("internal error: " + e.getMessage());
        }

        return true;
    }

    public boolean removeSubscriber(String email, EventType type) {
        if (!isAlreadySubscribed(email, type))
            return false;

        User user = userService.getUserByEmail(email);
        try {
            Event event = eventRepository.findByTypeWithtSubscribedes(type).get();
            event.removeSubscriber(user);
            eventRepository.save(event);
        } catch (RuntimeException e) {
            throw new RuntimeException("internal error: " + e.getMessage());
        }

        return true;
    }

    private boolean isAlreadySubscribed(String email, EventType type) {
        if (email == null || email.isBlank())
            throw new RuntimeException("invalid email");

        if (type == null)
            throw new RuntimeException("invalid event type");

        userService.existsUser(email);
        if (!eventRepository.existsByType(type))
            throw new RuntimeException("the event " + type.name() + " is not registered");

        try {
            return eventRepository.isAlreadySubscribed(email, type);
        } catch (RuntimeException e) {
            throw new RuntimeException("internal error: " + e.getMessage());
        }
    }

    public boolean notifySubscribedes(EventType type) {
        if (type == null)
            throw new RuntimeException("invalid event type");

        Optional<Event> event = eventRepository.findByTypeWithoutSubscribedes(type);
        if (event.isEmpty())
            throw new RuntimeException("the event " + type.name() + " is not registered");

        String message = "";

        try {
            message = objectMapper.writeValueAsString(new EventKafkaProducer(event.get()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("internal error: " + e.getMessage());
        }

        try {

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("notification-started", message);
            future.whenComplete((result, ex) -> {
                if (ex != null)
                    throw new RuntimeException(ex);
            });

        } catch (RuntimeException e) {
            return false;
        }

        return true;
    }
}
