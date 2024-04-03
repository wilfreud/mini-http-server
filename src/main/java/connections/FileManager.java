package connections;

import http.Helper;
import http.StatusCode;

import java.io.*;

public class FileManager {
    public FileManager() {
    }

    public void putFileInOutputStream(OutputStream output, File fileContent) throws IOException, IllegalArgumentException {
        if (output == null || fileContent == null) throw new IllegalArgumentException("One or many arguments are null");

        FileInputStream fis = new FileInputStream(fileContent);

        // Write headers first
        String headers = Helper.generateHttpHeaders(StatusCode.OK, fileContent.length());
        output.write(headers.getBytes());

        byte[] buffer = new byte[1024];
        int bytesRead;
        while((bytesRead = fis.read(buffer)) != -1) output.write(buffer, 0, bytesRead);

        fis.close();
    }
}
