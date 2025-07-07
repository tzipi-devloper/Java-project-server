package testSaveFile;

import Business.InquiryHandling;
import Business.InquiryManager;
import Data.Inquiry;
import HandleStoreFiles.HandleFiles;
import java.io.IOException;

public class TestingHandleFiles {
    public static void main(String[] args) throws IOException {
//        PersonForTestSaving p1 = new
//                PersonForTestSaving("1234","aaa");
//        PersonForTestSaving p2 = new
//                PersonForTestSaving("5432","bbb");
//        PersonForTestSaving p3 = new
//                PersonForTestSaving("9999","ccc");
//        PersonForTestSaving p4 = new
//                PersonForTestSaving("0090","ccdc");
//
          HandleFiles handleFiles = new HandleFiles();
//        handleFiles.saveFile(p3);
//        handleFiles.saveFiles(Arrays.asList(p1,p2,p3,p4));
//        handleFiles.deleteFile(p2);


//         PersonForTestSaving p5 = new PersonForTestSaving("123456789","sucess BH!");
//         handleFiles.saveCSV(p5, "123456789.csv");
//         PersonForTestSaving readP5 = (PersonForTestSaving) handleFiles. readCsv ("123456789.csv");
//         System.out.println(readP5.id);


        Inquiry i= InquiryManager.getQueue().remove();
        Inquiry i1= InquiryManager.getQueue().remove();
        //System.out.println(i.getFileName());
     InquiryHandling.moveInquiryToHistory(i);
        InquiryHandling.moveInquiryToHistory(i1);
        //handleFiles.saveFile(i,"InquiryHistory");
//        System.out.println(InquiryManager.getQueue());
    }
}
