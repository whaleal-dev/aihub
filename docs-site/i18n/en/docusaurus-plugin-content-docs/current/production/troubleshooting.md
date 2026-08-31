---
title: 排障
description: 客户端调用失败时先查密钥、baseUrl、模型名和 PlatformType。
tags: [how-to]
---

# 排障

| 现象 | 先查 |
| --- | --- |
| 401 / 403 | API Key、是否误用别的平台 key |
| 404 / 模型不存在 | 模型名、baseUrl 是否带正确 path |
| 超时 | 网络、代理、`timeout` |
| SSE 无事件 | 是否走了流式接口、监听器是否绑定 |
| 解析异常 | 厂商响应是否与当前实体字段不一致 |

不要把完整密钥贴到 Issue。可提供 provider、baseUrl 类型和模型名。

更多见 [Java 快速开始](/docs/getting-started/quickstart-java)。
