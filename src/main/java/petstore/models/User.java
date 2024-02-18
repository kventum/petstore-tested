package petstore.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {
    Long id;
    String username;
    String firstName;
    String lastName;
    String email;
    String password;
    String phone;
    int userStatus;
}
