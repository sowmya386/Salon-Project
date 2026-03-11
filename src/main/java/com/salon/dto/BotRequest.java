package com.salon.dto;

import jakarta.validation.constraints.NotBlank;

public class BotRequest {

    @NotBlank
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
