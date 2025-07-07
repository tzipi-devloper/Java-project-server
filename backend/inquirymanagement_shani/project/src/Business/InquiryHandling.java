package Business;
import Data.*;
import HandleStoreFiles.HandleFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static Business.InquiryManager.*;
import static Data.InquiryStatus.ARCHIVED;

public class InquiryHandling extends Thread {
    private static Inquiry currentInquiry;


    public Inquiry getCurrentInquiry() {
        return currentInquiry;
    }

    public void setCurrentInquiry(Inquiry currentInquiry) {
        this.currentInquiry = currentInquiry;
    }

    private static final HandleFiles handleFiles = new HandleFiles();

    public void createInquiry() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter 1 for Question, 2 for Request, 3 for Complaint: ");
        int code = Integer.parseInt(scanner.nextLine());
        switch (code) {
            case 1:
                setCurrentInquiry(new Question());
                this.setPriority(MAX_PRIORITY);
                break;
            case 2:
                setCurrentInquiry(new Request());
                break;
            case 3:
                setCurrentInquiry(new Complaint());
                break;
            default:
                setCurrentInquiry(null);
                break;
        }
    }

    @Override
    public void run() {
        if (currentInquiry != null) {
            handleInquiry(currentInquiry);
        }
    }

    public static void handleInquiry(Inquiry inquiry) {
        Random rand = new Random();
        int estimationTime = determineEstimationTime(rand,inquiry);
        try {
            Thread.sleep(estimationTime * 1000);
            inquiry.handling();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



    public static int determineEstimationTime(Random rand,Inquiry inquiry) {
        if (inquiry instanceof Question) {
            return rand.nextInt(5) + 1;
        } else if (inquiry instanceof Complaint) {
            return rand.nextInt(21) + 20;
        } else if (inquiry instanceof Request) {
            return rand.nextInt(6) + 10;
        }
        return 0;
    }

    public static void moveInquiryToHistory(Inquiry inquiry) throws IOException {
      //יוצר או פותח תקיית היסטוריה
        File historyDir = new File("InquiryHistory");
        if (!historyDir.exists()) {
            historyDir.mkdir();
        }
        System.out.println(historyDir.isDirectory());
        // שומר את האובייקט לתיקיית ההיסטוריה
        handleFiles.saveFile(inquiry,historyDir.getPath());
        // מוחק את הפניה מהתקיה המקורית

       handleFiles.deleteFile(inquiry);
        System.out.println("Inquiry deleted : " + inquiry.getFileName()+".txt");

        System.out.println("Inquiry moved to history: " + inquiry.getFileName()+".txt");
    }

    public static void updateStatusInFile(Inquiry inquiry, InquiryStatus newStatus) {
        try {
            File file = new File(inquiry.getFolderName(), inquiry.getFileName() + ".txt");
            if (!file.exists()) {
                System.out.println("File not found: " + file.getPath());
                return;
            }
            Object obj = handleFiles.readTxt(file);
            if (!(obj instanceof Inquiry)) {
                System.out.println("File does not contain a valid Inquiry object.");
                return;
            }
            Inquiry existingInquiry = (Inquiry) obj;
            existingInquiry.setStatus(newStatus);
            handleFiles.saveFile(existingInquiry);
            System.out.println("Status updated successfully in file: " + file.getPath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelInquiry(int code){
        Inquiry inquiry=getInquiryByCode(code);

        if(inquiry != null)
        {
           if(inquiry.getStatus()==InquiryStatus.IN_PROGRESS)
           {
               activeInquiriesMap.remove(inquiry);
               Representative rep = activeInquiriesMap.get(inquiry);
               representativeList.add(rep);
           }
           inquiry.setStatus(InquiryStatus.CANCELLED);
           handleFiles.saveFile(inquiry);
            try {
                moveInquiryToHistory(inquiry);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("code");
    }

    public static Inquiry getInquiryByCode(int code){

        for (Inquiry inquiry : InquiryManager.activeInquiriesMap.keySet()) {
            if (inquiry.getCode() == code) {
                return inquiry;
            }
        }
        String currentDirectory = System.getProperty("user.dir");
        Path directoryPath = Paths.get(currentDirectory);
        List<String> subDirectories = Arrays.asList("Question", "Request", "Complaint" , "InquiryHistory");

        for (String subDir : subDirectories) {
            Path subDirectoryPath = directoryPath.resolve(subDir);
            File[] files = subDirectoryPath.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    Inquiry inquiry = (Inquiry) handleFiles.readTxt(file);
                    if(inquiry.getCode()==code)
                        return inquiry;
                }
            }
        }
        return null ;
    }

    public InquiryStatus getInquiryStatusByCode(int code){
        Inquiry inquiry = getInquiryByCode(code);
        return inquiry.getStatus();
    }
    public static String getRepresentativeNameByInquiryCode(int code) {
        Inquiry inquiry = getInquiryByCode(code);
        if (inquiry != null && inquiry.getRepresentative() != null) {
            return inquiry.getRepresentative().getName();
        }
        return null;
    }
}

