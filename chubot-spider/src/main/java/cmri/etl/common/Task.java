package cmri.etl.common;

/**
 * Interface for identifying different tasks.<br>
 * Created by zhuyin on 3/13/15.
 */
public interface Task {
    /**
     * Unique id for a task.
     * @return uuid
     */
    String uuid();

    /**
     * Name for a task.
     * @return name
     */
    String name();
}
