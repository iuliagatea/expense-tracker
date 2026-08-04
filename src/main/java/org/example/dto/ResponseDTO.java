package org.example.dto;

import lombok.Data;

@Data
public class ResponseDTO {
    private Boolean success;
    private String message;

    public ResponseDTO(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
