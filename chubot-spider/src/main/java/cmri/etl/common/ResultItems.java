package cmri.etl.common;

import cmri.etl.spider.Spider;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zhuyin on 3/2/15.
 */
public class ResultItems {
    private final Request request;
    private final Spider spider;
    /**
     * web资源，可以是web页面，可以是json文本
     */
    private Object resource;
    /**
     * collection of page process result item.
     */
    private final Collection<MapItem> virginItems = new HashSet<>();
    /**
     * Whether or not the document is loaded from cache.
     */
    private boolean cacheUsed = false;
    /**
     * Whether or not to skip pipeline processing.
     */
    private boolean skip = false;
    /**
     * Requests that need to add to spider scheduler.
     */
    private final List<Request> targetRequests = new ArrayList<>();
    /**
     * Store extra meta data.
     */
    private final Map<String, Object> fields = new LinkedHashMap<>();

    public ResultItems(Request request, Spider spider) {
        this.request = request;
        this.spider = spider;
    }

    public Request getRequest() {
        return request;
    }

    public Spider getSpider() {
        return spider;
    }

    public Object getResource() {
        return resource;
    }

    /**
     * If resource is null, it would be skipped on spider's pipeline process. So, even no use of getResource, you still need to set a nonsense value on resource.
     */
    public ResultItems setResource(Object resource) {
        this.resource = resource;
        return this;
    }

    public ResultItems cacheUsed(boolean cacheUsed) {
        this.cacheUsed = cacheUsed;
        return this;
    }

    public boolean isCacheUsed() {
        return cacheUsed;
    }

    public ResultItems skip(boolean skip) {
        this.skip = skip;
        return this;
    }

    public boolean isSkip() {
        return this.skip;
    }

    public List<Request> getTargetRequests() {
        return new ArrayList<>(targetRequests);
    }

    public ResultItems addTargetRequest(Request request) {
        if (request != null) {
            targetRequests.add(request);
        }
        return this;
    }

    /**
     * add requests to fetch
     */
    public ResultItems addTargetRequest(Collection<Request> requests) {
        targetRequests.addAll(requests);
        return this;
    }

    public ResultItems addTargetRequest(Collection<String> urls, Request.RequestGenerator generator) {
        targetRequests.addAll(urls.stream().map(generator::generate).collect(Collectors.toList()));
        return this;
    }

    /**
     * <ul>
     * <strong>Reserved extra key:</strong>
     * <li>skipArchive, if this key has value, then this resultItem will be skipped on FilePipeline</li>
     * </ul>
     */
    public ResultItems setField(String key, Object value) {
        fields.put(key, value);
        return this;
    }

    public Object getField(String key) {
        return fields.get(key);
    }

    public Map<String, Object> getAllFields() {
        return fields;
    }

    public ResultItems addItem(MapItem entity) {
        if (entity != null) {
            this.virginItems.add(entity);
        }
        return this;
    }

    public ResultItems addItem(Collection<? extends MapItem> entities) {
        if (entities != null) {
            this.virginItems.addAll(entities.stream().collect(Collectors.toList()));
        }
        return this;
    }

    public Collection<MapItem> getItems() {
        return this.virginItems;
    }
}
