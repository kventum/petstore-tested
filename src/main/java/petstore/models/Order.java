package petstore.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import petstore.util.DateTimeDeserializer;

import java.time.OffsetDateTime;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Order {
    private Long id;
    private Long petId;
    private Integer quantity;
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private OffsetDateTime shipDate;
    private OrderStatus status;
    private boolean complete;
}

