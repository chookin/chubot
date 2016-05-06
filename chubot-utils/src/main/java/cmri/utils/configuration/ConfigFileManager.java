package cmri.utils.configuration;

import cmri.utils.io.FileHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyin on 3/20/15.
 */
public class ConfigFileManager {
    public static final String BASE_DIR;
    static {
        String confDir = System.getProperty("CONF_DIR");
        if(confDir == null){
            BASE_DIR = "conf/";
        }else {
            BASE_DIR = confDir;
        }
    }

    /**
     * 导出classpath文件到本地磁盘
     * @throws IOException
     */
    public static void dump(String fileName) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        FileHelper.save(in, BASE_DIR + fileName);
    }

    public static void dumpIfNotExists(String fileName) throws IOException {
        if (exists(fileName)) {
            return;
        }
        dump(fileName);
    }

    public static boolean exists(String fileName){
        return new File(BASE_DIR + fileName).exists();
    }

    public static String getPath(String fileName){
        return BASE_DIR + fileName;
    }

    /**
     * @param name The resource name
     * @return  An input stream for reading the resource, or <tt>null</tt> if the resource could not be found
     * @throws IOException
     */
    public static InputStream getResourceFile(String name) throws IOException {
        if (exists(name)) {
            return new FileInputStream(getPath(name));
        } else {
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        }
    }

    public static List<String> readLines(String fileName) throws IOException {
        InputStream in = getResourceFile(fileName);
        if(in == null){
            throw new IOException("cannot load resource: "+fileName);
        }
        List<String> lines = new ArrayList<>();
        InputStreamReader reader = new InputStreamReader(in, "utf-8");
        try (BufferedReader br = new BufferedReader(reader)) {
            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
        }
        return lines;
    }
}
