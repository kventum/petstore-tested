package petstore.models;

import lombok.Getter;

@Getter
public class ApiResponse {
    private short code;
    private String type;
    private String message;
}
