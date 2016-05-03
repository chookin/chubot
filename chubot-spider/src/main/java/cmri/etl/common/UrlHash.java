package cmri.etl.common;

/**
* Created by zhuyin on 4/28/15.
*/
public class UrlHash {
    private final String url;
    private final String hash;

    public UrlHash(String url, String hash){
        this.url = url;
        this.hash = hash;
    }
    public String getUrl() {

        return url;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UrlHash)) return false;

        UrlHash urlHash = (UrlHash) o;

        return url.equals(urlHash.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        return "UrlHash{" +
                "url='" + url + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
