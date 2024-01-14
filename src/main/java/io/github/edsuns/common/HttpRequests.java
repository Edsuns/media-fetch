package io.github.edsuns.common;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author edsuns@qq.com
 * @since 2024/1/14 19:10
 */
public class HttpRequests {

    public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_SET_COOKIE = "Set-Cookie";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +
            " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    public static URI makeURI(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void putCookies(CookieManager cookieManager, URI url, String name, String value) throws IOException {
        cookieManager.put(
                url,
                new HashMap<>(2) {{
                    put(HEADER_SET_COOKIE, Collections.singletonList(name + "=" + value));
                }}
        );
    }

    public static void putCookies(CookieManager cookieManager, URI url, String name1, String value1, String name2, String value2) throws IOException {
        cookieManager.put(
                url,
                new HashMap<>(2) {{
                    put(HEADER_SET_COOKIE, Arrays.asList(name1 + "=" + value1, name2 + "=" + value2));
                }}
        );
    }
}
