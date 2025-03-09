package org.skyhigh.notessearchservice.common;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class TextFileSerializerImpl implements TextFileSerializer {
    @Override
    public byte[] serializeToByteArrayFromString(String content) throws IOException {
        try {
            if (content == null) return null;
            return content.getBytes();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public byte[] serializeToByteArrayFromFile(File file) throws IOException {
        if (file == null) return null;
        try {
            return Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public File serializeToFileFromString(String content, String point) throws IOException {
        if (content == null || point == null) return null;
        var tempFile = File.createTempFile("temp_" + System.currentTimeMillis() + "_" + point, ".txt");
        tempFile.deleteOnExit();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            boolean deletionResult = tempFile.delete();
            throw new IOException(e);
        }
        return null;
    }
}
