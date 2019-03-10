package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Surface implements Serializable {

    private UUID id_surface;
    private BigDecimal area;
    private List<Position> coordinates;

    public Surface(BigDecimal area, List<Position> coordinates) {
        this.area = area;
        this.coordinates = coordinates;
    }

    public Surface(Integer area, List<Position> coordinates) {
        this.area = BigDecimal.valueOf(area);
        this.coordinates = coordinates;
    }

    public Surface(UUID id_surface, BigDecimal area, List<Position> coordinates) {
        this.id_surface = id_surface;
        this.area = area;
        this.coordinates = coordinates;
    }

    public UUID getId_surface() {
        return id_surface;
    }

    public void setId_surface(UUID id_surface) {
        this.id_surface = id_surface;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public List<Position> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Position> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "Surface{" +
                "id_surface=" + id_surface +
                ", area=" + area +
                '}';
    }
}
