import Business.InquiryManager;
import ClientServer.Server.InquiryManagerServer;
import Data.Inquiry;
import Data.Question;
import Data.Request;
import HandleStoreFiles.HandleFiles;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception{
        System.out.println("main");

        HandleFiles handleFiles = new HandleFiles();
        Inquiry r1 = new Request(9, "בקשה לשדרוג 4", LocalDateTime.now(), List.of(), "Request");

        Inquiry q1 = new Question(10, "שאלה על שירות 4  ", LocalDateTime.MAX.now(), List.of(), "Question");
        handleFiles.saveFile(r1);
        handleFiles.saveFile(q1);
        InquiryManager inquiryManager = new InquiryManager();

//        InquiryManagerServer inquiryManagerServer=new InquiryManagerServer(6000);
//        inquiryManagerServer.start();


//        try {
//            System.out.println("Server is running. Press Enter to stop.");
//            System.in.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        inquiryManagerServer.stop();

    }

}





