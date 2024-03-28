package com.moura.consumer.application.service.dto;

public record EventKafkaConsume(
        String name,
        String description,
        String type
) {}