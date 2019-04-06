package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.util.List;

public class VisionRequestBody implements Serializable {

    private List<VisionRequest> requests;

    public VisionRequestBody(List<VisionRequest> requests) {
        this.requests = requests;
    }

    public List<VisionRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<VisionRequest> requests) {
        this.requests = requests;
    }

    @Override
    public String toString() {
        return "VisionRequestBody{" +
                "requests=" + requests +
                '}';
    }
}
