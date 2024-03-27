package com.moura.restapiproducer.application.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moura.restapiproducer.application.web.request.EventReq;
import com.moura.restapiproducer.application.web.request.UserReq;
import com.moura.restapiproducer.infra.model.EventType;
import com.moura.restapiproducer.infra.repository.EventRepository;
import com.moura.restapiproducer.infra.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventControllerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void init() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldBeInitiateNotificationOfSubscribedesSuccessfully() throws Exception  {
        // Register 2 users
        UserReq userDTO = new UserReq("email@email.com", "Genival Moura");
        String valueAsString = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDTO.email()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDTO.name()));

        UserReq userDTO2 = new UserReq("user@email.com", "Random User");
        valueAsString = objectMapper.writeValueAsString(userDTO2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDTO2.email()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDTO2.name()));

        // Register event
        EventReq eventDTO = new EventReq("News", "description...", EventType.NEWS);
        valueAsString = objectMapper.writeValueAsString(eventDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(eventDTO.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(eventDTO.description()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(eventDTO.type().name()));

        // Add 2 subscribers
        mockMvc.perform(MockMvcRequestBuilders.patch(
                "/api/v1/events/"+ eventDTO.type().name() +"/add?email="+userDTO.email()))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        mockMvc.perform(MockMvcRequestBuilders.patch(
                        "/api/v1/events/"+ eventDTO.type().name() +"/add?email="+userDTO2.email()))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        // Remove Random User subscriber
        mockMvc.perform(MockMvcRequestBuilders.patch(
                        "/api/v1/events/"+ eventDTO.type().name() +"/remove?email="+userDTO2.email()))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/events/"+ eventDTO.type().name()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribedes", Matchers.hasSize(1)));

        // Notify subscribedes
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/events/"+ eventDTO.type().name()+"/notify"))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

    }
}