package com.teleport.tracking.service;

import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public interface TrackingService {

    Mono<String> generateAndPersistTrackingNumber(String origin_country_id, String destination_country_id, String customer_id, String customer_name,
                                                  String customer_slug, double weight, OffsetDateTime created_at);
}
