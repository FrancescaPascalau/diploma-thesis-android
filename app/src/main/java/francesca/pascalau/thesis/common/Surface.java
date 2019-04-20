package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Surface implements Serializable {

    private UUID idSurface;
    private BigDecimal area;
    private BigDecimal price;
    private List<Position> coordinates;
    private Type type;

    public Surface(BigDecimal area, List<Position> coordinates) {
        this.area = area;
        this.coordinates = coordinates;
    }

    public Surface(Integer area, List<Position> coordinates) {
        this.area = BigDecimal.valueOf(area);
        this.coordinates = coordinates;
    }

    public Surface(UUID idSurface, BigDecimal area, List<Position> coordinates, Type type) {
        this.idSurface = idSurface;
        this.area = area;
        this.coordinates = coordinates;
        this.type = type;
    }

    public Surface(UUID idSurface, BigDecimal area, BigDecimal price, List<Position> coordinates, Type type) {
        this.idSurface = idSurface;
        this.area = area;
        this.price = price;
        this.coordinates = coordinates;
        this.type = type;
    }

    public Surface(UUID idSurface, BigDecimal area, List<Position> coordinates) {
        this.idSurface = idSurface;
        this.area = area;
        this.coordinates = coordinates;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public UUID getIdSurface() {
        return idSurface;
    }

    public void setIdSurface(UUID idSurface) {
        this.idSurface = idSurface;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
                "idSurface=" + idSurface +
                ", area=" + area +
                ", price=" + price +
                ", type=" + type +
                '}';
    }
}
