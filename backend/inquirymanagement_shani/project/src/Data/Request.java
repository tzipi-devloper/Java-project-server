package Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Request extends Inquiry implements Serializable {

    public Request(Integer code, String description, LocalDateTime creationDate, List<String> documents, String className){
       super(code,description,creationDate,documents,className);
    }

    public Request(){}

    @Override
    public void handling() {
        int estimatedTime = getEstimatedTime();
        System.out.println("Handling Request with code " + this.getCode() + ". Estimated time: " + estimatedTime + " seconds.");
        try {
            Thread.sleep(estimatedTime * 1000);
            if (estimatedTime > 5 && Thread.activeCount() > 10) {
                Thread.yield();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getEstimatedTime() {
        return (int) (Math.random() * 6) + 10; // 10-15 seconds
    }

    @Override
    public String getFolderName() {
        return "Request";
    }
}
