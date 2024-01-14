package io.github.edsuns.twitter;

import java.util.Objects;

/**
 * @author edsuns@qq.com
 * @since 2024/1/14 19:45
 */
public class TwitterMedia {
    private TwitterImage image;
    private TwitterVideo video;

    public TwitterImage getImage() {
        return image;
    }

    public void setImage(TwitterImage image) {
        this.image = image;
    }

    public TwitterVideo getVideo() {
        return video;
    }

    public void setVideo(TwitterVideo video) {
        this.video = video;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TwitterMedia media = (TwitterMedia) o;
        return Objects.equals(image, media.image) && Objects.equals(video, media.video);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, video);
    }

    @Override
    public String toString() {
        return "TwitterMedia{" +
                "image=" + image +
                ", video=" + video +
                '}';
    }
}
