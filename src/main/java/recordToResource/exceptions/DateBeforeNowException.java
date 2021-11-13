package recordToResource.exceptions;

public class DateBeforeNowException extends Exception {
    public DateBeforeNowException() {
        super();
    }

    public DateBeforeNowException(String s) {
        super(s);
    }
}
