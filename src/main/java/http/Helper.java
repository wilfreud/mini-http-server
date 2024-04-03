package http;

public class Helper {

    public static final String CRLF = "\r\n";

    public static String generateSimpleResponse(int status, String html) {

        return "HTTP/1.1 " + status + "OK" + CRLF +
                        "Content-Length: " + html.getBytes().length + CRLF +
                        CRLF +
                        html +
                        CRLF + CRLF;

    }

    /*TODO
    * Check how to set content-type based on the returned data
    * */
    public static String generateHttpHeaders(StatusCode status, long contentLength){
        return "HTTP/1.1 " + status.CODE + " " + status.MESSAGE + CRLF +
                "Content-Length: " + contentLength + CRLF + CRLF ;
    }
}
