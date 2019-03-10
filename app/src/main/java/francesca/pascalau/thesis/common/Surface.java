package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class Surface implements Serializable {

    private UUID id;
    private BigDecimal area;
    private Set<Position> coordinates;

    public Surface(BigDecimal area, Set<Position> coordinates) {
        this.id = UUID.randomUUID();
        this.area = area;
        this.coordinates = coordinates;
    }

    public Surface(Integer area, Set<Position> coordinates) {
        this.id = UUID.randomUUID();
        this.area = BigDecimal.valueOf(area);
        this.coordinates = coordinates;
    }

    public Surface(UUID id, BigDecimal area, Set<Position> coordinates) {
        this.id = id;
        this.area = area;
        this.coordinates = coordinates;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public Set<Position> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Set<Position> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "Surface{" +
                "id=" + id +
                ", area=" + area +
                '}';
    }
}
