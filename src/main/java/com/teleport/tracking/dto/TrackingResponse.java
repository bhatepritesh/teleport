package com.teleport.tracking.dto;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class TrackingResponse {

    public String tracking_number;
    public String created_at;

    public TrackingResponse(String tracking_number, OffsetDateTime created_at) {
        this.tracking_number = tracking_number;
        this.created_at = created_at.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
