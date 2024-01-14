package io.github.edsuns.twitter;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author edsuns@qq.com
 * @since 2024/1/14 18:54
 */
class TwitterVideoFetchTest {

    @Test
    void test() throws IOException, InterruptedException {
        String id = "1745777693709476046";
        String authToken = "xxx";
        String bearerToken = "xxx";
        InetSocketAddress proxyAddress = new InetSocketAddress("127.0.0.1", 7890);
        TwitterVideoFetch twitterVideoFetch = new TwitterVideoFetch(proxyAddress, authToken, bearerToken);
        System.out.println(twitterVideoFetch.fetchVideos(id));
    }
}