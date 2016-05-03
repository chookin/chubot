package cmri.etl.job;

/**
 * Created by zhuyin on 8/29/15.
 */
public interface JobListener {
    void onInit();

    void onStart();

    void onSuccess();

    void onFail();
}
