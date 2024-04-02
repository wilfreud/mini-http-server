package connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class RequestHelper {
    public static void printInputStream(InputStream stream) throws IOException {
        BufferedReader buf = new BufferedReader(new InputStreamReader(stream));
        String str = buf.readLine();

        while(str != null){
            System.out.println(str);
            str= buf.readLine();
        }


    }
}
