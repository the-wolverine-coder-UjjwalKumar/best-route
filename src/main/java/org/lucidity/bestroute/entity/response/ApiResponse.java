package org.lucidity.bestroute.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    public ApiResponse(T data) {

        if (data == null) {
            this.status = 204;
            this.message = "NO_CONTENT";
        } else {
            this.data = data;
            this.status = 200;
            this.message = "OK";
        }

    }
    public ApiResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }


}
