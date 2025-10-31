package com.example.demo.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse {
    private boolean success;
    private String status;
    private String message;
    private Object data;
    public ApiResponse(boolean success, String status, String message, Object data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
    }
    public ApiResponse(boolean success, String status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
    }
}
