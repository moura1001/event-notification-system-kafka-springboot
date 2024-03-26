package com.moura.restapiproducer.application.web.request;

import com.moura.restapiproducer.infra.model.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserReq(
        @NotEmpty @Size(min = 8, max = 128) String email,
        @NotEmpty @Size(min = 3, max = 512) String name
) {
    public User toEntity() {
        return new User(
                this.email,
                this.name
        );
    }
}
