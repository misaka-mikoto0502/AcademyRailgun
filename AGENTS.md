# 开发约定

本目录是炮姐主题 Mod 的独立开发根目录。旧整合包只作研究参照，不把它的 mods、config、saves 或 .minecraft 带入工程。

- 项目边界见 `docs/design/project-boundaries.md`：Minecraft Java 版至少 1.20；角色能力与世界内容独立推进；正式平台尚未锁定。
- 最新角色讨论见 `docs/design/character-design.md`，日期快照见 `docs/checkpoints/2026-10-21.md`。区分用户确认、助手建议和未决问题。
- 当前采用问答方式确定设计；用户明确确认方案后再进入主题玩法实现。不得把“整理文件”或已有原型当成启动玩法开发的授权。
- 已确认可培养多名角色、当前一主一副、换搭配保留进度、限制各能力者携带技能数量；角色获取重视与原版进程结合的科技树及变身器。具体数量、科技节点和变身器职责仍待讨论；抽卡延后且不作为角色获取主要方式。
- `prototype-core` 是前期纯 Java 原型；`platform-probes/neoforge-1.21.1` 是官方技术验证模板。二者均不能当成已完成的主题玩法，也不必因已有目录而锁定最终架构。
- 已用 JDK 21 通过 NeoForge 1.21.1 官方小样构建、数据启动和运行文件准备。不要由模板示例方块推导产品需求。
- 平台构建入口为 `tools/Invoke-ProbeGradle.ps1`，便携工具与缓存放在本目录，不修改系统 Java 配置。工具和克隆准备见 `tools/README.md`。
- `.tools`、`.gradle-user-home`、构建产物、运行存档及生成的 Minecraft 源码不纳入 Git。官方 Gradle Wrapper JAR 和前期 Word 阅读版有意保留。
- 验证结果与限制记录在 `docs/workspace-migration.md`。区分编译、数据生成、客户端和专用服务器各自实际完成的检查。
- 官方模板已有自己的许可文件，应保留。后续自建代码不从旧 Mod 复制贴图、模型或音频。
- 前期调研、源码定位、迁移清单保留其历史身份；Word 是前期阅读快照，当前设计以 Markdown 为准。更新设计时同步当前入口与相关摘要。
