package com.teleport.tracking.controller;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/redis")
public class RedisTestController {

    private final ReactiveStringRedisTemplate reactiveRedisTemplate;

    public RedisTestController(ReactiveStringRedisTemplate reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @GetMapping("/ping")
    public Mono<ResponseEntity<Map<String, String>>> ping() {
        return reactiveRedisTemplate
                .getConnectionFactory()
                .getReactiveConnection()
                .ping()
                .map(pong -> ResponseEntity.ok(Map.of("ping", pong)))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(500).body(
                                Map.of("error", "Redis ping failed", "details", e.getMessage())
                        )
                ));
    }

}




