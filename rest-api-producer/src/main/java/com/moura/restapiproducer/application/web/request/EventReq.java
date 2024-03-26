package com.moura.restapiproducer.application.web.request;

import com.moura.restapiproducer.infra.model.Event;
import com.moura.restapiproducer.infra.model.EventType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EventReq(
        @NotEmpty @Size(min = 4, max = 512) String name,
        @NotEmpty @Size(min = 8, max = 2048) String description,
        @NotNull EventType type
) {
    public Event toEntity() {
        return new Event(
                this.name,
                this.description,
                this.type
        );
    }
}
