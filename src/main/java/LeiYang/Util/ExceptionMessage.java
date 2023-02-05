package LeiYang.Util;

public class ExceptionMessage<T> {
    private int code;
    private String message;
    public ExceptionMessage(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public ExceptionMessage() {

    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public ExceptionMessage success()
    {
        ExceptionMessage result = new ExceptionMessage(200,"OK");
        return result;
    }
    public ExceptionMessage fail()
    {
        ExceptionMessage result = new ExceptionMessage(400,"bad");
        return result;
    }
}
