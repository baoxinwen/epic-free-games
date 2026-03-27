package run.halo.app.epic.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Epic API 响应包装类
 */
public record EpicGamesResponse(

        @JsonProperty("message")
        String message,

        @JsonProperty("data")
        List<EpicGameData> data
) {

    /**
     * 单个游戏数据 - 匹配实际 API 返回格式
     */
    public record EpicGameData(

            @JsonProperty("id")
            String id,

            @JsonProperty("title")
            String title,

            @JsonProperty("cover")
            String cover,

            @JsonProperty("original_price")
            Integer originalPrice,

            @JsonProperty("original_price_desc")
            String originalPriceDesc,

            @JsonProperty("description")
            String description,

            @JsonProperty("seller")
            String seller,

            @JsonProperty("is_free_now")
            Boolean isFreeNow,

            @JsonProperty("free_start")
            String freeStart,

            @JsonProperty("free_start_at")
            Long freeStartAt,

            @JsonProperty("free_end")
            String freeEnd,

            @JsonProperty("free_end_at")
            Long freeEndAt,

            @JsonProperty("link")
            String link
    ) {
    }
}
