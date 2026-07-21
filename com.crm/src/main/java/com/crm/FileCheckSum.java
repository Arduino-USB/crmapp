package com.crm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32C;

public class FileCheckSum {

    public static long getCheckSum(Path path) throws IOException {
        CRC32C crc = new CRC32C();
        // 8KB buffer is optimal for most disk block sizes
        try (InputStream in = Files.newInputStream(path);
             BufferedInputStream bis = new BufferedInputStream(in, 8192)) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                crc.update(buffer, 0, bytesRead);
            }
        }
        return crc.getValue();
    }
}
