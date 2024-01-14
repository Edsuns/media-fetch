package io.github.edsuns.twitter;

import java.util.Objects;

/**
 * @author edsuns@qq.com
 * @since 2024/1/14 18:30
 */
public class TwitterVideo {
    private Long bitrate;
    private String contentType;
    private String url;

    public Long getBitrate() {
        return bitrate;
    }

    public void setBitrate(Long bitrate) {
        this.bitrate = bitrate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TwitterVideo that = (TwitterVideo) o;
        return Objects.equals(bitrate, that.bitrate) && Objects.equals(contentType, that.contentType) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitrate, contentType, url);
    }

    @Override
    public String toString() {
        return "TwitterVideo{" +
                "bitrate=" + bitrate +
                ", contentType='" + contentType + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
