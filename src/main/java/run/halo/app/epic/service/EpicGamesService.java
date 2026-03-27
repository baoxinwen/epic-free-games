package run.halo.app.epic.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import run.halo.app.epic.vo.EpicGameVo;
import run.halo.app.epic.vo.EpicGamesResponse;

import java.time.Duration;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Epic 免费游戏服务
 */
@Service
public class EpicGamesService {

    private static final Logger log = LoggerFactory.getLogger(EpicGamesService.class);

    private static final String EPIC_API_URL = "https://uapis.cn/api/v1/game/epic-free";
    private static final ZoneId ET_ZONE = ZoneId.of("America/New_York");

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public EpicGamesService() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 获取免费游戏列表（带缓存）
     */
    @Cacheable(value = "epicGames", cacheManager = "caffeineCacheManager",
               key = "'games'", unless = "#result == null || #result.isEmpty()")
    public List<EpicGameVo> getFreeGames() {
        log.info("缓存未命中，从 Epic API 获取免费游戏数据");
        try {
            return fetchFromApiSync();
        } catch (Exception e) {
            log.error("获取 Epic 游戏数据失败: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
            return getFallbackData();
        }
    }

    /**
     * 强制刷新缓存
     */
    @CacheEvict(value = "epicGames", allEntries = true)
    public void evictCache() {
        log.info("清除 Epic 游戏缓存");
    }

    /**
     * 同步方式从外部 API 获取数据
     */
    private List<EpicGameVo> fetchFromApiSync() {
        log.info("正在请求 Epic API: {}", EPIC_API_URL);
        try {
            String body = webClient.get()
                    .uri(EPIC_API_URL)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));

            if (body == null) {
                log.warn("API 返回空响应");
                return List.of();
            }

            log.info("API 响应成功，响应长度: {} 字符", body.length());
            return parseResponse(body);
        } catch (Exception e) {
            log.error("请求 Epic API 异常: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
            return getFallbackData();
        }
    }

    /**
     * 解析 API 响应
     */
    private List<EpicGameVo> parseResponse(String responseBody) {
        try {
            EpicGamesResponse response = objectMapper.readValue(responseBody, EpicGamesResponse.class);
            if (response.data() == null) {
                return List.of();
            }

            return response.data().stream()
                    .map(this::toVo)
                    .toList();
        } catch (Exception e) {
            log.error("解析 Epic 响应数据失败: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * 转换为 VO 对象
     */
    private EpicGameVo toVo(EpicGamesResponse.EpicGameData data) {
        boolean isFreeNow = data.isFreeNow() != null ? data.isFreeNow() : true;
        String currentPrice = isFreeNow ? "免费" : "即将免费";

        return new EpicGameVo(
                data.id(),
                data.title(),
                data.description(),
                data.cover() != null ? data.cover() : "",
                data.originalPriceDesc() != null ? data.originalPriceDesc() :
                    (data.originalPrice() != null ? "¥" + data.originalPrice() : ""),
                currentPrice,
                data.seller(),
                isFreeNow,
                data.freeStart(),
                data.freeEnd(),
                data.freeStartAt(),
                data.freeEndAt(),
                data.link() != null ? data.link() : "https://store.epicgames.com/"
        );
    }

    /**
     * 计算缓存过期时间（秒）
     * 使用较短的 TTL，由 Caffeine 自动过期
     */
    public static long getCacheTtlSeconds() {
        LocalDateTime now = LocalDateTime.now(ET_ZONE);
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        // 周四（Epic 更新日）使用 30 分钟，其他天使用 1 小时
        return dayOfWeek == DayOfWeek.THURSDAY ? 1800 : 3600;
    }

    /**
     * 获取备用数据（当 API 失败时使用，日期动态计算）
     */
    private List<EpicGameVo> getFallbackData() {
        log.info("使用备用数据");
        List<EpicGameVo> fallback = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now(ET_ZONE);
        LocalDateTime nextThursday = now.with(DayOfWeek.THURSDAY);
        if (nextThursday.isBefore(now)) {
            nextThursday = nextThursday.plusWeeks(1);
        }

        long currentStart = nextThursday.minusWeeks(1).atZone(ET_ZONE).toInstant().toEpochMilli();
        long currentEnd = nextThursday.atZone(ET_ZONE).toInstant().toEpochMilli();
        long nextStart = nextThursday.atZone(ET_ZONE).toInstant().toEpochMilli();
        long nextEnd = nextThursday.plusWeeks(1).atZone(ET_ZONE).toInstant().toEpochMilli();

        String currentStartStr = nextThursday.minusWeeks(1).toLocalDate().toString() + " 00:00:00";
        String currentEndStr = nextThursday.toLocalDate().toString() + " 23:59:59";
        String nextStartStr = nextThursday.toLocalDate().toString() + " 00:00:00";
        String nextEndStr = nextThursday.plusWeeks(1).toLocalDate().toString() + " 23:59:59";

        fallback.add(new EpicGameVo(
                "epic-sample-1",
                "Mystery Game: The Next Epic Adventure",
                "本周神秘免费游戏！一款备受期待的冒险大作，探索未知世界，解开古老谜题。Epic 每周免费送，记得领取！",
                "https://picsum.photos/seed/epic1/640/400",
                "$59.99",
                "免费",
                "Epic Games",
                true,
                currentStartStr,
                currentEndStr,
                currentStart,
                currentEnd,
                "https://store.epicgames.com/"
        ));

        fallback.add(new EpicGameVo(
                "epic-sample-2",
                "Indie Gem: Hidden Horizons",
                "独立游戏佳作，讲述一个关于探索与发现的温暖故事。画风精美，音乐动人，不容错过！",
                "https://picsum.photos/seed/epic2/640/400",
                "$24.99",
                "即将免费",
                "Indie Studio",
                false,
                nextStartStr,
                nextEndStr,
                nextStart,
                nextEnd,
                "https://store.epicgames.com/"
        ));

        return fallback;
    }
}
