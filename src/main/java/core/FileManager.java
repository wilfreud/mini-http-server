package core;

import http.Helper;
import http.StatusCode;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    public static String BASE_DIR = "src/main/resources/htdocs";
    public static final Path BASE_DIR_PATH = Paths.get(BASE_DIR).normalize();

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
        while ((bytesRead = fis.read(buffer)) != -1) output.write(buffer, 0, bytesRead);

        fis.close();
    }

    public void putDirectoryContentInOutputStream(OutputStream output, File directoryContent, String parentPath) throws IOException, IllegalArgumentException {
        if (output == null || directoryContent == null)
            throw new IllegalArgumentException("One or many arguments are null");

        File[] files = directoryContent.listFiles();

        StringBuilder htmlContent = new StringBuilder();
        String topHtml = """
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Commodore64 - Server</title>
                    <style>
                        body {
                            margin: 10px 10px;
                            font-family: Tahoma, Verdana, Arial, sans-serif;
                        }
                    </style>
                </head>
                <body>
                <ul>
                """;
        htmlContent.append(topHtml);

        if (files != null) {
            Path parentDirectoryPath = Paths.get(directoryContent.getParent()).normalize();
            Path currentFolder = Paths.get(parentPath);

            String immediateParentName = currentFolder.resolve("..").normalize().toString().replace("\\", "/");


            if (parentDirectoryPath.normalize().startsWith(BASE_DIR_PATH.normalize())) {
                htmlContent.append(String.format("<li> <a href=\"/%s\">..</a> </li>", immediateParentName));
            }


            for (File file : files) {
                String fileName = file.getName();
                String filePath = currentFolder.getFileName() + "/" + fileName;
                String li = String.format("<li> <a href=\"%s\"> %s </a> </li>", filePath, fileName);
                htmlContent.append(li);
            }
        } else {
            htmlContent.append("<p><em>Empty directory</em></p>");
        }

        String bottomHtml = """
                    </ul>
                </body>
                </html>
                """;

        htmlContent.append(bottomHtml);

        String response = Helper.generateSimpleResponse(StatusCode.OK.CODE, htmlContent.toString());
        output.write(response.getBytes());
    }
}
