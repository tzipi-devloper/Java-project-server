package ClientServer.Server;
import Business.InquiryHandling;
import Business.InquiryManager;
import ClientServer.InquiryManagerActions;
import ClientServer.RequestData;
import ClientServer.ResponseData;
import ClientServer.ResponseStatus;
import Data.Inquiry;
import Data.InquiryStatus;
import Data.Representative;
import HandleStoreFiles.HandleFiles;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.List;


public class HandleClient implements Runnable {
    Socket clientSocket;
    ObjectInputStream inputStream = null;
    ObjectOutputStream outputStream=null;

    public HandleClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {
                try {
                    RequestData request = (RequestData) inputStream.readObject();
                    System.out.println("Received object: " + request);
                    ResponseData responseData = handleClientRequest(request);

                    outputStream.writeObject(responseData);
                    outputStream.flush();
                } catch (IOException e) {
                    System.out.println("Client disconnected or error in communication.");
                    break;
                }
            }
            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ResponseData handleClientRequest(RequestData request) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        System.out.println("handleClientRequest");
        ResponseData responseData;

        switch(request.action) {
            case ADD_INQUIRY:
                return addInquiry(request.parameters);
            case ALL_INQUIRY:
                return getAllInquiryes(request.parameters);
            case GET_INQUIRY_STATUS:
                return getInquiryStatus(request.parameters);
            case CANCEL_INQUIRY:
                return cancelInquiry(request.parameters);
            case GET_REPRESENTATIVE_NAME_BY_INQUIRY_CODE:
                return  getRepresentativeNameByInquiryCode(request.parameters);
            case ADD_REPRESENTATIVE:
                return  addRepresentative(request.parameters);
            case DELETE_REPRESENTATIVE:
                return  deleteRepresentative(request.parameters);
            case IS_REPRESENTATIVE_ACTIVE:
                return  isRepresentativeActive(request.parameters);
            case GET_REPRESENTATIVE_INQUIRIES:
                return  getAllInquiriesByRepresentative(request.parameters);
            default:
                return new ResponseData(ResponseStatus.FAIL,"בקשה לא תקינה",null);
        }
    }

    public ResponseData getRepresentativeNameByInquiryCode(List<Object> parameters) {
        ResponseStatus status;
        String repName = null;
        try {
           int code = Integer.parseInt((String) parameters.get(0));
            if (!isInquiryExists(code,true))
            {
                return new ResponseData(ResponseStatus.FAIL, "לא נמצאה פנייה מתאימה ", null);
            }
            repName = InquiryHandling.getRepresentativeNameByInquiryCode(code);
            status = ResponseStatus.SUCCESS;
        } catch (Exception exception) {
            status = ResponseStatus.FAIL;
        }
        return new ResponseData(status, "message from server",repName);
    }

    public ResponseData addInquiry(List<Object> parameters) {

        Inquiry inquiry = (Inquiry) parameters.get(0);
        InquiryManager inquiryManager=new InquiryManager();
        ResponseStatus status = ResponseStatus.FAIL;
        int result=-1;
        try {
             result = inquiryManager.addInquiryFromClient(inquiry);
             status =ResponseStatus.SUCCESS;

        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        ResponseData responseData=new ResponseData(status,"AddInquiry from server",result);

        return responseData;
    }

    public ResponseData getAllInquiryes(List<Object> parameters){
        System.out.println("getAllInquiryes");
        ResponseStatus status =ResponseStatus.SUCCESS;
        InquiryManager inquiryManager=new InquiryManager();
        Object result=inquiryManager.getQueue();
        ResponseData responseData=new ResponseData(status,"getAllInquiryes from server",result);
        return responseData;
    }

    public ResponseData cancelInquiry(List<Object> parameters){
        System.out.println("cancelInquiry");

        int code = Integer.parseInt((String) parameters.get(0));
        if (!isInquiryExists(code,false))
        {
            return new ResponseData(ResponseStatus.FAIL, "לא נמצאה פנייה מתאימה ", null);
        }
        InquiryHandling inquiryHandling = new InquiryHandling();
        ResponseStatus status =ResponseStatus.FAIL;

        try {
            inquiryHandling.cancelInquiry(code);
            status =ResponseStatus.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();

        }finally {
            ResponseData responseData=new ResponseData(status,"cancelInquiry from server",null);
            return responseData;
        }
    }

    public ResponseData getInquiryStatus(List<Object> parameters){
        System.out.println("getInquiryStatus");
        int code = Integer.parseInt((String) parameters.get(0));
        if (!isInquiryExists(code,true))
        {
            return new ResponseData(ResponseStatus.FAIL, "לא נמצאה פנייה מתאימה ", null);
        }
        InquiryHandling inquiryHandling = new InquiryHandling();
        ResponseStatus status =ResponseStatus.FAIL;
        InquiryStatus inquiryStatus = null;
        try {
             inquiryStatus= inquiryHandling.getInquiryStatusByCode(code);
             status =ResponseStatus.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();

        }finally {
            ResponseData responseData=new ResponseData(status,"getInquiryStatus from server", inquiryStatus);
            return responseData;
        }
    }
    public ResponseData addRepresentative(List<Object> parameters) {
        ResponseStatus status;
        String message;

        try {

            if (parameters.size() < 2) {
                return new ResponseData(ResponseStatus.FAIL, "חסרים פרמטרים ליצירת נציג", null);
            }

            int id = Integer.parseInt(parameters.get(0).toString());
            String name = parameters.get(1).toString();

            if(isRepresentativeExists(id))
            {
                return new ResponseData(ResponseStatus.FAIL, "קיים כבר נציג עם id זהה", null);
            }
            Representative representative = new Representative(id, name);

            InquiryManager.representativeList.add(representative);
            HandleFiles handleFiles = new HandleFiles();


            File directory = new File("Representative");
            if (!directory.exists()) {
                directory.mkdir();
            }


            String filename = "Representative/" + representative.getCode() + ".csv";
            boolean success = handleFiles.saveCSV(representative, filename);

            if (success) {
                status = ResponseStatus.SUCCESS;
                message = "נציג נוסף בהצלחה ונשמר בקובץ.";
            } else {
                status = ResponseStatus.FAIL;
                message = "שגיאה בשמירת הנציג לקובץ.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            status = ResponseStatus.FAIL;
            message = "שגיאה בהוספת נציג: " + e.getMessage();
        }

        return new ResponseData(status, message, null);
    }

    public ResponseData deleteRepresentative(List<Object> parameters) {

        int id;
        try {
            id = Integer.parseInt(parameters.get(0).toString());
        } catch (NumberFormatException e) {
            return new ResponseData(ResponseStatus.FAIL, "קוד נציג לא תקין", null);
        }

        if (!isRepresentativeExists(id)) {
            return new ResponseData(ResponseStatus.FAIL, "נציג לא נמצא למחיקה", null);
        }

        Representative toRemove = null;
        for (Representative rep : InquiryManager.representativeList) {
            if (rep.getId() == id) {
                toRemove = rep;
                break;
            }
        }

        if (toRemove != null) {
            InquiryManager.representativeList.remove(toRemove);
        }

        File file = new File("Representative/" + id + ".csv");
        if (file.exists()) {
            file.delete();
        }

        return new ResponseData(ResponseStatus.SUCCESS, "נציג נמחק בהצלחה", null);
    }


    public ResponseData isRepresentativeActive(List<Object> parameters)
    {
        System.out.println("isRepresentativeActive");

        int code = (int) parameters.get(0);
        InquiryManager inquiryManager=new InquiryManager();
        ResponseStatus status =ResponseStatus.FAIL;
        String message="isRepresentativeActive";

        boolean result=false;
        try {
            if(isRepresentativeExists(code)) {
                result= inquiryManager.isRepresentativeActive(code);
                status =ResponseStatus.SUCCESS;
            }
            else {
                message="נציג לא נמצא";
            }
        }catch (Exception e){
            e.printStackTrace();
            message="שגיאת שרת";
        }finally {
            ResponseData responseData=new ResponseData(status,"isRepresentativeActive from server",result);
            return responseData;
        }
    }
   public ResponseData getAllInquiriesByRepresentative(List<Object> parameters)
   {
       System.out.println("getAllInquiriesByRepresentative");

       int code = (int) parameters.get(0);
       InquiryManager inquiryManager=new InquiryManager();
       ResponseStatus status =ResponseStatus.FAIL;
       String message="getAllInquiriesByRepresentative";
       List<Inquiry>  result=null;
       try {
          if(isRepresentativeExists(code)){
              result= inquiryManager.getAllInquiriesByRepresentative(code);
              status =ResponseStatus.SUCCESS;
          }
          else {
              message= "נציג לא נמצא";
          }
       }catch (Exception e){
           e.printStackTrace();
           message="שגיאת שרת";
       }finally {
           ResponseData responseData=new ResponseData(status,message,result);
           return responseData;
       }
    }

    public static boolean isRepresentativeExists(int repId) {
        for (Representative rep : InquiryManager.representativeList) {
            if (rep.getId() == repId) return true;
        }
        for (Representative rep : InquiryManager.activeInquiriesMap.values()) {
            if (rep.getId() == repId) return true;
        }
        File dir = new File("Representative");
        if (dir.exists()) {
            HandleFiles hf = new HandleFiles();
            for (File file : dir.listFiles()) {
                Representative rep = (Representative) hf.readCsv(file.getAbsolutePath());
                if (rep != null && rep.getId() == repId)
                    return true;
            }
        }
        return false;
    }
    public static boolean isInquiryExists(int code,boolean searchInHistory) {
        for (Inquiry inq : InquiryManager.queue) {
            if (inq.getCode() == code) return true;
        }
        for (Inquiry inq : InquiryManager.activeInquiriesMap.keySet()) {
            if (inq.getCode() == code) return true;
        }
        if(searchInHistory)
        {
            File dir = new File("InquiryHistory");
            if (dir.exists()) {
                HandleFiles hf = new HandleFiles();
                for (File file : dir.listFiles()) {
                    Inquiry inq = (Inquiry) hf.readTxt(file);
                    if (inq != null && inq.getCode() == code)
                        return true;
                }
            }
        }

        return false;
    }
}
