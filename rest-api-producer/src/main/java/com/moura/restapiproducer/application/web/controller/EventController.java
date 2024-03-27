package com.moura.restapiproducer.application.web.controller;

import com.moura.restapiproducer.application.service.EventService;
import com.moura.restapiproducer.application.web.request.EventReq;
import com.moura.restapiproducer.application.web.response.EventResp;
import com.moura.restapiproducer.infra.model.Event;
import com.moura.restapiproducer.infra.model.EventType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResp>> getEvents() {
        List<EventResp> events = eventService.getEvents().stream()
                .map((event) -> new EventResp(event)).collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{type}")
    public ResponseEntity<EventResp> getEventByType(@PathVariable EventType type) {
        Event e = eventService.getEventByType(type);
        if (e != null)
            return ResponseEntity.ok(new EventResp(e));
        else
            return (ResponseEntity<EventResp>) ResponseEntity.notFound();
    }

    @PostMapping
    public ResponseEntity<EventResp> createEvent(@RequestBody @Valid EventReq event) {
        Event eventSaved = eventService.createEvent(event.toEntity());
        return ResponseEntity.created(
                URI.create("/api/v1/events/"+eventSaved.getType().name())
        ).body(new EventResp(eventSaved));
    }

    @PatchMapping("/{type}/add")
    public ResponseEntity<String> addSubscriber(
            @PathVariable EventType type,
            @RequestParam(value = "email") String email
    ) {
        boolean isAdded = eventService.addSubscriber(email, type);
        if (isAdded)
            return ResponseEntity.accepted().body("");
        else
            return ResponseEntity.badRequest().body("user is already subscribed to the event");
    }

    @PatchMapping("/{type}/remove")
    public ResponseEntity<String> removeSubscriber(
            @PathVariable EventType type,
            @RequestParam(value = "email") String email
    ) {
        boolean isRemoved = eventService.removeSubscriber(email, type);
        if (isRemoved)
            return ResponseEntity.accepted().body("");
        else
            return ResponseEntity.badRequest().body("user is not subscribed to the event");
    }

    @PutMapping("/{type}/notify")
    public ResponseEntity<String> notifySubscribedes(
            @PathVariable EventType type
    ) {
        boolean isNotifyStarted = eventService.notifySubscribedes(type);
        if (isNotifyStarted)
            return ResponseEntity.accepted().body("");
        else
            return ResponseEntity.internalServerError().body("unable to initiate subscriber notification process");
    }
}
