package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class Type implements Serializable {
    private UUID id;
    private String description;
    private BigDecimal value;

    public Type(UUID id, String description, BigDecimal value) {
        this.id = id;
        this.description = description;
        this.value = value;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Type{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", value=" + value +
                '}';
    }
}
