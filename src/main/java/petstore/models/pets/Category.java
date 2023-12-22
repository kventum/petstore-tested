package petstore.models.pets;

import lombok.Getter;

@Getter
public class Category {
    private Long id;
    private String name;

    public Category(String name, Long  id) {
        this.id = id;
        this.name = name;
    }

    public Category() {
    }
}
