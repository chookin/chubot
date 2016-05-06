package cmri.utils.concurrent;

/**
 * Created by zhuyin on 1/6/15.
 */
public class ThreadHelper {
    public static void sleep(int milliseconds) {
        long end = System.currentTimeMillis() + milliseconds;
        while (!Thread.currentThread().isInterrupted() && System.currentTimeMillis() < end) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
