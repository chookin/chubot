package cmri.etl.validator;

import java.io.IOException;

/**
 * Created by chookin on 16/5/3.
 */
public class PageDeniedException extends IOException {
    public PageDeniedException(){}
    public PageDeniedException(String message) {
        super(message);
    }
}
