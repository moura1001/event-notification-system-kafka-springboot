package com.moura.restapiproducer.infra.repository;

import com.moura.restapiproducer.infra.model.Event;
import com.moura.restapiproducer.infra.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByType(EventType type);

    boolean existsByType(EventType type);

    @Query("SELECT COUNT(e)>0 FROM Event e INNER JOIN e.subscribedes sub " +
            "WHERE e.type = :type AND sub.email = :email")
    boolean isAlreadySubscribed(String email, EventType type);
}
