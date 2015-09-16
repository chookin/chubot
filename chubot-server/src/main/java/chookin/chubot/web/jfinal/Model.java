package chookin.chubot.web.jfinal;

/**
 * Created by zhuyin on 9/14/15.
 */
public abstract class Model<M extends com.jfinal.plugin.activerecord.Model> extends com.jfinal.plugin.activerecord.Model<M> {
    private final String name;
    public Model(String name){
        this.name = name;
    }
    public M loadModel(long id) {
        return findById(id);
    }

    public void removeCache(Long id){

    }
}
