package com.moura.restapiproducer.application.service;

import com.moura.restapiproducer.infra.model.Event;
import com.moura.restapiproducer.infra.model.EventType;
import com.moura.restapiproducer.infra.model.User;
import com.moura.restapiproducer.infra.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserService userService;

    public List<Event> getEvents() {
        try {
            return eventRepository.findAll();
        } catch (RuntimeException e) {
            throw new RuntimeException("internal error: " + e.getMessage());
        }
    }

    public Event getEventByType(EventType type) {
        try {
            Optional<Event> e = eventRepository.findByType(type);
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
            Event event = eventRepository.findByType(type).get();
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
            Event event = eventRepository.findByType(type).get();
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
}
