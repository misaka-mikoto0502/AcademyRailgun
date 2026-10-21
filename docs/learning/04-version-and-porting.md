# 版本变化与移植边界

核对日期：2026-10-21。此表用于说明技术变化，不是推荐版本排名，也不是完整兼容列表。

## 已核对的代表性版本

| 资料背景 | 已核对的要求或变化 | 使用边界 |
| --- | --- | --- |
| 本地 Minecraft 1.12.2／Forge 14.23.5.2837 | 本地版本配置确认；对应官方开发资料要求 Java 8 | 用于研究现有整合包；尚未在本机建立旧 MDK |
| Forge 1.20.1 文档 | 开发要求 Java 17；现代注册、事件总线和 Capability 例子 | 作为本轮主要现代学习样本 |
| Minecraft 1.20.5 | 官方说明要求 Java 21，物品堆数据转向结构化 components | 是需要单独处理的数据/API 迁移边界 |
| Forge 1.21.x 文档 | 当前阅读内容要求 Java 21 | 这是该文档分支的说明，不能扩展成所有未来版本的规则 |
| Minecraft 26.1 Snapshot 1 | 官方说明要求 Java 25，并说明后续构建去除混淆 | 本次核对的是这份技术说明，不据此宣称某个 Forge 已支持目标正式版 |

来源：[Forge 1.12](https://docs.minecraftforge.net/en/1.12.x/gettingstarted/)、[Forge 1.20.1](https://docs.minecraftforge.net/en/1.20.1/gettingstarted/)、[Minecraft 1.20.5](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-20-5)、[Forge 1.21.x](https://docs.minecraftforge.net/en/1.21.x/gettingstarted/)、[26.1 Snapshot 1](https://www.minecraft.net/en-us/article/minecraft-26-1-snapshot-1)

`latest`、`current` 与 `1.21.x` 都不是固定补丁版本。建工程时要再次核对下载页面、MDK 配置和实际依赖，记录完整版本组合。

## 1. 移植不只是修改 imports

版本变化可能涉及：启动入口和加载时机、注册对象的取得方式、网络包格式、玩家附加数据、物品数据、配方和模型文件、渲染管线以及构建插件。即使 Java 核心代码还能编译，游戏适配层也可能需要重新设计。

NeoForged 的版本迁移资料按相邻游戏版本组织，并区分原版变化与加载器变化。可将其用于寻找需要核对的主题，但不能将其中的 NeoForge API 当作 Forge API。[迁移资料](https://docs.neoforged.net/primer/docs/)

## 2. 物品数据的变化与玩家能力数据不同

1.20.5 的 structured components 变化主要涉及物品堆及相关数据表达；它不是“所有玩家成长数据都应改放到物品组件”的结论。[官方 1.20.5 技术变化](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-20-5)

项目推导：角色进度、道具附加数据、世界网络和临时会话各自选择归属。迁移其中一种存储机制时，避免牵动无关数据。

## 3. 映射方式也会变化

Mojang 先公开映射，之后又宣布去除混淆；26.1 Snapshot 1 的说明明确落实了新的构建方式。旧项目的反混淆流程不能直接当作未来所有版本的固定步骤。[去除混淆公告](https://www.minecraft.net/en-us/article/removing-obfuscation-in-java-edition)、[26.1 技术说明](https://www.minecraft.net/en-us/article/minecraft-26-1-snapshot-1)

去除混淆不意味着 Minecraft 游戏源码或资产变成任意可再发布的开源资源；官方公告也说明原有协议没有因此改变。这里仅记录官方声明，不作额外许可推断。

## 4. Forge、NeoForge 与 Fabric 分别核对

这些加载器不能仅凭相似的类名和概念混用。源码案例中的不同版本分支可能选择不同平台；以具体分支的 imports、构建配置和依赖为准。NeoForged 自己的用户指南也把 1.20.1 与后续版本分开建议，但这不决定本项目选型。[NeoForge 用户指南](https://docs.neoforged.net/user/docs/)

目前只把其他加载器作为比较和移植边界，没有把本项目切换到它们，也没有完成它们的系统 API 研究。

## 5. 未来选定版本时要得到的证据

1. 确认精确 Minecraft 版本、加载器构建、MDK 和依赖模组。
2. 固定 Gradle Wrapper、开发插件、Java 工具链和映射背景。
3. 建立最小模组，分别验证客户端与专用服务器能加载。
4. 接入一个可保存、可同步的玩家字段，再验证重连与重生。
5. 接入一个合成动作，验证服务端结算和观察者显示。
6. 最后再迁移真实内容，并用旧数据样本验证升级路径。

这些步骤是验证方法，具体游戏玩法仍然开放。
