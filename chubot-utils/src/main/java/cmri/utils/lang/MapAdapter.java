package cmri.utils.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zhuyin on 8/24/15.
 */
public class MapAdapter<K extends Object, V extends Object> {
    private Map<K,V> innerMap;
    public MapAdapter(){
        this.innerMap = new HashMap<>();
    }
    public MapAdapter(K key, V value){
        this.innerMap = new HashMap<>();
        this.innerMap.put(key, value);
    }

    @Override
    public int hashCode() {
        return innerMap.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapAdapter)) return false;

        MapAdapter<?, ?> that = (MapAdapter<?, ?>) o;

        return innerMap.equals(that.innerMap);

    }

    public boolean containsKey(K key){
        return this.innerMap.containsKey(key);
    }

    public MapAdapter<K,V> put(K key, V value){
        this.innerMap.put(key, value);
        return this;
    }

    public MapAdapter<K,V> put(Map<K,V> map){
        this.innerMap.putAll(map);
        return this;
    }

    public Map<K,V> get(){
        return this.innerMap;
    }

    public V get(K key){
        return this.innerMap.get(key);
    }

    public int size(){
        return innerMap.size();
    }

    public MapAdapter<K,V> remove(K key){
        this.innerMap.remove(key);
        return this;
    }
    public MapAdapter<K,V> clear(){
        this.innerMap.clear();
        return this;
    }

    public MapAdapter<K,V> sort(){
        if (!(this.innerMap instanceof TreeMap)) {
            this.innerMap = new TreeMap<>(this.innerMap);
        }
        return this;
    }

    public TreeMap<K,V> getSorted(){
        if (!(this.innerMap instanceof TreeMap)) {
            this.innerMap = new TreeMap<>(this.innerMap);
        }
        return (TreeMap<K, V>) innerMap;
    }

    public String toJson(){
        return JsonHelper.toJson(this.innerMap);
    }

    public String join(String kvSep, String groupSep){
        StringBuilder strb = new StringBuilder();
        for(Map.Entry<K,V> entry: innerMap.entrySet()){
            strb.append(entry.getKey())
                    .append(kvSep)
                    .append(entry.getValue())
                    .append(groupSep);
        }
        return strb.deleteCharAt(strb.length() - 1).toString();
    }

    @Override
    public String toString() {
        return innerMap.toString();
    }
}
