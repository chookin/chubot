package chookin.chubot.web.jfinal;

import com.jfinal.plugin.ehcache.CacheKit;

/**
 * Created by zhuyin on 9/14/15.
 */
public abstract class Model<M extends com.jfinal.plugin.activerecord.Model> extends com.jfinal.plugin.activerecord.Model<M> {
    private final String name;
    public Model(String name){
        this.name = name;
    }
    public M loadModel(int id) {
        final int ID = id;
        return (M) CacheKit.get(name, ID, () -> {
            return findById(ID);
        });
    }

    public void removeCache(Integer id){
        if(id == null){
            return;
        }
        CacheKit.remove(name, id);
    }
}
