package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.util.List;

public class SurfaceFirebase implements Serializable {

    private String idSurface;
    private double area;
    private double price;
    private List<Position> coordinates;
    private TypeFirebase type;

    private SurfaceFirebase() {
    }

    public static SurfaceFirebase fromSurface(Surface surface) {
        SurfaceFirebase surfaceFirebase = new SurfaceFirebase();
        surfaceFirebase.setIdSurface(surface.getIdSurface().toString());
        surfaceFirebase.setArea(surface.getArea().doubleValue());
        surfaceFirebase.setPrice(surface.getPrice().doubleValue());
        surfaceFirebase.setCoordinates(surface.getCoordinates());
        surfaceFirebase.setType(TypeFirebase.fromType(surface.getType()));
        return surfaceFirebase;
    }

    public String getIdSurface() {
        return idSurface;
    }

    public void setIdSurface(String idSurface) {
        this.idSurface = idSurface;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Position> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Position> coordinates) {
        this.coordinates = coordinates;
    }

    public TypeFirebase getType() {
        return type;
    }

    public void setType(TypeFirebase type) {
        this.type = type;
    }
}
