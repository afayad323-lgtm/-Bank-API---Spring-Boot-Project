package com.ahmed.bank_api.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateAccountRequest {
@NotBlank(message = "Owner name is required")
    private String ownerName;

    public String getOwnerName() {
        return ownerName;
    }


}
