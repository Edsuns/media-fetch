package io.github.edsuns.twitter;

import java.util.Objects;

/**
 * @author edsuns@qq.com
 * @since 2024/1/14 19:37
 */
public class TwitterImage {
    private String type;
    private String mediaUrlHttps;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMediaUrlHttps() {
        return mediaUrlHttps;
    }

    public void setMediaUrlHttps(String mediaUrlHttps) {
        this.mediaUrlHttps = mediaUrlHttps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TwitterImage that = (TwitterImage) o;
        return Objects.equals(type, that.type) && Objects.equals(mediaUrlHttps, that.mediaUrlHttps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, mediaUrlHttps);
    }

    @Override
    public String toString() {
        return "TwitterImage{" +
                "type='" + type + '\'' +
                ", mediaUrlHttps='" + mediaUrlHttps + '\'' +
                '}';
    }
}
