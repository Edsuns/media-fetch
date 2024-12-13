package io.github.edsuns.twitter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

/**
 * @author edsuns@qq.com
 * @since 2024/12/13 22:11
 */
@ParametersAreNonnullByDefault
public class XMedia {
    private String mediaKey;
    private String mediaUrlHttps;
    private Type type;
    private OriginalInfo originalInfo;
    @Nullable
    private VideoInfo videoInfo;

    public String getMediaKey() {
        return mediaKey;
    }

    public void setMediaKey(String mediaKey) {
        this.mediaKey = mediaKey;
    }

    public String getMediaUrlHttps() {
        return mediaUrlHttps;
    }

    public void setMediaUrlHttps(String mediaUrlHttps) {
        this.mediaUrlHttps = mediaUrlHttps;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public OriginalInfo getOriginalInfo() {
        return originalInfo;
    }

    public void setOriginalInfo(OriginalInfo originalInfo) {
        this.originalInfo = originalInfo;
    }

    @Nullable
    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(@Nullable VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof XMedia)) return false;
        XMedia media = (XMedia) o;
        return Objects.equals(mediaKey, media.mediaKey) && Objects.equals(mediaUrlHttps, media.mediaUrlHttps)
                && type == media.type && Objects.equals(originalInfo, media.originalInfo)
                && Objects.equals(videoInfo, media.videoInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaKey, mediaUrlHttps, type, originalInfo, videoInfo);
    }

    @Override
    public String toString() {
        return "XMedia{" +
                "mediaKey='" + mediaKey + '\'' +
                ", mediaUrlHttps='" + mediaUrlHttps + '\'' +
                ", type=" + type +
                ", originalInfo=" + originalInfo +
                ", videoInfo=" + videoInfo +
                '}';
    }

    public enum Type {
        photo, video
    }

    public static class OriginalInfo {
        private Integer height;
        private Integer width;

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof OriginalInfo)) return false;
            OriginalInfo that = (OriginalInfo) o;
            return Objects.equals(height, that.height) && Objects.equals(width, that.width);
        }

        @Override
        public int hashCode() {
            return Objects.hash(height, width);
        }

        @Override
        public String toString() {
            return "OriginalInfo{" +
                    "height=" + height +
                    ", width=" + width +
                    '}';
        }
    }

    public static class VideoInfoVariant {
        private String contentType;
        private String url;
        @Nullable
        private Integer bitrate;

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

        @Nullable
        public Integer getBitrate() {
            return bitrate;
        }

        public void setBitrate(@Nullable Integer bitrate) {
            this.bitrate = bitrate;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof VideoInfoVariant)) return false;
            VideoInfoVariant that = (VideoInfoVariant) o;
            return Objects.equals(contentType, that.contentType)
                    && Objects.equals(url, that.url) && Objects.equals(bitrate, that.bitrate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(contentType, url, bitrate);
        }

        @Override
        public String toString() {
            return "VideoInfoVariant{" +
                    "contentType='" + contentType + '\'' +
                    ", url='" + url + '\'' +
                    ", bitrate=" + bitrate +
                    '}';
        }
    }

    public static class VideoInfo {
        private Long durationMillis;
        private List<VideoInfoVariant> variants;

        public Long getDurationMillis() {
            return durationMillis;
        }

        public void setDurationMillis(Long durationMillis) {
            this.durationMillis = durationMillis;
        }

        public List<VideoInfoVariant> getVariants() {
            return variants;
        }

        public void setVariants(List<VideoInfoVariant> variants) {
            this.variants = variants;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof VideoInfo)) return false;
            VideoInfo videoInfo = (VideoInfo) o;
            return Objects.equals(durationMillis, videoInfo.durationMillis)
                    && Objects.equals(variants, videoInfo.variants);
        }

        @Override
        public int hashCode() {
            return Objects.hash(durationMillis, variants);
        }

        @Override
        public String toString() {
            return "VideoInfo{" +
                    "durationMillis=" + durationMillis +
                    ", variants=" + variants +
                    '}';
        }
    }
}
