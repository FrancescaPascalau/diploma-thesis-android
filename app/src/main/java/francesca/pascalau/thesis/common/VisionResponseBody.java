package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.util.List;

public class VisionResponseBody implements Serializable {

    private List<VisionResponse> responses;

    public VisionResponseBody(List<VisionResponse> responses) {
        this.responses = responses;
    }

    public List<VisionResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<VisionResponse> responses) {
        this.responses = responses;
    }

    @Override
    public String toString() {
        return "VisionResponseBody{" +
                "responses=" + responses +
                '}';
    }
}

