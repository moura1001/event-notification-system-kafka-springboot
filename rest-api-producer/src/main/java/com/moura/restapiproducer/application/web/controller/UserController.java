package com.moura.restapiproducer.application.web.controller;

import com.moura.restapiproducer.application.service.UserService;
import com.moura.restapiproducer.application.web.request.UserReq;
import com.moura.restapiproducer.application.web.response.UserResp;
import com.moura.restapiproducer.infra.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResp>> getUsers() {
        List<UserResp> users = userService.getUsers().stream()
                .map((user) -> new UserResp(user)).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResp> getUserByEmail(@PathVariable String email) {
        User u = userService.getUserByEmail(email);
        if (u != null)
            return ResponseEntity.ok(new UserResp(u));
        else
            return (ResponseEntity<UserResp>) ResponseEntity.notFound();
    }

    @PostMapping
    public ResponseEntity<UserResp> createAvenger(@RequestBody @Valid UserReq user) {
        User userSaved = userService.createUser(user.toEntity());
        return ResponseEntity.created(
                URI.create("/api/v1/users/"+userSaved.getEmail())
        ).body(new UserResp(userSaved));
    }
}
