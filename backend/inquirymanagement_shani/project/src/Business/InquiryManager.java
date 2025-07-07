package Business;

import Data.*;
import Exceptions.InquiryRunTimeException;
import HandleStoreFiles.HandleFiles;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static Data.Representative.getRepresentativeNextCodeVal;
import static Data.Representative.setRepresentativeNextCodeVal;

public class InquiryManager {
    public static Queue<Inquiry> queue = new LinkedList<>();
    private static final HandleFiles handleFiles = new HandleFiles();
    private static Integer nextCodeVal = 0;
    private boolean flag = true;

    public  static Queue<Representative> representativeList = new LinkedList<>();
    public  static Map<Inquiry, Representative> activeInquiriesMap = new HashMap<>();

    public static Queue<Inquiry> getQueue() {
        return queue;
    }

    public static Integer getNextCodeVal() {
        return nextCodeVal;
    }

    static {
        String currentDirectory = System.getProperty("user.dir");
        Path directoryPath = Paths.get(currentDirectory);
        List<String> subDirectories = Arrays.asList("Question", "Request", "Complaint");

        for (String subDir : subDirectories) {
            Path subDirectoryPath = directoryPath.resolve(subDir);
            File[] files = subDirectoryPath.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        createInquiryFromFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        loadRepresentativesFromFiles();
        updateNextCodeValFromHistory();
    }

    private static void createInquiryFromFile(File file) throws IOException {
        Inquiry inquiry = (Inquiry) handleFiles.readTxt(file);
        nextCodeVal++;
        queue.add(inquiry);
    }


    private static void loadRepresentativesFromFiles() {
        String directoryPath = "Representative";
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    Representative representative = (Representative) handleFiles.readCsv(file.getAbsolutePath());
                    representativeList.add(representative);
                    setRepresentativeNextCodeVal(getRepresentativeNextCodeVal() + 1);
                }
            }
        }
    }

    private static void updateNextCodeValFromHistory() {
        String directoryPath = "InquiryHistory";
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    nextCodeVal++;
                }
            }
        }
    }

    public void inquiryCreation() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (flag) {
            System.out.print("Enter 1 for Question, 2 for Request, 3 for Complaint, or any other key to exit: ");
            int code = Integer.parseInt(scanner.nextLine());
            Inquiry currentInquiry = createInquiryByCode(code);
            if (currentInquiry != null) {
                currentInquiry.setClassName(currentInquiry.getClass().getName());
                currentInquiry.fillDataByUser();
                currentInquiry.setCode(nextCodeVal++);
                try {
                    queue.add(currentInquiry);
                } catch (InquiryRunTimeException exception) {
                    throw new InquiryRunTimeException(--nextCodeVal, "שגיאה בשמירת הפניה " + exception.getMessage());
                }

                handleFiles.saveFile(currentInquiry);
            } else {
                flag = false;
            }
        }
    }

    private Inquiry createInquiryByCode(int code) {
        switch (code) {
            case 1:
                return new Question();
            case 2:
                return new Request();
            case 3:
                return new Complaint();
            default:
                return null;
        }
    }

    public void processInquiryManager() {
        for (Inquiry inquiry : queue) {
            Thread thread = new InquiryHandling();
            ((InquiryHandling) thread).setCurrentInquiry(inquiry);
            thread.start();
        }
    }

    public void defineRepresentative() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter representative name (or type 'exit' to finish): ");
            String name = scanner.nextLine();
            if (name.equalsIgnoreCase("exit")) {
                break;
            }
            System.out.print("Enter representative ID number: ");
            int id = Integer.parseInt(scanner.nextLine());
            Representative representative = new Representative(id, name);
            representativeList.add(representative);
            File directory = new File("Representative");
            if (!directory.exists()) {
                directory.mkdir();
            }
            handleFiles.saveCSV(representative, "Representative/" + representative.getCode() + ".csv");
        }
    }

    public Queue<Representative> getRepresentativeList() {
        return representativeList;
    }

    public void setRepresentativeList(Queue<Representative> representativeList) {
        InquiryManager.representativeList = representativeList;
    }

    public int addInquiryFromClient(Inquiry inquiry)  throws IOException {
        inquiry.setCode(nextCodeVal);
        inquiry.setStatus(InquiryStatus.OPEN);
        queue.add(inquiry);
        handleFiles.saveFile(inquiry);
        nextCodeVal++;
        return inquiry.getCode();
    }

    public boolean isRepresentativeActive(int code) {
        for (Representative representative : activeInquiriesMap.values()) {
            System.out.println(representative.getCode());
            if (representative.getId() == code) {
                return true;
            }
        }
        return false;
    }
    public List<Inquiry> getAllInquiriesByRepresentative(int code) {
        List<Inquiry> result = new ArrayList<>();
        HandleFiles handleFiles = new HandleFiles();

        File historyDir = new File("InquiryHistory");
        if (historyDir.exists() && historyDir.isDirectory()) {
            File[] files = historyDir.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    Object obj = handleFiles.readTxt(file);
                    if (obj instanceof Inquiry inquiry) {
                        Representative inquiryRep = inquiry.getRepresentative();
                        if (inquiryRep != null && inquiryRep.getId() == code) {
                            result.add(inquiry);
                        }
                    }
                }
            }
        }

        for (Map.Entry<Inquiry, Representative> entry : activeInquiriesMap.entrySet()) {
            Representative repFromMap = entry.getValue();
            if (repFromMap != null && repFromMap.getId() == code) {
                result.add(entry.getKey());
            }
        }

        return result;
    }

}
