package com.teleport.tracking.controller;

import com.teleport.tracking.dto.TrackingRequest;
import com.teleport.tracking.dto.TrackingResponse;
import com.teleport.tracking.service.TrackingService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Set;

@Validated
@RestController
@RequestMapping("/v1")
public class TrackingController {

    private TrackingService trackingService;
    private final Validator validator;

    public TrackingController(TrackingService trackingService, Validator validator) {
        this.trackingService = trackingService;
        this.validator = validator;
    }

    @GetMapping("/next-tracking-number")
    public Mono<TrackingResponse> getNextTrackingNumber(
            @ModelAttribute TrackingRequest request
    ) {
        Set<ConstraintViolation<TrackingRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        OffsetDateTime createdAt = request.getCreated_at() != null
                ? OffsetDateTime.parse(request.getCreated_at())
                : OffsetDateTime.now();
        return trackingService.generateAndPersistTrackingNumber(
                request.getOrigin_country_id(),
                request.getDestination_country_id(),
                request.getCustomer_id(),
                request.getCustomer_name(),
                request.getCustomer_slug(),
                request.getWeight(),
                createdAt
        ).map(trackingNumber -> new TrackingResponse(trackingNumber, createdAt));
    }
}
