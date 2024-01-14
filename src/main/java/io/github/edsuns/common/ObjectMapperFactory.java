package io.github.edsuns.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.github.edsuns.twitter.TwitterVideoFetch;

/**
 * @author edsuns@qq.com
 * @since 2024/1/14 18:53
 */
public class ObjectMapperFactory {

    private static ObjectMapper DEFAULT_OBJECT_MAPPER;

    public static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper x = DEFAULT_OBJECT_MAPPER;
        if (x == null) {
            synchronized (TwitterVideoFetch.class) {
                x = DEFAULT_OBJECT_MAPPER;
                if (x == null) {
                    x = new ObjectMapper()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    x.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
                    DEFAULT_OBJECT_MAPPER = x;
                }
            }
        }
        return x;
    }
}
