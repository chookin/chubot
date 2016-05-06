package cmri.utils.io;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by zhuyin on 7/6/14.
 */
public class FileHelper {
    private final static Logger LOG = Logger.getLogger(FileHelper.class);
    public final static Charset DEFAULT_ENCODING = java.nio.charset.StandardCharsets.UTF_8;

    public static String formatFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        String myName = fileName.replaceAll("&|<|>|\n", "-");
        return FilenameUtils.normalize(myName);
    }

    /**
     * Writes a string to a file. Create parent directories if not exist.
     *
     * @throws IOException
     */
    public static void save(String str, String fileName)
            throws IOException {
        LOG.trace(String.format("save file %s", new File(fileName).getAbsolutePath()));
        makeParentDirs(fileName);
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), DEFAULT_ENCODING))) {
            out.write(str);
        }
    }

    public static void save(byte[] bytes, String fileName)
            throws IOException {
        LOG.trace(String.format("save file %s", new File(fileName).getAbsolutePath()));
        makeParentDirs(fileName);
        try (FileOutputStream output = new FileOutputStream(fileName)) {
            output.write(bytes);
        }
    }

    /**
     * 写输入流中的数据到文件
     *
     * @param in       输入流,在退出函数时,会关闭该输入流
     * @param fileName 要写入的文件
     * @throws IOException
     */
    public static void save(InputStream in, String fileName) throws IOException {
        if (in == null) {
            return;
        }
        try {
            LOG.info(String.format("save file %s", new File(fileName).getAbsolutePath()));
            makeParentDirs(fileName);
            try (OutputStream out = new FileOutputStream(fileName)) {
                IOUtils.copy(in, out);
            }
        }finally {
            in.close();
        }
    }

    public static String readString(String fileName) throws IOException {
        // return FileUtils.readFileToString(new File(fileName));
        StringBuilder sb = new StringBuilder();
        FileInputStream fInputStream = new FileInputStream(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fInputStream, DEFAULT_ENCODING);
        try (BufferedReader br = new BufferedReader(inputStreamReader)) {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
        }
        return sb.toString();
    }

    public static void makeParentDirs(String fileName) throws IOException {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("fileName");
        }
        FileHelper.mkdirs(new File(fileName).getAbsoluteFile().getParentFile().getAbsolutePath());
    }

    /**
     * Note:
     * <li>Can't create a file and a folder with the same name and in the same folder. The OS would not allow you to do that since the name is the id for that file/folder object. So we have delete the older file</li>
     * <li>While File#mkdirs only return false if a file already exists with the same name.</li>
     *
     * @throws IOException if exists a file with the same name, or if failed to make dir because of security error.
     */
    public static void mkdirs(String dirpath) throws IOException {
        if (new java.io.File(dirpath).isDirectory()) {
            return;
        }
        LOG.info("make dir: " + dirpath);
        dirpath = FilenameUtils.normalize(dirpath);
        dirpath = dirpath.replace('\\', '/').replace("//", "/");

        String basePath = "";
        if (dirpath.startsWith("/")) {
            basePath = "/";
        }

        String[] paths = dirpath.split("/");
        for (String path : paths) {
            if (path.isEmpty()) {
                continue;
            }
            if (basePath.isEmpty()) {
                basePath = path;
            } else {
                basePath = basePath + "/" + path;
            }
            java.io.File file = new java.io.File(basePath);
            if (file.isFile()) {
                throw new IOException(String.format("failed to make dir '%s', because a file already exists with that name",
                        file.getPath()));
            }
            if (file.exists()) {
                continue;
            }
            boolean isCreated = file.mkdir();
            if (isCreated) {
                LOG.trace(String.format("success to make dir %s", file.getAbsolutePath()));
            }
        }
        if (!new File(dirpath).isDirectory()) {
            throw new IOException("Failed to create dir " + dirpath);
        }
    }
}
