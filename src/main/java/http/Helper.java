package http;

public class Helper {

    public static final String CRLF = "\r\n";

    public static String generateResponse(int status, String html) {

        String response =
                "HTTP/1.1 " + status + "OK" + CRLF +
                        "Content-Length: " + html.getBytes().length + CRLF +
                        CRLF +
                        html +
                        CRLF + CRLF;

        return response;
    }
}
