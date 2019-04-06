package francesca.pascalau.thesis.common;

import java.io.Serializable;

public class VisionFeature implements Serializable {

    private int maxResults;
    private String type;

    public VisionFeature(int maxResults, String type) {
        this.maxResults = maxResults;
        this.type = type;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "VisionFeature{" +
                "maxResults=" + maxResults +
                ", type='" + type + '\'' +
                '}';
    }
}
