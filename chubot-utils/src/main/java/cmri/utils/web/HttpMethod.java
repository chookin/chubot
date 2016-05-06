package cmri.utils.web;

/**
 * Created by chookin on 16/4/27.
 */
public enum  HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    TRACE("TRACE");

    private String value;
    HttpMethod(String value){
        this.value = value;
    }
    public String toString(){
        return this.value;
    }
}
