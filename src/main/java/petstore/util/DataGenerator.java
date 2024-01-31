package petstore.util;

import petstore.models.pets.Category;
import petstore.models.pets.Pet;
import petstore.models.pets.PetStatus;

import java.util.List;

public class DataGenerator {

    public static Pet getPet(String name, String categoryName, String photoUrl, String tagName, PetStatus status) {
        return Pet.builder()
                .id((long) (Math.random()*10000))
                .category(new Category(categoryName, ((long)(Math.random()*10000))))
                .name(name)
                .photoUrls(new String[]{photoUrl})
                .tags(List.of(new Category(tagName, (long)(Math.random()*1000))))
                .status(status)
                .build();
    }

    public static Pet getPet(String name, String photoUrl) {
        return Pet.builder()
                .id((long) (Math.random()*10000))
                .name(name)
                .photoUrls(new String[]{photoUrl})
                .build();
    }

    public static Pet getPet(Long id, String name, Long categoryId, String categoryName, String photoUrl,Long tagId, String tagName, PetStatus status) {
        return Pet.builder()
                .id(id)
                .category(new Category(categoryName, categoryId))
                .name(name)
                .photoUrls(new String[]{photoUrl})
                .tags(List.of(new Category(tagName, tagId)))
                .status(status)
                .build();
    }

    public static Pet getPet(Long id, String name, String photoUrl) {
        return Pet.builder()
                .id(id)
                .name(name)
                .photoUrls(new String[]{photoUrl})
                .build();
    }

    public static Pet getPet(Long id) {
        return Pet.builder()
                .id(id)
                .build();
    }
}
