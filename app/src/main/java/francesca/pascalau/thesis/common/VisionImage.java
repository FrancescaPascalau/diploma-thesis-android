package francesca.pascalau.thesis.common;

import java.io.Serializable;

public class VisionImage implements Serializable {

    private String content;

    public VisionImage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
