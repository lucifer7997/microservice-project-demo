package com.example.authservice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class ApiResponse {
    private int code;
    private String message;
    private Object data;

    public ApiResponse(HttpStatus httpStatus, String message, Object data) {
        this.code = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    public ApiResponse(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.message = message;
    }
}
