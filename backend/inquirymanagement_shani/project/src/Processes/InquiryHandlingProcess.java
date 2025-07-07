package Processes;
import Data.*;
import Business.InquiryHandling;
import Business.InquiryManager;
import HandleStoreFiles.HandleFiles;

import java.util.Random;

import static Business.InquiryHandling.determineEstimationTime;
import static Business.InquiryManager.activeInquiriesMap;
import static Business.InquiryManager.representativeList;

public class InquiryHandlingProcess extends Thread{
    private final Inquiry inquiry;


    public InquiryHandlingProcess(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    @Override
    public void run() {
        try {
            System.out.println("בטיפול");
            InquiryHandling.handleInquiry(inquiry);
            System.out.println("שינוי סטטוס");
            inquiry.setStatus(InquiryStatus.ARCHIVED);
            HandleFiles.updateFile(inquiry);
            System.out.println("מועבר להיסטוריה");
            InquiryHandling.moveInquiryToHistory(inquiry);
            System.out.println("החזרת נציג לתור");
            Representative rep = activeInquiriesMap.get(inquiry);
            representativeList.add(rep);
            System.out.println("הסרת הנציג והפניה מהMAP");
            activeInquiriesMap.remove(inquiry);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


