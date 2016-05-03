package cmri.etl.common;

import org.junit.Test;

/**
 * Created by zhuyin on 6/23/15.
 */
public class RequestTest {

    @Test
    public void testGetUrl() throws Exception {
        Request request = new Request().setUrl("http://baidu.com");
        System.out.println(request.getUrl());
    }

    @Test
    public void testSetUrl(){
        try {
            new Request().setUrl("www.baidu.com");
        }catch (IllegalArgumentException e){
            System.out.println(e);
        }
    }
}