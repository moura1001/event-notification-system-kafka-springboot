package com.moura.consumer.application.service.dto;

import java.util.List;

public record EventResp(
        Long id,
        String name,
        String description,
        String type,
        List<UserResp> subscribedes
) {}