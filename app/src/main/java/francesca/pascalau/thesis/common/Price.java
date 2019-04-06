package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class Price implements Serializable {
    private UUID idPrice;
    private String type;
    private BigDecimal value;

    public Price(UUID idPrice, String type, BigDecimal value) {
        this.idPrice = idPrice;
        this.type = type;
        this.value = value;
    }

    public UUID getIdPrice() {
        return idPrice;
    }

    public void setIdPrice(UUID idPrice) {
        this.idPrice = idPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Price{" +
                "idPrice=" + idPrice +
                ", type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}
