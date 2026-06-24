package com.ahmed.bank_api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timeStamp;
public ErrorResponse(int status,String message , LocalDateTime timeStamp){
    this.status = status;
    this.message = message;
    this.timeStamp = timeStamp;
}

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
}
