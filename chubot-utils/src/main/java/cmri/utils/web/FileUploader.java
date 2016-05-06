package cmri.utils.web;

import cmri.utils.configuration.ConfigManager;
import cmri.utils.io.FileHelper;
import cmri.utils.lang.TimeHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhuyin on 11/13/15.
 */
public class FileUploader {
    static final org.slf4j.Logger LOG = LoggerFactory.getLogger(FileUploader.class);
    static int index = -1;
    static Lock lock = new ReentrantLock();

    /**
     * 产生作为文件名一部分的“计数”，避免文件并发上传时文件名重复的情况。
     */
    static int getIndex(){
        lock.lock();
        try {
            ++index;
            if(index == Integer.MAX_VALUE) index = 0;
            return index;
        }finally {
            lock.unlock();
        }
    }
    private long maxSizeMB = ConfigManager.getLong("upload.fileSizeMax");
    private int sizeThreshold = ConfigManager.getInt("upload.sizeThreshold");
    private String basePath = ConfigManager.get("upload.basePath");
    private String relPath;
    private String defaultExtension;
    private final HttpServletRequest request;
    private int count = Integer.MAX_VALUE;
    protected FileUploader(HttpServletRequest request){
        this.request = request;
    }
    public static FileUploader getInstance(HttpServletRequest request){
        return new FileUploader(request);
    }
    public long getMaxSizeMB() {
        return maxSizeMB;
    }

    public FileUploader setMaxSizeMB(long maxSizeMB) {
        this.maxSizeMB = maxSizeMB;
        return this;
    }

    public int getSizeThreshold() {
        return sizeThreshold;
    }

    public FileUploader setSizeThreshold(int sizeThreshold) {
        this.sizeThreshold = sizeThreshold;
        return this;
    }

    public String getBasePath() {
        return basePath;
    }

    public FileUploader setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public String getRelPath(){
        return relPath;
    }

    public FileUploader setRelPath(String relPath){
        this.relPath = relPath;
        return this;
    }
    public String getDefaultExtension(){
        return this.defaultExtension;
    }
    public FileUploader setDefaultExtension(String extension){
        this.defaultExtension = extension;
        return this;
    }
    /**
     * 最多上传几个文件
     * @param count 个数，如果小于1，则赋值为Integer.MAX_VALUE
     */
    public FileUploader setCount(int count){
        if(count < 1){
            this.count = Integer.MAX_VALUE;
        }else {
            this.count = count;
        }
        return this;
    }
    public int count(){
        return count;
    }
    String getUploadPath(){
        String uploadPath= FilenameUtils.concat(request.getSession().getServletContext().getRealPath("/"), basePath);
        uploadPath = FilenameUtils.concat(uploadPath, relPath);
        uploadPath = FilenameUtils.concat(uploadPath, TimeHelper.toString(new Date(), "yyyyMMdd"));
        return uploadPath;
    }

    /**
     * 上传文件到服务器
     * Warn: default, SpringBoot has configured "org.springframework.web.multipart.commons.CommonsMultipartResolver", which would cause ServletFileUpload.parseRequest(request) return null.
     * @throws Exception
     */
    public List<String> upload()
            throws Exception {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            throw new FileUploadException("request not contains multipart content");
        }
        String uploadPath = getUploadPath();
        String tmpPath = FilenameUtils.concat(request.getSession().getServletContext().getRealPath("/"), basePath + "/tmp");
        FileHelper.mkdirs(uploadPath);
        FileHelper.mkdirs(tmpPath);

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(sizeThreshold);
        factory.setRepository(new File(tmpPath));
        // 创建解析类的实例. ServletFileUpload负责处理上传的文件数据,并将表单中每个输入项封装成一个FileItem对象中.
        ServletFileUpload uploader = new ServletFileUpload(factory);
        uploader.setFileSizeMax(maxSizeMB * 1024 * 1024);
        List<FileItem> items = uploader.parseRequest(request);
        if(items.isEmpty()){
            throw new FileUploadException("请选择要上传的文件.");
        }
        List<String> savedNames = new ArrayList<>();
        int count = 0;
        for (FileItem item : items) {
            // 判断是否是普通类型的表单,如果不是那么就是file类型
            if (item.isFormField()) {
                continue;
            }
            String name = item.getName();//获取上传文件名,包括路径
            if (name.isEmpty() || item.getSize() == 0) {
                throw new FileUploadException("文件为空");
            }
            String extension = FilenameUtils.getExtension(name);
            if(extension.isEmpty()){
                extension = this.defaultExtension;
            }
            String myName = TimeHelper.toString(new Date(), "HHmmss") + "_" + getIndex() + "." + extension;
            item.write(new File(uploadPath, myName));
            savedNames.add(myName);
            if(this.count <= count){
                break;
            }
        }
        return savedNames;
    }

}
