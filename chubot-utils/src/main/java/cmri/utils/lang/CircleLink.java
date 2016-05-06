package cmri.utils.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 7/7/15.
 */
public class CircleLink<T> {
    private final List<T> items = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int index = -1;

    public T current(){
        lock.readLock().lock();
        try{
            if (this.items.isEmpty()) {
                return null;
            }
            if (index >= this.items.size() || index < 0) {
                index = 0;
            }
            return this.items.get(index);
        }finally {
            lock.readLock().unlock();
        }
    }

    public CircleLink<T> switchNext(){
        lock.readLock().lock();
        try {
                ++index;
        }finally {
            lock.readLock().unlock();
        }
        return this;
    }

    public int size(){
        lock.readLock().lock();
        try{
            return items.size();
        }finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 从紧邻当前位的下一个位置添加
     * @param items 要添加的元素
     * @return this
     */
    public CircleLink<T> add(Collection<T> items){
        lock.writeLock().lock();
        try{
            int myIndex = Math.min(index + 1, this.items.size());
            this.items.addAll(myIndex, items);
        }finally {
            lock.writeLock().unlock();
        }
        return this;
    }

    public CircleLink<T> remove(Collection<T> items){
        lock.writeLock().lock();
        try{
            this.items.removeAll(items);
        }finally {
            lock.writeLock().unlock();
        }
        return this;
    }
}
