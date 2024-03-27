package com.moura.restapiproducer.application.service;

import com.moura.restapiproducer.infra.model.Event;

record EventKafkaProducer(
        String name,
        String description,
        String type
) {
    public EventKafkaProducer(Event event) {
        this(
                event.getName(),
                event.getDescription(),
                event.getType().name()
        );
    }
}
