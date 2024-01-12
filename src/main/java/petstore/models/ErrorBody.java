package petstore.models;

import lombok.Getter;

@Getter
public class ErrorBody {
    private short code;
    private String type;
    private String message;
}
