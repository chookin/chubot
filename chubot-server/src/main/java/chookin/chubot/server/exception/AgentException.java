package chookin.chubot.server.exception;

/**
 * Created by zhuyin on 9/11/15.
 */
public class AgentException extends RuntimeException {
    public AgentException(String message){
        super(message);
    }
    public AgentException(String message, Throwable t){
        super(message, t);
    }
}
