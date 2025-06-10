package com.teleport.tracking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TrackingRequest {

    @NotBlank(message = "origin_country_id is required")
    private String origin_country_id;

    @NotBlank(message = "destination_country_id is required")
    private String destination_country_id;

    @DecimalMin(value = "0.001", message = "weight must be greater than 0")
    @NotNull(message = "weight is required")
    private Double weight;

    private String created_at;

    @NotNull(message = "customer_id is required")
    private String customer_id;

    @NotBlank(message = "customer_name is required")
    private String customer_name;

    @NotBlank(message = "customer_slug is required")
    private String customer_slug;
}
