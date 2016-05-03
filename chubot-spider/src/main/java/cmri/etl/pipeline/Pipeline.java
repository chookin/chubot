package cmri.etl.pipeline;

import cmri.etl.common.ResultItems;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by zhuyin on 3/2/15.
 */
public interface Pipeline extends Closeable {
    default Logger getLogger(){
        return Logger.getLogger(Pipeline.class);
    }

    void process(ResultItems resultItems);

    default void open() throws IOException {}
    default void close() throws IOException {}
}
