package io.github.edsuns.twitter;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.edsuns.common.ObjectMapperFactory;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static io.github.edsuns.common.HttpRequests.*;

/**
 * @author edsuns@qq.com
 * @since 2024/1/14 17:13
 */
public class TwitterVideoFetch {

    private static final JsonPointer VIDEO_POINTER = JsonPointer.compile("/data/threaded_conversation_with_injections_v2/instructions/0/entries/0/content/itemContent/tweet_results/result/legacy/entities/media/0/video_info/variants");

    private final InetSocketAddress proxyAddress;
    private final String authToken;
    private final String bearerToken;
    private final CookieManager cookieManager;
    private final HttpClient client;

    public TwitterVideoFetch(InetSocketAddress proxyAddress, String authToken, String bearerToken) {
        this.proxyAddress = proxyAddress;
        this.authToken = authToken;
        this.bearerToken = bearerToken;
        this.cookieManager = new CookieManager();
        this.client = createClient();
    }

    private HttpClient createClient() {
        return HttpClient.newBuilder()
                .proxy(ProxySelector.of(proxyAddress))
                .cookieHandler(cookieManager)
                .build();
    }

    public List<TwitterVideo> fetchVideos(String id) throws IOException, InterruptedException {
        String authTokenCookieName = "auth_token";
        String csrfTokenCookieName = "ct0";

        URI statusUrl = statusUrl(id);
        putCookies(cookieManager, statusUrl, authTokenCookieName, authToken);
        client.send(
                HttpRequest.newBuilder()
                        .uri(statusUrl)
                        .headers(
                                HEADER_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9",
                                HEADER_ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9",
                                HEADER_USER_AGENT, USER_AGENT
                        )
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.discarding()
        ).body();
        String csrfToken = cookieManager.getCookieStore().get(statusUrl).stream().filter(x -> csrfTokenCookieName.equals(x.getName()))
                .map(HttpCookie::getValue).findAny().orElse(null);
        if (csrfToken == null || csrfToken.isBlank()) {
            throw new IOException("failed to get csrf token");
        }

        URI detailUrl = detailUrl(id);
        putCookies(cookieManager, detailUrl,
                authTokenCookieName, authToken,
                csrfTokenCookieName, csrfToken
        );
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(detailUrl)
                        .headers(
                                HEADER_AUTHORIZATION, "Bearer " + bearerToken,
                                "X-Csrf-Token", csrfToken
                        )
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("status: " + response.statusCode());
        }
        String json = response.body();

        ObjectMapper mapper = ObjectMapperFactory.getDefaultObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode videos = root.requiredAt(VIDEO_POINTER);
        return mapper.readValue(videos.traverse(), new TypeReference<>() { });
    }

    private static URI statusUrl(String id) {
        return makeURI("https://twitter.com/zuoyinan888/status/" + id);
    }

    private static URI detailUrl(String id) {
        return makeURI(
                "https://twitter.com/i/api/graphql/TYIBgzy5ZTobTeU0T-8pFw/TweetDetail?variables=%7B%22focalTweetId%22%3A%22"
                        + id +
                        "%22%2C%22with_rux_injections%22%3Afalse%2C%22includePromotedContent%22%3Atrue%2C%22withCommunity%22%3Atrue" +
                        "%2C%22withQuickPromoteEligibilityTweetFields%22%3Atrue%2C%22withBirdwatchNotes%22%3Atrue" +
                        "%2C%22withVoice%22%3Atrue%2C%22withV2Timeline%22%3Atrue%7D&features=%7B%22responsive_web_graphql_exclude_directive_enabled%22%3Atrue" +
                        "%2C%22verified_phone_label_enabled%22%3Afalse%2C%22creator_subscriptions_tweet_preview_api_enabled%22%3Atrue" +
                        "%2C%22responsive_web_graphql_timeline_navigation_enabled%22%3Atrue%2C%22responsive_web_graphql_skip_user_profile_image_extensions_enabled%22%3Afalse" +
                        "%2C%22c9s_tweet_anatomy_moderator_badge_enabled%22%3Atrue%2C%22tweetypie_unmention_optimization_enabled%22%3Atrue" +
                        "%2C%22responsive_web_edit_tweet_api_enabled%22%3Atrue" +
                        "%2C%22graphql_is_translatable_rweb_tweet_is_translatable_enabled%22%3Atrue%2C%22view_counts_everywhere_api_enabled%22%3Atrue" +
                        "%2C%22longform_notetweets_consumption_enabled%22%3Atrue%2C%22responsive_web_twitter_article_tweet_consumption_enabled%22%3Afalse" +
                        "%2C%22tweet_awards_web_tipping_enabled%22%3Afalse%2C%22freedom_of_speech_not_reach_fetch_enabled%22%3Atrue" +
                        "%2C%22standardized_nudges_misinfo%22%3Atrue%2C%22tweet_with_visibility_results_prefer_gql_limited_actions_policy_enabled%22%3Atrue" +
                        "%2C%22rweb_video_timestamps_enabled%22%3Atrue%2C%22longform_notetweets_rich_text_read_enabled%22%3Atrue" +
                        "%2C%22longform_notetweets_inline_media_enabled%22%3Atrue%2C%22responsive_web_media_download_video_enabled%22%3Afalse" +
                        "%2C%22responsive_web_enhance_cards_enabled%22%3Afalse%7D&fieldToggles=%7B%22withArticleRichContentState%22%3Afalse%7D"
        );
    }
}
