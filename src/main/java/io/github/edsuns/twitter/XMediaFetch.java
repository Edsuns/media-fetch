package io.github.edsuns.twitter;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.edsuns.common.ObjectMapperFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static io.github.edsuns.common.HttpRequests.*;

/**
 * @author edsuns@qq.com
 * @since 2024/1/14 17:13
 */
@ParametersAreNonnullByDefault
public class XMediaFetch {

    private static final String API_URL = "https://x.com/i/api/graphql/iP4-On5YPLPgO9mjKRb2Gg/TweetDetail";
    private static final String API_QUERY_TEMPLATE = "?variables=" + URLEncoder.encode(
            "{\"focalTweetId\":\"{{STATUS_ID}}\",\"with_rux_injections\":false,\"rankingMode\":\"Relevance\",\"includePromotedContent\":true,\"withCommunity\":true,\"withQuickPromoteEligibilityTweetFields\":true,\"withBirdwatchNotes\":true,\"withVoice\":true}",
            StandardCharsets.UTF_8
    ) + "&features=" + URLEncoder.encode(
            "{\"profile_label_improvements_pcf_label_in_post_enabled\":false,\"rweb_tipjar_consumption_enabled\":true,\"responsive_web_graphql_exclude_directive_enabled\":true,\"verified_phone_label_enabled\":false,\"creator_subscriptions_tweet_preview_api_enabled\":true,\"responsive_web_graphql_timeline_navigation_enabled\":true,\"responsive_web_graphql_skip_user_profile_image_extensions_enabled\":false,\"premium_content_api_read_enabled\":false,\"communities_web_enable_tweet_community_results_fetch\":true,\"c9s_tweet_anatomy_moderator_badge_enabled\":true,\"responsive_web_grok_analyze_button_fetch_trends_enabled\":false,\"articles_preview_enabled\":true,\"responsive_web_edit_tweet_api_enabled\":true,\"graphql_is_translatable_rweb_tweet_is_translatable_enabled\":true,\"view_counts_everywhere_api_enabled\":true,\"longform_notetweets_consumption_enabled\":true,\"responsive_web_twitter_article_tweet_consumption_enabled\":true,\"tweet_awards_web_tipping_enabled\":false,\"creator_subscriptions_quote_tweet_preview_enabled\":false,\"freedom_of_speech_not_reach_fetch_enabled\":true,\"standardized_nudges_misinfo\":true,\"tweet_with_visibility_results_prefer_gql_limited_actions_policy_enabled\":true,\"rweb_video_timestamps_enabled\":true,\"longform_notetweets_rich_text_read_enabled\":true,\"longform_notetweets_inline_media_enabled\":true,\"responsive_web_enhance_cards_enabled\":false}",
            StandardCharsets.UTF_8
    ) + "&fieldToggles=" + URLEncoder.encode(
            "{\"withArticleRichContentState\":true,\"withArticlePlainText\":false,\"withGrokAnalyze\":false,\"withDisallowedReplyControls\":false}",
            StandardCharsets.UTF_8
    );
    private static final String API_QUERY_TEMPLATE_PLACEHOLDER_STATUS_ID = URLEncoder.encode("{{STATUS_ID}}", StandardCharsets.UTF_8);

    private static final JsonPointer POINTER_INSTRUCTIONS = JsonPointer.compile("/data/threaded_conversation_with_injections_v2/instructions");
    private static final JsonPointer POINTER_ENTRY_TYPE = JsonPointer.compile("/content/entryType");
    private static final JsonPointer POINTER_MEDIA_1 = JsonPointer.compile("/content/itemContent/tweet_results/result/tweet/legacy/entities/media");
    private static final JsonPointer POINTER_MEDIA_2 = JsonPointer.compile("/content/itemContent/tweet_results/result/legacy/entities/media");

    private final InetSocketAddress proxyAddress;
    private final String authToken;
    private final String bearerToken;
    private final CookieManager cookieManager;
    private final HttpClient client;

    public XMediaFetch(InetSocketAddress proxyAddress, String authToken, String bearerToken) {
        this.proxyAddress = proxyAddress;
        this.authToken = authToken;
        this.bearerToken = composeBearerToken(bearerToken);
        this.cookieManager = new CookieManager();
        this.client = createClient();
    }

    private HttpClient createClient() {
        return HttpClient.newBuilder()
                .proxy(ProxySelector.of(proxyAddress))
                .cookieHandler(cookieManager)
                .build();
    }

    public List<XMedia> fetchMediaByStatusId(String id) throws IOException, InterruptedException {
        String authTokenCookieName = "auth_token";
        String csrfTokenCookieName = "ct0";

        URI statusUrl = statusUrl(id);
        putCookies(cookieManager, statusUrl, authTokenCookieName, authToken);
        client.send(
                HttpRequest.newBuilder(statusUrl)
                        .headers(
                                HEADER_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9",
                                HEADER_ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9",
                                HEADER_USER_AGENT, USER_AGENT
                        )
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.discarding()
        );
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
                HttpRequest.newBuilder(detailUrl)
                        .headers(
                                HEADER_AUTHORIZATION, bearerToken,
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
        ArrayNode instructions = root.withArray(POINTER_INSTRUCTIONS);
        if (!instructions.isMissingNode()) {
            ArrayNode mediaArray = StreamSupport.stream(instructions.spliterator(), false)
                    .filter(x -> Objects.equals(x.get("type").asText(), "TimelineAddEntries"))
                    .map(addEntriesInstruction -> (ArrayNode) addEntriesInstruction.withArray("entries"))
                    .flatMap(entries -> StreamSupport.stream(entries.spliterator(), false))
                    .filter(x -> Objects.equals(x.at(POINTER_ENTRY_TYPE).asText(), "TimelineTimelineItem"))
                    .map(x -> Optional.of(x.withArray(POINTER_MEDIA_1))
                            .filter(media -> !media.isEmpty())
                            .orElseGet(() -> x.withArray(POINTER_MEDIA_2)))
                    .findFirst().orElse(null);
            if (mediaArray != null) {
                List<XMedia> mediaList = mapper.readValue(mediaArray.traverse(), new TypeReference<>() { });
                if (mediaList != null) {
                    for (XMedia media : mediaList) {
                        if (XMedia.Type.photo == media.getType() && media.getMediaUrlHttps() != null) {
                            media.setMediaUrlHttps(media.getMediaUrlHttps() + "?name=large");
                        }
                    }
                }
                return mediaList;
            }
        }
        return Collections.emptyList();
    }

    private static String composeBearerToken(String bearerToken) {
        String prefix = "Bearer ";
        if (bearerToken.startsWith(prefix)) {
            return bearerToken;
        }
        return prefix + bearerToken;
    }

    private static URI statusUrl(String id) {
        return makeURI("https://twitter.com/zuoyinan888/status/" + id);
    }

    private static URI detailUrl(String id) {
        return makeURI(API_URL + API_QUERY_TEMPLATE.replace(API_QUERY_TEMPLATE_PLACEHOLDER_STATUS_ID, id));
    }
}
