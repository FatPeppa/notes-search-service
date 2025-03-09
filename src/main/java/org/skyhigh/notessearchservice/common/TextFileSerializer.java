package org.skyhigh.notessearchservice.common;

import java.io.File;
import java.io.IOException;

public interface TextFileSerializer {
    byte[] serializeToByteArrayFromString(String content) throws IOException;
    byte[] serializeToByteArrayFromFile(File file) throws IOException;
    File serializeToFileFromString(String content, String point) throws IOException;
}
