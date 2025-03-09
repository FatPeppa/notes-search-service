package org.skyhigh.notessearchservice.common;

import java.io.File;
import java.io.IOException;

public interface TextFileDeserializer {
    String deserializeByteArrayOfTxtFileToString(byte[] byteArrayOfTxtFile, String point) throws IOException;
    String deserializeFileToString(File file) throws IOException;
    String deserializeByteArrayOfStringToString(byte[] byteArrayOfString) throws IOException;
}
