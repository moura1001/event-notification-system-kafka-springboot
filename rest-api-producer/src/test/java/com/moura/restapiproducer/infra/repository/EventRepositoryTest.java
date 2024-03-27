package com.moura.restapiproducer.infra.repository;

import com.moura.restapiproducer.infra.model.Event;
import com.moura.restapiproducer.infra.model.EventType;
import com.moura.restapiproducer.infra.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EventRepository eventRepository;

    @BeforeAll
    void init() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
        Set<User> users = Set.of(
                new User("email1@email.com", "User 1"),
                new User("email2@email.com", "User 2"),
                new User("email3@email.com", "User 3")
        );
        userRepository.saveAllAndFlush(users);

        Event e1 = new Event("News", "description...", EventType.NEWS);
        e1.setSubscribedes(users);
        Event e2 = new Event("Status Updates", "description...", EventType.STATUS_UPDATES);
        Set<Event> events = Set.of(
                e1,
                e2
        );
        eventRepository.saveAllAndFlush(events);
    }

    @Test
    @Order(1)
    void shouldBeFindByType() {
        Optional<Event> event = eventRepository.findByTypeWithtSubscribedes(EventType.NEWS);
        assertTrue(event.isPresent());
        assertEquals(3, event.get().getSubscribedes().size());

        event = eventRepository.findByTypeWithtSubscribedes(EventType.STATUS_UPDATES);
        assertTrue(event.isPresent());
        assertEquals(0, event.get().getSubscribedes().size());

        event = eventRepository.findByTypeWithtSubscribedes(EventType.NEW_POSTS);
        assertFalse(event.isPresent());
    }

    @Test
    @Order(2)
    void isAlreadySubscribed() {
        assertTrue(eventRepository.isAlreadySubscribed("email2@email.com", EventType.NEWS));
        assertFalse(eventRepository.isAlreadySubscribed("email2@email.com", EventType.STATUS_UPDATES));
    }

    @Test
    @Order(3)
    void addSubscriberesToStatusUpdates() {
        Event event = eventRepository.findByTypeWithtSubscribedes(EventType.STATUS_UPDATES).get();
        assertTrue(event.addSubscriber(new User("email1@email.com", "User 1")));
        assertFalse(event.addSubscriber(new User("email1@email.com", "User 1")));
        assertTrue(event.addSubscriber(new User("email3@email.com", "User 3")));
        eventRepository.saveAndFlush(event);

        event = eventRepository.findByTypeWithtSubscribedes(EventType.STATUS_UPDATES).get();
        assertEquals(2, event.getSubscribedes().size());
        assertTrue(event.addSubscriber(new User("email2@email.com", "User 2")));
        eventRepository.saveAndFlush(event);

        event = eventRepository.findByTypeWithtSubscribedes(EventType.STATUS_UPDATES).get();
        assertEquals(3, event.getSubscribedes().size());
    }

    @Test
    @Order(4)
    void removeSubscriberesToNews() {
        Event event = eventRepository.findByTypeWithtSubscribedes(EventType.NEWS).get();
        assertTrue(event.removeSubscriber(new User("email1@email.com", "User 1")));
        assertFalse(event.removeSubscriber(new User("email1@email.com", "User 1")));
        assertTrue(event.removeSubscriber(new User("email3@email.com", "User 3")));
        eventRepository.saveAndFlush(event);

        event = eventRepository.findByTypeWithtSubscribedes(EventType.NEWS).get();
        assertEquals(1, event.getSubscribedes().size());
        assertFalse(event.addSubscriber(new User("email2@email.com", "User 2")));
        eventRepository.saveAndFlush(event);

        event = eventRepository.findByTypeWithtSubscribedes(EventType.NEWS).get();
        assertEquals(1, event.getSubscribedes().size());
        assertTrue(event.getSubscribedes().contains(new User("email2@email.com", "User 2")));
    }

    @Test
    @Order(5)
    void shouldBeFindByTypeWithoutSubscribedes() {
        Optional<Event> event = eventRepository.findByTypeWithoutSubscribedes(EventType.NEWS);
        assertTrue(event.isPresent());
        assertEquals("News", event.get().getName());
        assertEquals("description...", event.get().getDescription());
        assertEquals(EventType.NEWS, event.get().getType());
    }
}