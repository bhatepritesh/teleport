package com.teleport.tracking.service;

import com.teleport.tracking.exception.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static com.teleport.tracking.constants.TrackingServiceConstants.*;

@Service
public class TrackingNumberServiceImpl implements TrackingService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final DatabaseClient databaseClient;

    @Value("${tracking.rate-limit}")
    private long rateLimit;

    public TrackingNumberServiceImpl(ReactiveStringRedisTemplate redisTemplate, DatabaseClient databaseClient) {
        this.redisTemplate = redisTemplate;
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<String> generateAndPersistTrackingNumber(String origin, String destination, String customerId, String customerName, String customerSlug, double weight, OffsetDateTime createdAt) {
        String rateKey = RATE_LIMIT_REDIS_KEY + customerId;

        return redisTemplate.opsForValue().increment(rateKey)
                .flatMap(rateCount -> {
                    if (rateCount == 1) {
                        redisTemplate.expire(rateKey, Duration.ofMinutes(1)).subscribe();
                    }
                    if (rateCount > rateLimit) {
                        return Mono.error(new RateLimitExceededException(RATE_LIMIT_EXCEEDED));
                    }
                    return generateTrackingNumberFromRedisCounter(origin, destination)
                            .flatMap(trackingNumber -> /*saveTrackingNumberToDatabase(trackingNumber, origin, destination, customerId, customerName, customerSlug, weight, createdAt)
                                    .thenReturn(trackingNumber)*/
                                    Mono.just(trackingNumber))
                            .onErrorResume(ex -> generateFallbackTrackingNumber(origin, destination)
                                    .flatMap(trackingNumber -> /*saveTrackingNumberToDatabase(trackingNumber, origin, destination, customerId, customerName, customerSlug, weight, createdAt)
                                            .thenReturn(trackingNumber)*/
                                            Mono.just(trackingNumber)));
                });
    }

    private Mono<String> generateTrackingNumberFromRedisCounter(String origin, String destination) {
        String key = TRACKING_COUNTER_REDIS_KEY;
        return redisTemplate.opsForValue().increment(key)
                .map(counter -> buildFormattedTrackingNumber(origin, destination, counter));
    }

    private Mono<String> generateFallbackTrackingNumber(String origin, String destination) {
        return Mono.fromCallable(() -> {
            long randomValue = ThreadLocalRandom.current().nextLong(1_000_000L);
            return buildFormattedTrackingNumber(origin, destination, randomValue);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private String buildFormattedTrackingNumber(String origin, String destination, long counter) {
        // Ensure total length ≤ 16 characters
        String base36 = encodeToBase36WithPadding(counter);
        return (origin + destination + base36).toUpperCase(Locale.ROOT);
    }

    private String encodeToBase36WithPadding(Long number) {
        String base36 = Long.toString(number, 36).toUpperCase(Locale.ROOT);
        return String.format("%6s", base36).replace(' ', '0');
    }


    private Mono<Void> saveTrackingNumberToDatabase(String trackingNumber, String origin, String destination, String customerId, String customerName, String customerSlug, double weight, OffsetDateTime createdAt) {
        return databaseClient.sql("INSERT INTO TELEPORT_SCHEMA.TRACKING_NUMBERS (TRACKING_NUMBER, ORIGIN_COUNTRY_ID, DESTINATION_COUNTRY_ID, CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_SLUG, WEIGHT, CREATED_AT) " +
                        "VALUES (:tracking_number, :origin, :destination, :customer_id, :customer_name, :customer_slug, :weight, :created_at)")
                .bind(TRACKING_NUMBER, trackingNumber)
                .bind(ORIGIN, origin)
                .bind(DESTINATION, destination)
                .bind(CUSTOMER_ID, customerId)
                .bind(CUSTOMER_NAME, customerName)
                .bind(CUSTOMER_SLUG, customerSlug)
                .bind(WEIGHT, weight)
                .bind(CREATED_AT, createdAt)
                .then();
    }
}
