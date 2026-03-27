package run.halo.app.epic.endpoint;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

/**
 * Epic 免费游戏页面控制器
 */
@Controller
@RequestMapping("/epic-games")
public class EpicGamesController {

    @GetMapping
    public Mono<ResponseEntity<String>> servePage() {
        String html = """
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Epic 免费游戏</title>
    <style>
        *{margin:0;padding:0;box-sizing:border-box}
        html{scroll-behavior:smooth}
        body{
            font-family:-apple-system,"PingFang SC","Microsoft YaHei","Inter",Georgia,serif;
            min-height:100vh;
            background:linear-gradient(180deg,#fff9f0 0%,#f5f0e8 100%);
            color:#5a5a5a;
            line-height:1.8;
            -webkit-font-smoothing:antialiased
        }

        body::before,body::after{
            content:'';position:fixed;opacity:.03;pointer-events:none;z-index:0
        }
        body::before{
            top:100px;right:50px;width:300px;height:300px;
            background:url("data:image/svg+xml,%3Csvg viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M50 10c-5 10-15 15-25 15s-20-5-25-15c5 5 15 10 25 10s20-5 25-10c-5 15-15 25-25 35 15-5 25-15 30-30 5 15 15 25 30 30-10-10-20-15-30-15-5 10-15 15-25 15s-20-5-25-15z' fill='%234a7c59'/%3E%3C/svg%3E") no-repeat
        }
        body::after{
            bottom:100px;left:50px;width:250px;height:250px;
            background:url("data:image/svg+xml,%3Csvg viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cellipse cx='50' cy='50' rx='40' ry='20' transform='rotate(-30 50 50)' fill='%238b7355'/%3E%3C/svg%3E") no-repeat
        }

        .container{max-width:1120px;margin:0 auto;padding:40px 24px;position:relative;z-index:1}

        /* Header */
        .header{display:flex;align-items:center;justify-content:space-between;padding:20px 0 36px}
        .header-left{display:flex;align-items:center;gap:14px}
        .title{
            font-size:clamp(22px,4vw,30px);font-weight:500;
            color:#3d4a3d;letter-spacing:1px
        }
        .subtitle-badge{
            padding:4px 10px;background:#f0f5eb;border-radius:20px;
            font-size:11px;color:#6b8e6b;letter-spacing:.5px;white-space:nowrap
        }

        /* Refresh Button */
        .refresh-btn{
            display:inline-flex;align-items:center;gap:8px;
            padding:11px 28px;background:#fff;color:#6b8e6b;
            border:1.5px solid #d4e2d4;border-radius:50px;
            font-size:13px;font-weight:500;letter-spacing:1px;
            cursor:pointer;transition:all .3s ease;
            box-shadow:0 2px 12px rgba(74,125,89,.08)
        }
        .refresh-btn:hover{background:#f0f5eb;border-color:#a8b89c;transform:translateY(-2px);box-shadow:0 4px 20px rgba(74,125,89,.15)}
        .refresh-btn:active{transform:translateY(0)}
        .refresh-btn.is-loading{opacity:.6;pointer-events:none}
        .refresh-btn svg{width:15px;height:15px;transition:transform .4s ease}
        .refresh-btn:hover svg{transform:rotate(180deg)}
        .refresh-btn.is-loading svg{animation:spin 1s linear infinite}
        @keyframes spin{to{transform:rotate(360deg)}}

        /* Loading */
        .loading{display:flex;flex-direction:column;align-items:center;padding:80px 20px;gap:16px}
        .loading.hidden{display:none}
        .spinner{
            width:36px;height:36px;border:2.5px solid #e8e8e8;
            border-top-color:#a8b89c;border-radius:50%;animation:spin 1s linear infinite
        }
        .loading p{color:#9a9a8a;font-size:13px;font-weight:300;letter-spacing:1px}

        /* Error */
        .error{padding:20px;background:#fdf2f0;border:1px solid #f5e6e6;border-radius:16px;text-align:center;color:#c96;font-size:13px;max-width:360px;margin:0 auto}
        .error.hidden{display:none}

        /* Games Grid */
        .games-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(340px,1fr));gap:28px;padding-bottom:60px}

        /* Game Card */
        .game-card{
            background:#fff;border-radius:24px;overflow:hidden;
            border:1px solid #f0ebe3;
            box-shadow:0 4px 28px rgba(74,93,74,.08);
            transition:all .4s ease
        }
        .game-card:hover{transform:translateY(-8px);box-shadow:0 16px 48px rgba(74,93,74,.14);border-color:#e0d9cc}

        /* Card Image */
        .card-image{position:relative;width:100%;aspect-ratio:16/9;overflow:hidden;background:linear-gradient(135deg,#f8f6f3,#f0ebe3)}
        .card-image img{width:100%;height:100%;object-fit:cover;transition:transform .6s ease}
        .game-card:hover .card-image img{transform:scale(1.05)}

        /* Status Badge */
        .status-badge{
            position:absolute;top:14px;left:14px;
            padding:6px 16px;border-radius:20px;
            font-size:11px;font-weight:500;letter-spacing:1.5px;
            backdrop-filter:blur(10px);-webkit-backdrop-filter:blur(10px)
        }
        .status-badge.free{background:rgba(168,184,156,.92);color:#fff;box-shadow:0 2px 12px rgba(168,184,156,.35)}
        .status-badge.upcoming{background:rgba(255,255,255,.95);color:#6b8e6b;border:1px solid rgba(168,184,156,.4)}

        /* Price Badge */
        .price-badge{
            position:absolute;top:14px;right:14px;
            padding:5px 12px;border-radius:20px;
            background:rgba(255,255,255,.94);backdrop-filter:blur(10px);-webkit-backdrop-filter:blur(10px);
            font-size:11px;font-weight:500;color:#aaa;
            text-decoration:line-through
        }

        /* Card Body */
        .card-body{padding:22px 24px 26px}
        .game-title{font-size:18px;font-weight:500;color:#3d4a3d;line-height:1.5;margin-bottom:4px}
        .game-seller{font-size:12px;color:#9a9a8a;margin-bottom:12px}
        .game-desc{font-size:13px;color:#7a8a7a;line-height:1.7;margin-bottom:16px;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden}

        /* Info Grid */
        .info-grid{display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-bottom:14px}
        .info-item{
            display:flex;align-items:center;gap:6px;
            padding:8px 10px;background:#faf8f5;border-radius:12px;font-size:12px
        }
        .info-item svg{width:14px;height:14px;color:#b8c4a8;flex-shrink:0}
        .info-label{color:#b0b8a8;white-space:nowrap}
        .info-value{color:#5a6a5a;font-weight:500}

        /* Countdown */
        .countdown{
            display:flex;align-items:center;gap:8px;
            padding:10px 14px;background:linear-gradient(135deg,#f8f9f4,#f0f5eb);
            border-radius:14px;font-size:12px;color:#7a8a7a;margin-bottom:14px
        }
        .countdown svg{width:14px;height:14px;color:#a8b89c;flex-shrink:0}
        .countdown-text{font-weight:500;color:#5a6a5a;font-variant-numeric:tabular-nums}

        /* CTA Button */
        .cta-btn{
            display:flex;align-items:center;justify-content:center;gap:8px;
            width:100%;padding:14px;border:none;border-radius:16px;
            font-size:13px;font-weight:500;letter-spacing:1px;
            cursor:pointer;transition:all .3s ease;text-decoration:none
        }
        .cta-btn svg{width:16px;height:16px}
        .cta-btn.primary{
            background:linear-gradient(135deg,#a8b89c,#9aaa8e);color:#fff;
            box-shadow:0 3px 16px rgba(168,184,156,.3)
        }
        .cta-btn.primary:hover{box-shadow:0 5px 24px rgba(168,184,156,.45);transform:translateY(-2px)}
        .cta-btn.secondary{
            background:#faf8f5;color:#6b8e6b;
            border:1.5px solid #d4e2d4
        }
        .cta-btn.secondary:hover{background:#f0f5eb;border-color:#a8b89c}

        /* Empty State */
        .empty-state{text-align:center;padding:80px 20px;background:#fff;border-radius:24px;border:1px solid #f0ebe3}
        .empty-state .empty-icon{width:52px;height:52px;margin:0 auto 16px;border-radius:14px;background:#f0f5eb;display:flex;align-items:center;justify-content:center}
        .empty-state .empty-icon svg{width:24px;height:24px;color:#a8b89c}
        .empty-state h3{font-size:17px;font-weight:500;color:#4a5d4a;margin-bottom:6px}
        .empty-state p{color:#9a9a8a;font-size:13px}

        /* Footer */
        .footer{text-align:center;padding:32px 20px;color:#9a9a8a;font-size:12px;position:relative}
        .footer::before{
            content:'';display:block;width:40px;height:1px;
            background:#e0d9cc;margin:0 auto 16px
        }
        .footer a{color:#6b8e6b;text-decoration:none;font-weight:400}
        .footer a:hover{text-decoration:underline}

        /* Leaf Decorations */
        .leaf-deco{
            position:fixed;font-size:18px;opacity:.12;
            pointer-events:none;z-index:0;
            animation:floatLeaf 8s ease-in-out infinite
        }
        .leaf-1{top:15%;left:5%;animation-delay:0s}
        .leaf-2{top:55%;right:6%;animation-delay:2.5s}
        .leaf-3{bottom:20%;left:12%;animation-delay:5s}
        @keyframes floatLeaf{
            0%,100%{transform:translateY(0) rotate(0deg)}
            50%{transform:translateY(-12px) rotate(8deg)}
        }

        /* Responsive */
        @media(max-width:768px){
            .container{padding:24px 16px}
            .games-grid{grid-template-columns:1fr;gap:20px}
            .header{padding:32px 0 28px}
            .leaf-deco{display:none}
        }
    </style>
</head>
<body>
    <div class="leaf-deco leaf-1">🍃</div>
    <div class="leaf-deco leaf-2">🌿</div>
    <div class="leaf-deco leaf-3">🍃</div>

    <div class="container">
        <header class="header">
            <div class="header-left">
                <h1 class="title">Epic 免费游戏</h1>
                <span class="subtitle-badge">每周更新</span>
            </div>
            <button class="refresh-btn" id="refreshBtn">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
                    <path d="M3 3v5h5"/>
                    <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16"/>
                    <path d="M16 21h5v-5"/>
                </svg>
                刷新
            </button>
        </header>

        <div id="loading" class="loading">
            <div class="spinner"></div>
            <p>正在获取游戏信息...</p>
        </div>

        <div id="error" class="error hidden"></div>
        <div id="games-container" class="games-grid"></div>
    </div>

    <footer class="footer">
        <p>数据来源 <a href="https://www.epicgames.com/store" target="_blank" rel="noopener">Epic Games Store</a> · 插件 v1.0.0</p>
    </footer>

    <script>
    (function(){
        var gamesContainer=document.getElementById("games-container"),
            loadingEl=document.getElementById("loading"),
            errorEl=document.getElementById("error"),
            refreshBtn=document.getElementById("refreshBtn"),
            timerMap=new Map();

        function init(){
            if(refreshBtn)refreshBtn.addEventListener("click",handleRefresh);
            loadGames();
        }

        function handleRefresh(){
            if(refreshBtn.classList.contains("is-loading"))return;
            refreshBtn.classList.add("is-loading");
            loadGames(true).finally(function(){
                refreshBtn.classList.remove("is-loading");
            });
        }

        function loadGames(forceRefresh){
            showLoading();hideError();
            var url=forceRefresh?"/epic-games/api/games?refresh=true":"/epic-games/api/games";
            return fetch(url).then(function(r){return r.json()}).then(function(data){
                if(data&&data.length>0)renderGames(data);
                else renderEmpty();
            }).catch(function(e){
                showError("无法获取游戏数据，请稍后重试");
            }).finally(function(){hideLoading()});
        }

        function renderGames(games){
            timerMap.forEach(function(t){clearInterval(t)});timerMap.clear();
            gamesContainer.innerHTML=games.map(createGameCard).join("");
            games.forEach(function(g){
                if(g.endDateTimestamp&&g.isFreeNow)startCountdown(g.endDateTimestamp,"end");
                else if(g.startDateTimestamp)startCountdown(g.startDateTimestamp,"start");
            });
        }

        function createGameCard(game){
            var t=game.title||"未知游戏",
                desc=game.description||"暂无描述",
                img=game.imageUrl||"",
                op=game.originalPrice||"",
                cp=game.currentPrice||"免费",
                seller=game.seller||"",
                fs=game.freeStart||"",
                fe=game.freeEnd||"",
                et=game.endDateTimestamp,
                st=game.startDateTimestamp,
                fn=game.isFreeNow===true,
                url=game.epicUrl||"https://store.epicgames.com/";

            var startHtml=fs?'<div class="info-item"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg><span class="info-label">开始</span><span class="info-value">'+esc(fs)+'</span></div>':'';
            var endHtml=fe?'<div class="info-item"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg><span class="info-label">结束</span><span class="info-value">'+esc(fe)+'</span></div>':'';

            var countdownHtml="";
            if(fn&&et){
                countdownHtml='<div class="countdown" data-type="end" data-timestamp="'+et+'"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg><span class="countdown-text">剩余: 计算中...</span></div>';
            }else if(!fn&&st){
                countdownHtml='<div class="countdown" data-type="start" data-timestamp="'+st+'"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg><span class="countdown-text">距开始: 计算中...</span></div>';
            }

            var badgeClass=fn?'free':'upcoming';
            var badgeText=fn?'免费领取':'即将免费';
            var priceHtml=op?'<span class="price-badge">'+esc(op)+'</span>':'';
            var ctaClass=fn?'primary':'secondary';
            var ctaText=fn?'前往领取':'查看详情';
            var ctaSvg=fn?'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>':'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/></svg>';

            return '<div class="game-card">'
                +'<div class="card-image">'
                +(img?'<img src="'+esc(img)+'" alt="'+esc(t)+'" loading="lazy">':'')
                +'<span class="status-badge '+badgeClass+'">'+badgeText+'</span>'
                +priceHtml
                +'</div>'
                +'<div class="card-body">'
                +'<h3 class="game-title">'+esc(t)+'</h3>'
                +(seller?'<p class="game-seller">'+esc(seller)+'</p>':'')
                +'<p class="game-desc">'+esc(desc)+'</p>'
                +'<div class="info-grid">'+startHtml+endHtml+'</div>'
                +countdownHtml
                +'<a href="'+esc(url)+'" target="_blank" rel="noopener" class="cta-btn '+ctaClass+'">'
                +ctaSvg+ctaText
                +'</a>'
                +'</div></div>';
        }

        function startCountdown(timestamp,type){
            var label=type==="start"?"距开始":"剩余";
            var key=timestamp+"_"+type;
            function update(){
                var els=document.querySelectorAll('[data-timestamp="'+timestamp+'"][data-type="'+type+'"]');
                if(!els.length)return;
                var diff=timestamp-Date.now();
                if(diff<=0){
                    els.forEach(function(el){
                        var t=el.querySelector(".countdown-text");
                        if(t)t.textContent=label==="start"?"已开始":"已结束";
                    });
                    clearInterval(timerMap.get(key));return;
                }
                var d=Math.floor(diff/86400000),h=Math.floor(diff%86400000/3600000),m=Math.floor(diff%3600000/60000),s=Math.floor(diff%60000/1000);
                var txt=d>0?d+"天 "+h+"时 "+m+"分":pad(h)+":"+pad(m)+":"+pad(s);
                els.forEach(function(el){
                    var t=el.querySelector(".countdown-text");
                    if(t)t.textContent=label+": "+txt;
                });
            }
            update();
            timerMap.set(key,setInterval(update,1000));
        }

        function pad(n){return n<10?"0"+n:""+n}

        function renderEmpty(){
            if(!gamesContainer)return;
            gamesContainer.innerHTML='<div class="empty-state"><div class="empty-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 11h4"/><path d="M8 9v4"/><path d="m15 12 2 2 4-4"/><path d="M17.5 21H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z"/></svg></div><h3>暂无免费游戏</h3><p>当前没有可领取的免费游戏，请稍后再来看看</p></div>';
        }

        function showLoading(){if(loadingEl)loadingEl.classList.remove("hidden");if(gamesContainer)gamesContainer.innerHTML=""}
        function hideLoading(){if(loadingEl)loadingEl.classList.add("hidden")}
        function showError(msg){if(errorEl){errorEl.textContent=msg;errorEl.classList.remove("hidden")}}
        function hideError(){if(errorEl)errorEl.classList.add("hidden")}

        function esc(text){
            var d=document.createElement("div");d.textContent=text||"";return d.innerHTML;
        }

        document.readyState==="loading"?document.addEventListener("DOMContentLoaded",init):init();
        window.addEventListener("beforeunload",function(){timerMap.forEach(function(t){clearInterval(t)});timerMap.clear()});
    })();
    </script>
</body>
</html>
            """;

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html));
    }
}
