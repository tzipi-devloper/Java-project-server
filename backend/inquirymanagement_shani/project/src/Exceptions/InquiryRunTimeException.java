package Exceptions;

public class InquiryRunTimeException extends RuntimeException {

    private final int InquiryCode;

    public InquiryRunTimeException(int referenceNumber, String message) {
        super(message);
        this.InquiryCode = referenceNumber;
    }

    @Override
    public String getMessage() {
        return InquiryCode + ": " + super.getMessage();
    }
}

