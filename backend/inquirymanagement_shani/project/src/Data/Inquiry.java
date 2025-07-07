package Data;

import Exceptions.InquiryException;
import Exceptions.InquiryRunTimeException;
import HandleStoreFiles.IForSaving;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Inquiry implements IForSaving, Serializable{
    private Integer code;
    private String description;
    private LocalDateTime creationDate;
    private List <String> documents = new ArrayList<>();
    private String className;
    private InquiryStatus status;
    private Representative representative;

    public Representative getRepresentative() {
        return representative;
    }
    public void setRepresentative(Representative representative) {
        this.representative = representative;
    }
    public InquiryStatus getStatus() {
        return status;
    }
    public void setStatus(InquiryStatus status) {
        this.status = status;
    }

    public Inquiry(Integer code, String description, LocalDateTime creationDate, List<String> documents, String className){
        this.code=code;
        this.description=description;
        this.creationDate=creationDate;
        this.documents=documents;
        this.className=className;
        status = InquiryStatus.OPEN;
    }
    public Inquiry() {
        this.creationDate = LocalDateTime.now();
    }

    public void fillDataByUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter description: ");
        this.description = scanner.nextLine();

        if (this.description == null || this.description.isEmpty()) {
            throw new InquiryRunTimeException(this.code, "Description cannot be empty");
        }

        String input;
        System.out.println("Enter documents (type '0' to finish):");
        while (true) {
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("0")) {
                break;
            }
            documents.add(input);
        }
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String getData() {
        StringBuilder sb = new StringBuilder();
        sb.append("className: ").append(this.getClass().getSimpleName()).append("\n");
        sb.append("folderName: ").append(getFolderName()).append("\n");
        sb.append("code: ").append(getCode()).append("\n");
        sb.append("creationDate: ").append(getCreationDate()).append("\n");
        sb.append("description: ").append(getDescription()).append("\n");
        sb.append("documents: ").append(getDocuments().toString()).append("\n");
        sb.append("status: ").append(getStatus()).append("\n");

        // חשוב - שמירת נתוני נציג
        if (representative != null) {
            sb.append("representativeId: ").append(representative.getId()).append("\n");
            sb.append("representativeName: ").append(representative.getName()).append("\n");
        } else {
            sb.append("representativeId: \n");
            sb.append("representativeName: \n");
        }

        return sb.toString();
    }


    public void parseFromFile(List<String> values) {
        this.className = values.get(0).trim();
        this.code = Integer.parseInt(values.get(1).trim());
        this.description = values.get(2).trim();
        this.creationDate = LocalDateTime.parse(values.get(3).trim());
        //this.files = values.size() > 4 && !values.get(4).trim().isEmpty() ? Arrays.asList(values.get(4).trim().split(" ,")) : new ArrayList<>();


    }

    public abstract void handling();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public String getFileName() {
        return String.valueOf(code);
    }

    public abstract String getFolderName();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "Inquiry{" +
                "code=" + code +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                ", documents=" + documents +
                ", className='" + className + '\'' +
                ", status=" + status +
                ", representative=" + representative +
                '}';
    }
}

