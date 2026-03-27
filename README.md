# Epic 免费游戏

[![CI](https://github.com/baoxinwen/epic-free-games/actions/workflows/ci.yml/badge.svg)](https://github.com/baoxinwen/epic-free-games/actions/workflows/ci.yml)
[![Release](https://github.com/baoxinwen/epic-free-games/actions/workflows/cd.yml/badge.svg)](https://github.com/baoxinwen/epic-free-games/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/baoxinwen/epic-free-games/blob/main/LICENSE)

一款 [Halo 2.x](https://halo.run) 插件，展示 Epic Games 商店当周免费游戏，区分当前免费与即将免费，并提供实时倒计时。

## 功能特性

- 展示 Epic Games 商店当周免费游戏信息
- 区分「免费领取中」与「即将免费」两种状态
- 实时倒计时显示剩余免费时间或距开始时间
- 智能缓存策略（周四 Epic 更新日自动缩短缓存时间）
- 响应式布局，适配桌面端与移动端
- 支持手动刷新获取最新数据

## 安装

### 通过应用市场安装

在 Halo 后台 `插件` -> `安装` 中搜索 `Epic 免费游戏`。

### 手动安装

1. 前往 [Releases](https://github.com/baoxinwen/epic-free-games/releases) 下载最新 JAR 文件
2. 登录 Halo 后台，进入 `插件` -> `安装` -> `上传`
3. 选择下载的 JAR 文件，点击安装并启用

### 开发

```bash
git clone https://github.com/baoxinwen/epic-free-games.git
cd epic-free-games
```

### 构建

```
./gradlew build
```

## 使用

安装并启用插件后，访问 `https://your-domain/epic-games` 即可查看。

**路由说明：**

| 路径 | 说明 |
|------|------|
| `/epic-games` | 前端展示页面 |
| `/epic-games/api/games` | JSON 数据接口，支持 `?refresh=true` 强制刷新 |

**缓存策略：**

| 场景 | 缓存时间 |
|------|---------|
| 周四（Epic 更新日） | 30 分钟 |
| 其他日期 | 60 分钟 |
| 手动刷新 | 立即清除 |

## 技术栈

- Java 21
- Spring Boot 3.x / WebFlux
- Caffeine 缓存
- 原生 HTML + CSS + JavaScript（内联渲染）

## 数据来源

免费游戏数据来自 [uapis.cn](https://uapis.cn) API。

## 参与贡献

欢迎提交 [Issue](https://github.com/baoxinwen/epic-free-games/issues) 和 Pull Request。

## 许可证

[MIT](LICENSE)
