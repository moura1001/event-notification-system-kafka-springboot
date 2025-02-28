package com.moura.restapiproducer.application.web.response;

import com.moura.restapiproducer.infra.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record EventResp(
        Long id,
        String name,
        String description,
        String type,
        List<UserResp> subscribedes
) {
    public EventResp(Event event) {
        this(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getType().name(),
                new ArrayList<>(getSubscribedesSize(event))
        );

        if (event.getSubscribedes() != null && !event.getSubscribedes().isEmpty()) {
            List<UserResp> subscribedes = event.getSubscribedes().stream()
                    .map((user) -> new UserResp(user)).collect(Collectors.toList());
            this.subscribedes.addAll(subscribedes);
        }
    }

    private static int getSubscribedesSize(Event event) {
        return event.getSubscribedes() != null ? event.getSubscribedes().size() : 0;
    }
}
