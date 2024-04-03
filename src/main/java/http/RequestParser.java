package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestParser {
    private final String METHOD;
    private final String RESOURCE_URI;
    private String HOST;
    private String CONTENT_TYPE;
    private String ACCEPT;
    private int CONTENT_LENGTH = 0;


    public RequestParser(InputStream input) throws IOException, ArrayIndexOutOfBoundsException, ParsingException {
        if (input == null) throw new ParsingException("Cannot parse an empty request");

        BufferedReader buf = new BufferedReader(new InputStreamReader(input));

        // Mark input's "beginning"
        final boolean streamCanBeMarked = buf.markSupported();
        if (streamCanBeMarked) buf.mark(1024);

        String line = buf.readLine();
        if (line == null || line.isEmpty()) throw new ParsingException("Error while parsing incoming HTTP request: Empty request");

        String[] lineContent = line.split(" ");

        // Extract HTTP method and URI
        this.METHOD = lineContent[0];
        this.RESOURCE_URI = lineContent[1].substring(1);

        while (!line.isEmpty() && !line.equals(Helper.CRLF)) {

            line = buf.readLine();

            // Extract host address
            if (line.startsWith("Host")) {
                lineContent = line.split(" ");
                this.HOST = lineContent[1];
            }
            // Extract content-type
            else if (line.startsWith("Content-Type")) {
                lineContent = line.split(" ");
                this.CONTENT_TYPE = lineContent[1];
            }
            // Extract "accept"
            else if (line.startsWith("Accept")) {
                lineContent = line.split(" ");
                this.ACCEPT = lineContent[1];
            }
            // Extract content-length
            else if (line.startsWith("Content-Length")) {
                lineContent = line.split(" ");
                this.CONTENT_LENGTH = Integer.parseInt(lineContent[1]);
            }
        }

        /*TODO
         * Find a way to parse Headers
         * Find a way to parse body
         * */

        if (streamCanBeMarked) buf.reset();
    }

    public String getMETHOD() {
        return METHOD;
    }

    public String getRESOURCE_URI() {
        return RESOURCE_URI;
    }

    public String getHOST() {
        return HOST;
    }

    public String getCONTENT_TYPE() {
        return CONTENT_TYPE;
    }

    public String getACCEPT() {
        return ACCEPT;
    }

    public int getCONTENT_LENGTH() {
        return CONTENT_LENGTH;
    }
}
