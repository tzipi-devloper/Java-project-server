package Processes;
import Business.InquiryManager;
import Data.Inquiry;
import Data.InquiryStatus;
import Data.Representative;
import HandleStoreFiles.HandleFiles;

import java.util.Queue;
public class InquiryProcess extends Thread {
    private InquiryManager inquiryManager;
    public InquiryProcess(InquiryManager inquiryManager) {
        this.inquiryManager = inquiryManager;
    }
    @Override
    public void run() {
        while (true) {
            try {
                connectToRepresentative();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    private void connectToRepresentative() {
        Queue<Inquiry> inquiryQueue = inquiryManager.getQueue();
        Queue<Representative> representativeQueue = inquiryManager.representativeList;
        if (!inquiryQueue.isEmpty() && !representativeQueue.isEmpty()) {
            Inquiry inquiry = inquiryQueue.poll();
            Representative representative = representativeQueue.poll();
            if (inquiry != null && representative != null) {
                inquiry.setStatus(InquiryStatus.IN_PROGRESS);
                inquiry.setRepresentative(representative);
                HandleFiles handleFiles=new HandleFiles();
                handleFiles.saveFile(inquiry);
                saveInMap(inquiry, representative);
                System.out.println("הוקצה נציג " + representative.getName() + " לפנייה #" + inquiry.getCode());
                InquiryHandlingProcess handlingProcess = new InquiryHandlingProcess(inquiry);
                handlingProcess.start();
            }
        }
    }
    private void saveInMap(Inquiry inquiry, Representative representative) {
        inquiryManager.activeInquiriesMap.put(inquiry, representative);
    }
}