package run.halo.app.epic;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.halo.app.epic.service.EpicGamesService;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;


/**
 * Epic 免费游戏插件
 */
@Configuration
public class EpicFreeGamesPlugin extends BasePlugin {

    public EpicFreeGamesPlugin(PluginContext pluginContext) {
        super(pluginContext);
    }

    /**
     * 配置 Caffeine 缓存管理器
     * 使用自定义 Expiry 实现动态 TTL：周四 30 分钟，其他天 60 分钟
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfter(new Expiry<Object, Object>() {
                    @Override
                    public long expireAfterCreate(Object key, Object value, long currentTime) {
                        return EpicGamesService.getCacheTtlSeconds() * 1_000_000_000L;
                    }

                    @Override
                    public long expireAfterUpdate(Object key, Object value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(Object key, Object value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                }));
        return cacheManager;
    }
}
