package cmri.etl.common;

import cmri.utils.lang.MapAdapter;

import java.util.Map;

/**
 * Created by chookin on 16/3/8.
 */
public class MapEntity extends MapAdapter<String, Object> implements MapItem {
    @Override
    public Map<String, Object> toStringMap() {
        return super.get();
    }

    @Override
    public String getId() {
        Object id = super.get("_id");
        if(id == null){
            return null;
        }else{
            return (String) id;
        }
    }

    public static String genId(String site, String code, String name){
        StringBuilder strb = new StringBuilder(site);
        if (code != null) {
            strb.append("-").append(code);
        }
        strb.append("-").append(name.substring(0, Math.min(9, name.length())));
        return strb.toString();
    }
}
