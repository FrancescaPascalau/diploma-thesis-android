package francesca.pascalau.thesis.common;

import java.io.Serializable;

public class TypeFirebase implements Serializable {

    private String id;
    private String description;
    private double value;

    private TypeFirebase() {
    }

    public static TypeFirebase fromType(Type type) {
        TypeFirebase typeFirebase = new TypeFirebase();
        typeFirebase.setId(type.getId().toString());
        typeFirebase.setDescription(type.getDescription());
        typeFirebase.setValue(type.getValue().doubleValue());
        return typeFirebase;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
