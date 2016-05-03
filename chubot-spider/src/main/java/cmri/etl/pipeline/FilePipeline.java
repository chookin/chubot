package cmri.etl.pipeline;

import cmri.etl.common.ResultItems;
import cmri.etl.dao.UrlFetchedDAO;
import cmri.utils.io.FileHelper;

import java.io.IOException;

/**
 * 在保存文件到本地磁盘时，会写资源的url及url的md5值到mongo的"urlFetched"集合
 *
 * Created by zhuyin on 3/2/15.
 */
public class FilePipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems) {
        if(resultItems.isSkip()){
            return;
        }
        long validateMilliseconds = resultItems.getRequest().getValidPeriod();
        if (resultItems.getResource() == null || validateMilliseconds == 0L || resultItems.getField("skipArchive") != null) {
            return;
        }
        if (resultItems.isCacheUsed() && new java.io.File(getFileName(resultItems)).exists()) {
            return;
        }
        switch (resultItems.getRequest().getTarget()) {
            case ByteArray:
                try {
                    saveByteArray(resultItems);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case File:
                break;
            default:
                try {
                    saveDocument(resultItems);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    private Pipeline saveDocument(ResultItems resultItems) throws IOException {
        FileHelper.save(resultItems.getResource().toString(), getFileName(resultItems));
        UrlFetchedDAO.save(resultItems.getRequest().getUrl());
        return this;
    }

    private boolean saveByteArray(ResultItems resultItems) throws IOException {
        byte[] bytes = (byte[]) resultItems.getResource();
        FileHelper.save(bytes, getFileName(resultItems));
        return true;
    }

    String getFileName(ResultItems resultItems) {
        return resultItems.getRequest().getFilePath();
    }
}
