package http;

public enum StatusCode {
    OK(200, "OK"),
    BAD_REQUEST_404(404, "Bad Response"),
    INTERNAL_SERVER_ERROR_500(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not implemented");
    public int CODE;
    public String MESSAGE;

    StatusCode(int code, String message) {
        this.CODE = code;
        this.MESSAGE = message;
    }
}
