package http;

public enum StatusCode {
    OK(200, "OK"),
    NOT_FOUD_404(404, "Not Found"),
    FORBIDDEN(403, "Forbidden"),
    BAD_REQUEST(400, "Bard Request"),
    INTERNAL_SERVER_ERROR_500(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not implemented");
    public int CODE;
    public String MESSAGE;

    StatusCode(int code, String message) {
        this.CODE = code;
        this.MESSAGE = message;
    }
}
