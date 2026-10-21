# 官方来源与阅读范围

核对日期：2026-10-21。链接用于定位原文；笔记采用概括和项目推导，没有复制整篇文档。实际开发时还需核对对应 MDK 和精确版本。

## Java 与构建

| 官方资料 | 本轮阅读范围 |
| --- | --- |
| [Dev.java 学习入口](https://dev.java/learn/) | 语言、API、JVM 与工具的主题分布；部分子页未能取得正文，具体机制改查 Oracle 文档 |
| [Oracle 接口与继承](https://docs.oracle.com/javase/tutorial/java/IandI/index.html) | 接口、实现、继承、多态与扩展契约 |
| [Oracle 泛型](https://docs.oracle.com/javase/tutorial/java/generics/index.html) | 编译期类型检查；注意 JDK 8 教程背景 |
| [Java 8 Map API](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html) | 键、迭代顺序和可变键约束 |
| [Oracle try-with-resources](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html) | AutoCloseable 与资源关闭路径 |
| [JLS 17：线程与锁](https://docs.oracle.com/javase/specs/jls/se17/html/jls-17.html) | 共享状态、同步和内存模型的基础；未逐条形式化验证 |
| [javac 17 手册](https://docs.oracle.com/en/java/javase/17/docs/specs/man/javac.html) | 编译输入、class 目标及 --release |
| [Gradle Toolchains](https://docs.gradle.org/current/userguide/toolchains.html) | 编译工具链、Gradle JVM 和交叉编译的区别 |
| [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) | 项目构建工具版本的固定与调用 |
| [Gradle Compatibility](https://docs.gradle.org/current/userguide/compatibility.html) | JVM 和工具链支持范围的核对入口 |

`current` 是浮动地址，不代表后续应自动升级项目 Wrapper。

## Forge：版本背景

| 官方资料 | 本轮阅读范围 |
| --- | --- |
| [1.12 开发入门](https://docs.minecraftforge.net/en/1.12.x/gettingstarted/) | Java 8、MDK、构建与调试背景 |
| [1.12 加载阶段](https://docs.minecraftforge.net/en/1.12.x/conventions/loadstages/) | preInit、init、postInit 与注册事件 |
| [1.20.1 开发入门](https://docs.minecraftforge.net/en/1.20.1/gettingstarted/) | Java 17、MDK、Wrapper、构建及两端运行 |
| [1.21.x 开发入门](https://docs.minecraftforge.net/en/1.21.x/gettingstarted/) | Java 21 要求和该分支背景 |

## Forge：运行机制

| 官方资料 | 本轮阅读范围 |
| --- | --- |
| [Mod Files](https://docs.minecraftforge.net/en/1.20.1/gettingstarted/modfiles/) | 元数据、依赖和 @Mod 入口 |
| [Lifecycle](https://docs.minecraftforge.net/en/1.20.1/concepts/lifecycle/) | 加载阶段与并行初始化边界 |
| [Sides](https://docs.minecraftforge.net/en/1.20.1/concepts/sides/) | 物理/逻辑两端和客户端类隔离 |
| [Events](https://docs.minecraftforge.net/en/1.20.1/concepts/events/) | 两种事件总线、注册与取消语义 |
| [Registries](https://docs.minecraftforge.net/en/1.20.1/concepts/registries/) | 稳定键、DeferredRegister 与时机 |
| [Capabilities](https://docs.minecraftforge.net/en/1.20.1/datastorage/capabilities/) | 附加功能、有效期、同步与玩家克隆 |
| [SavedData](https://docs.minecraftforge.net/en/1.20.1/datastorage/saveddata/) | 世界数据归属、加载和修改标记 |
| [Codecs](https://docs.minecraftforge.net/en/1.20.1/datastorage/codecs/) | 编解码、DataResult 和部分结果 |
| [SimpleImpl](https://docs.minecraftforge.net/en/1.20.1/networking/simpleimpl/) | 协议、发送者、线程和位置请求 |
| [Entity Synchronization](https://docs.minecraftforge.net/en/1.20.1/networking/entities/) | 生成数据和动态同步参数 |
| [Block States](https://docs.minecraftforge.net/en/1.20.1/blocks/states/) | 有限属性组合及状态与实例的区别 |
| [Block Entities](https://docs.minecraftforge.net/en/1.20.1/blockentities/) | 实例数据、tick、修改标记和同步 |
| [Menus](https://docs.minecraftforge.net/en/1.20.1/gui/menus/) | 交互容器和客户端画面分工 |
| [Client Resources](https://docs.minecraftforge.net/en/1.20.1/resources/client/) | assets 范围与客户端资源 |
| [Server Data](https://docs.minecraftforge.net/en/1.20.1/resources/server/) | data 范围与服务端资料 |
| [Game Tests](https://docs.minecraftforge.net/en/1.20.1/misc/gametest/) | 场景模板、测试和成功条件 |

## Minecraft 官方技术说明

| Mojang 资料 | 本轮核对内容 |
| --- | --- |
| [Java Edition 1.20.3](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-20-3) | tick 状态与速率管理 |
| [Java Edition 1.20.5](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-20-5) | Java 21、物品堆 components 与数据格式 |
| [Removing Obfuscation](https://www.minecraft.net/en-us/article/removing-obfuscation-in-java-edition) | 去除混淆的背景和官方协议声明 |
| [26.1 Snapshot 1](https://www.minecraft.net/en-us/article/minecraft-26-1-snapshot-1) | Java 25、新编号与无混淆构建 |

这些说明不构成某个加载器已支持对应正式版本的证明。

## 其他加载器的官方边界资料

- [NeoForge User Guide](https://docs.neoforged.net/user/docs/)：仅用于了解版本边界和与 Forge 分开核对的必要性。
- [NeoForged Primers](https://docs.neoforged.net/primer/docs/)：相邻版本迁移主题索引；尚未逐篇完成移植试验。

## 模组官方源码

已读取 Create、Botania、Applied Energistics 2、Mekanism 和 Ars Nouveau 的指定文件及各自 gradle.properties。固定提交与观察范围见 [源码定位记录](source-pins.json)，结论见 [源码案例](05-mod-source-cases.md)。

只阅读公开资料，不代表已经构建、测试或掌握这些模组的全部系统。仓库简介、第三方转载和玩家讨论没有作为具体实现结论的证据。
