package core;

import utils.Config;
import http.Helper;
import http.PythonExecutionException;
import http.StatusCode;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    public static String BASE_DIR = Config.BASE_DIR;
    public static final Path BASE_DIR_PATH = Paths.get(BASE_DIR).normalize();

    private final String SVG_FILE_ICON = "<svg xmlns=\"http://www.w3.org/2000/svg\"  viewBox=\"0 0 50 50\" width=\"50px\" height=\"50px\"><path d=\"M 7 2 L 7 48 L 43 48 L 43 14.59375 L 42.71875 14.28125 L 30.71875 2.28125 L 30.40625 2 Z M 9 4 L 29 4 L 29 16 L 41 16 L 41 46 L 9 46 Z M 31 5.4375 L 39.5625 14 L 31 14 Z M 15 22 L 15 24 L 35 24 L 35 22 Z M 15 28 L 15 30 L 31 30 L 31 28 Z M 15 34 L 15 36 L 35 36 L 35 34 Z\"/></svg>";
    private final String SVG_FOLDER_ICON = "<svg xmlns=\"http://www.w3.org/2000/svg\"  viewBox=\"0 0 48 48\" width=\"48px\" height=\"48px\"><path fill=\"#FFA000\" d=\"M38,12H22l-4-4H8c-2.2,0-4,1.8-4,4v24c0,2.2,1.8,4,4,4h31c1.7,0,3-1.3,3-3V16C42,13.8,40.2,12,38,12z\"/><path fill=\"#FFCA28\" d=\"M42.2,18H15.3c-1.9,0-3.6,1.4-3.9,3.3L8,40h31.7c1.9,0,3.6-1.4,3.9-3.3l2.5-14C46.6,20.3,44.7,18,42.2,18z\"/></svg>";

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

        Path currentFolder = Paths.get(parentPath);

        StringBuilder htmlContent = new StringBuilder();
        String topHtml = """
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Commodore64 - Server</title>
                    <style>
                        body {
                            background-color: #fbfbfb;
                            margin: 2em 4em;
                            font-family: Tahoma, Verdana, Arial, sans-serif;
                        }
                        svg {
                            width: 20px;
                            height: 20px;
                        }
                    </style>
                </head>
                <body>
                <h2>Index of /%s</h2>
                <ul>
                """.formatted(parentPath);
        htmlContent.append(topHtml);

        if (files != null) {
            Path parentDirectoryPath = Paths.get(directoryContent.getParent()).normalize();


            String immediateParentName = currentFolder.resolve("..").normalize().toString().replace("\\", "/");


            if (parentDirectoryPath.normalize().startsWith(BASE_DIR_PATH.normalize())) {
                htmlContent.append(String.format("<li> %s <a href=\"/%s\">..</a> </li>", SVG_FOLDER_ICON, immediateParentName));
            }


            for (File file : files) {
                String fileName = file.getName();
                String filePath = currentFolder.getFileName() + "/" + fileName;
                String li = String.format("<li> %s <a href=\"%s\"> %s </a> </li>", (file.isFile() ? SVG_FILE_ICON : SVG_FOLDER_ICON), filePath, fileName);
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

    public void runScriptAndPutInOutputStream(OutputStream output, File scriptFile) throws PythonExecutionException {
        try {
            Path path = Paths.get(scriptFile.getAbsolutePath());

            Process process = Runtime.getRuntime().exec("python " + path);

            BufferedReader scriptOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while ((line = scriptOutput.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }
            String scriptResult = outputBuilder.toString();

            String response = Helper.generateSimpleResponse(StatusCode.OK.CODE, scriptResult);
            output.write(response.getBytes());
        } catch (IOException | InvalidPathException err) {
            System.err.println(err.getMessage());
            throw new PythonExecutionException("Error executing python script " + scriptFile.getName());
        }
    }
}
