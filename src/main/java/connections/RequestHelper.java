package connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class RequestHelper {
    public static void printInputStream(InputStream stream) throws IOException {
        BufferedReader buf = new BufferedReader(new InputStreamReader(stream));
        String str = buf.readLine();

        if(buf.markSupported()) buf.mark(0);

        while (!str.isEmpty() && !str.isBlank()) {
            System.out.println(str);
            str = buf.readLine();
        }

        buf.reset();
    }
}
