package com.moura.restapiproducer.infra.model;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(nullable = false, length = 512)
    private String name;

    @Column(nullable = false, length = 2048)
    private String description;

    @Column(nullable = false, length = 128)
    @Enumerated
    private EventType type;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "events_subscribedes",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_email")
    )
    private Set<User> subscribedes;

    public Event() {}

    public Event(String name, String description, EventType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Set<User> getSubscribedes() {
        return subscribedes;
    }

    public void setSubscribedes(Set<User> subscribedes) {
        this.subscribedes = subscribedes;
    }

    public boolean addSubscriber(User subscriber) {
        if (subscribedes.contains(subscriber))
            return false;

        this.subscribedes.add(subscriber);
        return true;
    }

    public boolean removeSubscriber(User subscriber) {
        if (!subscribedes.contains(subscriber))
            return false;

        this.subscribedes.remove(subscriber);
        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return Objects.equals(getId(), event.getId()) && Objects.equals(getName(), event.getName()) && getType() == event.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getType());
    }
}
