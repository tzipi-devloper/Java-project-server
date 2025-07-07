package Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Complaint extends Inquiry implements Serializable {
    private String assignedBranch;

    public String getAssignedBranch() {
        return assignedBranch;
    }

    private void setAssignedBranch(String s) {
        assignedBranch=s;
    }
    public Complaint(Integer code, String description, LocalDateTime creationDate, List<String> documents, String className, String assignedBranch) {
        super(code, description, creationDate, documents, className);
        this.assignedBranch = assignedBranch;
    }


    public Complaint(String assignedBranch) {
        this.assignedBranch = assignedBranch;
    }

    public Complaint(){}

    @Override
    public void fillDataByUser() {
        super.fillDataByUser();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your assigned branch: ");
        this.setAssignedBranch(scanner.nextLine());
    }

    @Override
    public String getData() {
        return super.getData() + "\n assignedBranch: " + (assignedBranch != null ? assignedBranch : "");
    }

    @Override
    public void handling() {
        int estimatedTime = getEstimatedTime();
        System.out.println("Handling Complaint with code " + this.getCode() + ". Estimated time: " + estimatedTime + " seconds.");
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
        return (int) (Math.random() * 21) + 20;
    }

    @Override
    public String getFolderName() {
        return "Complaint";
    }
}

