package com.moura.restapiproducer.infra.repository;

import com.moura.restapiproducer.infra.model.Event;
import com.moura.restapiproducer.infra.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.subscribedes")
    List<Event> findAllWithtSubscribedes();

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.subscribedes WHERE e.type = :type")
    Optional<Event> findByTypeWithtSubscribedes(EventType type);

    @Query("SELECT e FROM Event e WHERE e.type = :type")
    Optional<Event> findByTypeWithoutSubscribedes(EventType type);

    boolean existsByType(EventType type);

    @Query("SELECT COUNT(e)>0 FROM Event e INNER JOIN e.subscribedes sub " +
            "WHERE e.type = :type AND sub.email = :email")
    boolean isAlreadySubscribed(String email, EventType type);
}
