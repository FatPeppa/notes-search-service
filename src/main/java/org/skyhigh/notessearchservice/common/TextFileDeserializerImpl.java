package org.skyhigh.notessearchservice.common;

import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class TextFileDeserializerImpl implements TextFileDeserializer {
    @Override
    public String deserializeByteArrayOfTxtFileToString(byte[] byteArrayOfTxtFile, String point) throws IOException {
        if (byteArrayOfTxtFile == null || point == null) return null;
        var tempFile = File.createTempFile("temp_" + System.currentTimeMillis() + "_" + point, ".txt");
        tempFile.deleteOnExit();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            fileOutputStream.write(byteArrayOfTxtFile);
            fileOutputStream.close();
            StringBuilder resultStringBuilder = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
            boolean deletionResult = tempFile.delete();
            if (resultStringBuilder.toString().isEmpty())
                return null;
            return resultStringBuilder.toString();
        } catch (Exception e) {
            boolean deletionResult = tempFile.delete();
            throw new IOException(e);
        }
    }

    @Override
    public String deserializeFileToString(File file) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
        if (resultStringBuilder.toString().isEmpty())
            return null;
        return resultStringBuilder.toString();
    }

    @Override
    public String deserializeByteArrayOfStringToString(byte[] byteArrayOfString) throws IOException {
        if (byteArrayOfString == null || byteArrayOfString.length == 0) return null;
        try {
            return new String(byteArrayOfString);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
