package com.ijse.smartqueue.util;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class APIResponse<T> {

    private int status;
    private String message;
    private T data;

    public APIResponse(String message, T data) {
        this.status = 200;
        this.message = message;
        this.data = data;
    }
}
