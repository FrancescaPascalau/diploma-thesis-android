package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.util.List;

public class VisionResponse implements Serializable {

    private List<VisionLabelAnnotation> labelAnnotations;

    public VisionResponse(List<VisionLabelAnnotation> labelAnnotations) {
        this.labelAnnotations = labelAnnotations;
    }

    public List<VisionLabelAnnotation> getLabelAnnotations() {
        return labelAnnotations;
    }

    public void setLabelAnnotations(List<VisionLabelAnnotation> labelAnnotations) {
        this.labelAnnotations = labelAnnotations;
    }

    @Override
    public String toString() {
        return "VisionResponse{" +
                "labelAnnotations=" + labelAnnotations +
                '}';
    }
}
