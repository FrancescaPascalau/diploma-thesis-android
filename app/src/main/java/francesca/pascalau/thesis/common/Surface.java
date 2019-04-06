package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Surface implements Serializable {

    private UUID idSurface;
    private BigDecimal area;
    private List<Position> coordinates;
    private Price price;

    public Surface(BigDecimal area, List<Position> coordinates) {
        this.area = area;
        this.coordinates = coordinates;
    }

    public Surface(Integer area, List<Position> coordinates) {
        this.area = BigDecimal.valueOf(area);
        this.coordinates = coordinates;
    }

    public Surface(UUID idSurface, BigDecimal area, List<Position> coordinates, Price price) {
        this.idSurface = idSurface;
        this.area = area;
        this.coordinates = coordinates;
        this.price = price;
    }

    public Surface(UUID idSurface, BigDecimal area, List<Position> coordinates) {
        this.idSurface = idSurface;
        this.area = area;
        this.coordinates = coordinates;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
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
                '}';
    }
}
