package run.halo.app.epic.endpoint;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.epic.service.EpicGamesService;
import run.halo.app.epic.vo.EpicGameVo;

import java.util.List;

/**
 * Epic 免费游戏 API 端点
 */
@RestController
@RequestMapping("/epic-games/api")
public class EpicGamesEndpoint {

    private final EpicGamesService epicGamesService;

    public EpicGamesEndpoint(EpicGamesService epicGamesService) {
        this.epicGamesService = epicGamesService;
    }

    /**
     * 获取游戏列表 API
     */
    @GetMapping("/games")
    public ResponseEntity<List<EpicGameVo>> getGames(
            @RequestParam(value = "refresh", defaultValue = "false") boolean refresh) {
        if (refresh) {
            epicGamesService.evictCache();
        }
        List<EpicGameVo> games = epicGamesService.getFreeGames();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(games);
    }
}
