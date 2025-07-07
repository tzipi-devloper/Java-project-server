package Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Question extends Inquiry implements Serializable {

    public Question(Integer code, String description, LocalDateTime creationDate, List<String> documents, String className)
    {
        super(code,description,creationDate,documents,className);
    }
    public Question(){}

    @Override
    public void handling() {
        int estimatedTime = getEstimatedTime();
        System.out.println("Handling Question with code " + this.getCode() + ". Estimated time: " + estimatedTime + " seconds.");
        try {
            Thread.sleep(estimatedTime * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getEstimatedTime() {
        return (int) (Math.random() * 5) + 1; // 1-5 seconds
    }

    @Override
    public String getFolderName() {
        return "Question";
    }

}

