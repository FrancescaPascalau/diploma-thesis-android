package francesca.pascalau.thesis.common;

import java.io.Serializable;
import java.math.BigDecimal;

public class VisionLabelAnnotation implements Serializable {

    private String mid;
    private String description;
    private BigDecimal score;
    private BigDecimal topicality;

    public VisionLabelAnnotation(String mid, String description, BigDecimal score, BigDecimal topicality) {
        this.mid = mid;
        this.description = description;
        this.score = score;
        this.topicality = topicality;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getTopicality() {
        return topicality;
    }

    public void setTopicality(BigDecimal topicality) {
        this.topicality = topicality;
    }

    @Override
    public String toString() {
        return "VisionLabelAnnotation{" +
                "mid='" + mid + '\'' +
                ", description='" + description + '\'' +
                ", score=" + score +
                ", topicality=" + topicality +
                '}';
    }
}
