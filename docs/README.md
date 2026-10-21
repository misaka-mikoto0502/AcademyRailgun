# 项目文档索引

更新日期：2026-10-21。项目处于设计讨论与技术验证阶段。开始下一轮工作前，先阅读当前边界、角色讨论稿和最新检查点。

## 当前有效的设计与状态

| 文档 | 阅读目的 |
| --- | --- |
| [项目边界](design/project-boundaries.md) | Minecraft 版本下限、角色与世界内容职责、独立推进要求 |
| [角色与能力设计讨论稿](design/character-design.md) | 用户已确认的方向、助手建议、待定问题和下一问 |
| [2026-10-21 检查点](checkpoints/2026-10-21.md) | 今天的对话推进顺序、工程实况与保存范围 |
| [迁移与环境状态](workspace-migration.md) | 独立目录、固定工具组合、实际验证与运行限制 |
| [原型架构](architecture.md) | 已有纯 Java 规则的能力及平台适配缺口 |
| [工具与仓库使用](../tools/README.md) | 本机验证、克隆后的环境准备、GitHub 上传范围 |

## 前置研究与学习资料

| 资料 | 定位 |
| --- | --- |
| [旧整合包调研](project-research.md) | 针对旧 Minecraft 1.12.2 实例和 AcademyCraft 的历史研究 |
| [十种玩法候选](design/research-content.md) | 可删减、组合的内容库，不是全部实施承诺 |
| [设计资料说明](design/README.md) | Markdown、Word 阅读版和生成脚本的对应关系 |
| [开发基础学习](learning/README.md) | Java、Minecraft、Forge/NeoForge、版本迁移和源码案例 |
| [官方资料来源](learning/sources.md) | 先前已阅读资料的入口与阅读范围 |
| [五个参考模组源码定位](learning/source-pins.json) | 固定提交、文件与实际阅读范围 |
| [本机游戏源码定位](../tools/source-reading.json) | 已生成游戏与 NeoForge 源码的阅读位置和哈希 |
| [迁移前工程入口](history/framework-readme-before-migration.md) | 迁移时保留的历史快照 |

## 工程对应入口

- [纯 Java 原型](../prototype-core/README.md)：通用规则与合成测试资料，尚未接入 Minecraft。
- [NeoForge 官方小样](../platform-probes/neoforge-1.21.1/README.md)：官方模板说明，仍为 Example Mod。
- [环境版本记录](../tools/environment-lock.json)：候选版本组合与检查范围。
- [迁移清单](../tools/migration-manifest.json)：迁移时的文件来源、目的地和 SHA256。

## 文档如何使用

项目边界约束整体范围，角色讨论稿承接最新设计对话；检查点记录当日状态。更新设计时同步修正相关摘要，并在需要时新增日期检查点。

前期调研与学习中的“本次”“本轮”按各篇的成文阶段理解，不能覆盖后续已取得的构建证据或用户新决定。原型架构描述现有代码，不反向决定玩法。助手建议只有在用户接受后才能写为确认规则。

Word 与布局 JSON 保留为前期阅读与排版资料，没有同步加入本轮角色讨论。历史文件中的路径按原样保留；当前执行入口以仓库根目录和工具说明为准。

本轮只整理 Markdown 与仓库入口，未重生成 Word，也未声称重新核对所有外部链接或第三方发布版本。
