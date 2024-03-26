package com.moura.restapiproducer.application.web.response;

import com.moura.restapiproducer.infra.model.User;

public record UserResp(
        String email,
        String name
) {
    public UserResp(User user) {
        this(
                user.getEmail(),
                user.getName()
        );
    }
}
