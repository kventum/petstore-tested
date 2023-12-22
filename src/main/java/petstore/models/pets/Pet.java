package petstore.models.pets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Pet {

    private Long id;
    private Category category;
    private String name;
    private String[] photoUrls;
    private List<Category> tags;
    private PetStatus status;

}
