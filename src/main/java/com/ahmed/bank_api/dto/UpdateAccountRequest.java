package com.ahmed.bank_api.dto;
import jakarta.validation.Constraint.*;
import jakarta.validation.constraints.NotBlank;

public class UpdateAccountRequest {
    @NotBlank(message = "owner name is required")
    private String ownerName;

    public String getOwnerName() {
        return ownerName;
    }
}
