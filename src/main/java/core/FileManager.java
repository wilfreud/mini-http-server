package core;

import config.Config;
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

    public void runScriptAndPutInOutputStream(OutputStream output, File scriptFile) throws PythonExecutionException {
        try {
            Path path = Paths.get(scriptFile.getAbsolutePath());

            Process process = Runtime.getRuntime().exec( "python " + path);

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
