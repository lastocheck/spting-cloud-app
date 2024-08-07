package com.example.util.http;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@NoArgsConstructor(force = true)
@Getter
public class HttpErrorInfo {
    public final ZonedDateTime timestamp;
    public final String path;
    public final HttpStatus httpStatus;
    public final String message;

    public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
        this.timestamp = ZonedDateTime.now();
        this.path = path;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
