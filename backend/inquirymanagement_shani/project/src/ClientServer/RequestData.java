package ClientServer;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class RequestData implements Serializable {
    private static final long serialVersionUID = 6634007134381918066L;

    public InquiryManagerActions action;
    public List <Object> parameters;

    public RequestData(InquiryManagerActions action, List<Object> parameters) {
        this.action = action;
        this.parameters = parameters;
    }

    public RequestData() {

    }
}