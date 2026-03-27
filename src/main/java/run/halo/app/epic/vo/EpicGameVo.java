package run.halo.app.epic.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Epic 免费游戏数据视图对象
 */
public record EpicGameVo(

        @JsonProperty("id")
        String id,

        @JsonProperty("title")
        String title,

        @JsonProperty("description")
        String description,

        @JsonProperty("imageUrl")
        String imageUrl,

        @JsonProperty("originalPrice")
        String originalPrice,

        @JsonProperty("currentPrice")
        String currentPrice,

        @JsonProperty("seller")
        String seller,

        @JsonProperty("isFreeNow")
        Boolean isFreeNow,

        @JsonProperty("freeStart")
        String freeStart,

        @JsonProperty("freeEnd")
        String freeEnd,

        @JsonProperty("startDateTimestamp")
        Long startDateTimestamp,

        @JsonProperty("endDateTimestamp")
        Long endDateTimestamp,

        @JsonProperty("epicUrl")
        String epicUrl
) {
}
