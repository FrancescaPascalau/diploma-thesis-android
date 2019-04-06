package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.util.List;

public class VisionRequest implements Serializable {

    private List<VisionFeature> features;
    private VisionImage image;

    public VisionRequest(List<VisionFeature> features, VisionImage image) {
        this.features = features;
        this.image = image;
    }

    public List<VisionFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<VisionFeature> features) {
        this.features = features;
    }

    public VisionImage getImage() {
        return image;
    }

    public void setImage(VisionImage image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "VisionRequest{" +
                "features=" + features +
                ", image=" + image +
                '}';
    }
}
