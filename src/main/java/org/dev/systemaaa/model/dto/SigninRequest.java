package org.dev.systemaaa.model.dto;

import lombok.Data;

@Data
public class SigninRequest {
    private String username;
    private String email;
    private String password;
}
