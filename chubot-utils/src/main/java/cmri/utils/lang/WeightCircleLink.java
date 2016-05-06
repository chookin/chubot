package cmri.utils.lang;

import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 7/22/15.
 */
public class WeightCircleLink<T> implements Serializable {
    private final List<Node<T>> items = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Node<T> currentNode = null;

    /**
     * @return null if empty.
     */
    public T current() {
        lock.readLock().lock();
        try {
            if (currentNode == null) {
                return null;
            }
            return currentNode.item;
        } finally {
            lock.readLock().unlock();
        }
    }

    public WeightCircleLink<T> switchNext() {
        lock.readLock().lock();
        try {
            if (currentNode != null)
                currentNode = currentNode.next;
            else
                currentNode = null;
        } finally {
            lock.readLock().unlock();
        }
        return this;
    }

    public int size() {
        lock.readLock().lock();
        try {
            return items.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 均衡的插入元素到串中
     *
     * @param item 要添加的元素
     * @param n    需要插入的次数
     * @return this
     */
    public WeightCircleLink<T> add(T item, int n) {
        lock.writeLock().lock();
        try {
            int start = 0;
            for (int count = 0; count < n; ++count) {
                start = _add(item, start);
            }
        } finally {
            lock.writeLock().unlock();
        }
        return this;
    }

    /**
     * 插入元素到从某个位置起第一次出现了重复元素的位置，例如，若items为ABCAC，待插入的元素为D，则插入元素D到index=3的位置，插入后的串为ABCDAC
     *
     * @param item  要插入的元素
     * @param start 从哪个位置开始查找
     * @return 插入的位置
     */
    private int _add(T item, int start) {
        Validate.isTrue(start > -1, "'start' must be bigger than -1");

        // get the position index where to insert the new item.
        Set<T> unique = new HashSet<>();
        int index = start;
        for (; index < items.size(); ++index) {
            T me = items.get(index).item;
            if (unique.contains(me)) {
                break;
            } else {
                unique.add(me);
            }
        }

        Node<T> node = new Node<>(item);
        if (index == 0) {// if this.items is empty, then index must be 0.
            node.next(node);
            currentNode = node;
        } else {
            // first set prev node.
            items.get(index - 1).next(node);

            // then set the next node.
            if (index < items.size()) {// if not append to the last.
                node.next(items.get(index));
            } else { // if append to the last, then set next to the first.
                node.next(items.get(0));
            }
        }
        items.add(index, node);
        return index;
    }

    public WeightCircleLink<T> remove(Collection<T> items) {
        lock.writeLock().lock();
        try {
            for (int index = 0; index < this.items.size(); ) {
                Node<T> node = this.items.get(index);
                if (items.contains(node.item)) {
                    if (this.items.size() == 1) { // if the list only contains this item, then empty it.
                        this.items.clear();
                        currentNode = null;
                    } else {
                        if (index < this.items.size() - 1) { // if not the last item
                            node.prev.next(this.items.get(index + 1));
                        } else {
                            node.prev.next(this.items.get(0));
                        }
                        if (node.equals(currentNode)) {
                            currentNode = node.prev.next;
                        }
                        this.items.remove(index);
                    }
                } else {
                    ++index;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
        return this;
    }

    static class Node<T> {
        private T item;
        private Node<T> prev;
        private Node<T> next;

        public Node(T item) {
            Validate.notNull(item, "link node's item cannot be null");
            this.item = item;
        }

        public Node<T> next(Node<T> next) {
            this.next = next;
            next.prev = this;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;

            Node<?> node = (Node<?>) o;

            return item.equals(node.item);

        }

        @Override
        public int hashCode() {
            return item.hashCode();
        }

        @Override
        public String toString() {
            StringBuilder strb = new StringBuilder();
            strb.append(item);
            if (prev != null) {
                strb.append(" prev=")
                        .append(prev.item);
            }
            if (next != null) {
                strb.append(" prev=")
                        .append(next.item);
            }
            return strb.toString();
        }
    }
}
