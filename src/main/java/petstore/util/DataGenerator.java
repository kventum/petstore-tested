package petstore.util;

import petstore.models.Order;
import petstore.models.OrderStatus;
import petstore.models.User;
import petstore.models.pets.Category;
import petstore.models.pets.Pet;
import petstore.models.pets.PetStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

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

    public static Order getOrder(long petId, int quantity, OffsetDateTime shipDate, OrderStatus status, boolean complete) {
        return Order.builder()
                .id((long) (Math.random() * 100000))
                .petId(petId)
                .quantity(quantity)
                .shipDate(shipDate)
                .status(status)
                .complete(complete)
                .build();
    }

    public static Order getOrder(long petId, boolean complete) {
        return Order.builder()
                .id((long) (Math.random() * 100000))
                .petId(petId)
                .complete(complete)
                .build();
    }

    public static Order getOrder(int quantity, OrderStatus status, boolean complete) {
        return Order.builder()
                .id((long) (Math.random() * 100000))
                .quantity(quantity)
                .status(status)
                .complete(complete)
                .build();
    }

    public static User getUser(int length, int status) {
        return User.builder()
                .id((long) (Math.random() * 10000))
                .username(random(length, true, true))
                .firstName(randomAlphabetic(4, 8))
                .lastName(randomAlphabetic(4, 8))
                .email(randomAlphabetic(6, 10) + "@" + randomAlphabetic(2, 5) + ".com")
                .password(random(8, true, true))
                .phone("+" + random(11, false, true))
                .userStatus(status)
                .build();
    }

    public static User getUser(int usernameLength, int firstNameLength, int lastNameLength, int phoneLength, int status) {
        return User.builder()
                .id((long) (Math.random() * 10000))
                .username(random(usernameLength, true, true))
                .firstName(randomAlphabetic(firstNameLength))
                .lastName(randomAlphabetic(lastNameLength))
                .email(randomAlphabetic(6, 10) + "@" + randomAlphabetic(2, 5) + ".com")
                .password(random(8, true, true))
                .phone(random(phoneLength, false, true))
                .userStatus(status)
                .build();
    }

    public static User getUser(String username, String firstName, String lastName, String email, String password,
                               String phone, int status) {
        return User.builder()
                .id((long) (Math.random() * 10000))
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .phone(phone)
                .userStatus(status)
                .build();
    }

    public static List<User> getUsers(int number) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            users.add(getUser(6, 0));
        }
        return users;
    }
}
