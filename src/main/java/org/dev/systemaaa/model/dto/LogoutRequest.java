package org.dev.systemaaa.model.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}