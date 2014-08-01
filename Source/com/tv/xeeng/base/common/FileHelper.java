package com.tv.xeeng.base.common;

import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by thanhnvt on 23/06/2014.
 */
public class FileHelper {
    static Logger logger = Logger.getLogger(FileHelper.class);

    private static String readFile(String path, Charset encoding)
    {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        } catch (Exception ex) {
            logger.error(ex);
            return "";
        }
    }

    public static String readFileUTF8(String path) {
        String content = readFile(path, StandardCharsets.UTF_8);

        return content;
    }
}
