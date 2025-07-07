package Exceptions;

public class InquiryException extends Exception {

    private final int InquiryCode;

    public InquiryException(int referenceNumber, String message) {
        super(message);
        this.InquiryCode = referenceNumber;
    }

    @Override
    public String getMessage() {
        return InquiryCode + ": " + super.getMessage();
    }
}
