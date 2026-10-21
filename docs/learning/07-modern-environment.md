# 现代开发环境与设计影响

更新日期：2026-10-21。当前技术小样为Minecraft1.21.1与NeoForge21.1.264，正式产品平台仍待选定。

## 已阅读的实际工程

官方MDK固定提交：`f7002b1b6011723c7b12fde95147689491a6ddab`。

本地入口：`platform-probes/neoforge-1.21.1/`。已读 `build.gradle`、`settings.gradle`、`gradle.properties`、`gradlew.bat`、`ExampleMod.java`、`ExampleModClient.java`及模组元数据模板。

- 构建工具管理NeoForge、Minecraft和映射依赖，开发目录不必来自已安装的整合包。
- 示例通过DeferredRegister与模组事件总线登记内容，并在通用入口监听运行时事件。
- 客户端示例入口带Dist.CLIENT限制；界面与客户端Minecraft类不应被专用服务器侧加载。
- 模组元数据声明游戏与加载器依赖。文件夹拆开、Java包名不同，都不自动意味着两个模组能够独立加载。

这是对示例组织方式的观察，不是新项目必须采用示例物品、方块与配置的要求。本地生成源码的后续阅读结果见本文末尾；阅读范围以tools/source-reading.json为准。

## 对角色部分的影响

角色定义、玩家实例与客户端外观分开。通用注册对象不保存某个玩家的技能余额；角色数据不能放进共享静态字段。先让服务端管理规则，再按对象可见范围同步外观与演出。

不要求先写专属设备或怪物来驱动成长。技能如需弹射物，可作为角色模块内部实现；只有具备独立世界用途的内容才归第二部分。

## 已核对的版本差异

NeoForge1.21.1的数据附件需要配置持久化；死亡继承需要明确策略。版本化网页仍描述自行发包同步，但本次锁定的21.1.264源码已提供可选的AttachmentType.Builder.sync同步API。因此不能依据网页断言该构建必须手写所有附件同步包；应核对具体构建的接收者、编码与更新机制。角色存档、死亡恢复和网络反馈仍分别验证。[版本化文档](https://docs.neoforged.net/docs/1.21.1/datastorage/attachments/)

该版本Payload文档说明处理器默认在主线程执行，显式切到网络线程后则需按需切回主线程。不能直接照搬旧Forge中对默认线程的假设；无论在哪个线程，技能请求仍需校验真实发送者、目标与资源。[Payload官方文档](https://docs.neoforged.net/docs/1.21.1/networking/payload/)

单人也包含逻辑服务端；单机运行成功不能代替专用服务器验证。角色渲染、快捷键与界面只能作为客户端适配，不能混入服务器必需类。[Sides官方文档](https://docs.neoforged.net/docs/1.21.1/concepts/sides/)

注册顺序与时机由平台管理。第二部分的方块、实体定义使用稳定ID；跨模块的数据定义以可选引用处理，不在角色基础初始化时强制访问未安装内容。[Registries官方文档](https://docs.neoforged.net/docs/1.21.1/concepts/registries/)

## 下一批源码阅读目标

依赖生成后按真实需求阅读：ServerPlayer及玩家恢复、Entity与LivingEntity生命周期、伤害入口、ItemStack数据组件、同步与附件、客户端玩家渲染。每项记录所读方法和设计影响，不把阅读局部源码表述为完全掌握游戏。

工程状态与实际验证结果见 [迁移与环境状态](../workspace-migration.md)。本次没有承诺整个Minecraft源码已理解，也没有完成具体角色技能实现。

## 来源

- [NeoForge1.21.1开发环境及源码位置](https://docs.neoforged.net/docs/1.21.1/gettingstarted/)
- [官方1.21.1 MDK模板](https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle/tree/f7002b1b6011723c7b12fde95147689491a6ddab)
- [Forge1.20.x开发环境](https://docs.minecraftforge.net/en/1.20.x/gettingstarted/)

JDK与Gradle精确来源、传输镜像及官方SHA256记录保存在 `tools/jdk-source.json`、`tools/gradle-source.json`。版本化文档用于解释规则，实际工程仍需运行验证。

## 本地游戏源码阅读结果

开发套件已经生成Minecraft1.21.1与NeoForge21.1.264的可读源码。以下仅覆盖选定区域；文件哈希、来源JAR与行号记录在 `tools/source-reading.json`。

| 已读区域 | 实际观察 | 对新Mod的影响 |
| --- | --- | --- |
| ServerPlayer.restoreFrom | 恢复部分玩家状态并调用玩家克隆事件 | 永久成长和临时施放状态分别规定继承，不能只保留旧玩家对象引用 |
| Entity保存与读取 | 附件在实体存取流程中序列化和恢复 | 定义序列化与迁移规则，再明确可选同步配置与接收者 |
| LivingEntity.hurt入口 | 拒绝客户端和部分无效伤害，进入可取消的伤害流程 | 区分施放被接受与实际造成伤害；不能直接改血量绕开标准伤害机制 |
| ItemStack.getComponents | 物品读取组件映射与组件差异 | 专属物品数据通过平台适配，不沿用旧版任意NBT写入的假设 |
| AttachmentType.Builder.copyOnDeath | 需要序列化器，可自定义拷贝处理 | 角色进度应明确死亡继承方式，并分别测试重生和末地返回 |
| PayloadRegistrar字段与构造 | 默认MAIN处理线程，配置通过注册器保存 | 按实际注册上下文处理请求，不照搬旧Forge网络线程默认值 |
| EventHooks.onPlayerClone | 发布玩家克隆事件 | 该入口证明事件调用位置，尚未审阅附件拷贝的全部调用链 |

这些观察已经足以约束首个角色原型的生命周期、资源结算与数据边界，不意味着已经掌握全部游戏实现。当前没有复制这些源码到自建Mod中；生成JAR与游戏资源保留在忽略的构建缓存内。

### 所选构建中的附件同步补查

运行日志出现AttachmentSync后，补读AttachmentType、AttachmentHolder与AttachmentSync。21.1.264已支持配置同步处理器或StreamCodec，也可按接收玩家过滤。setData与removeData进入同步通知，未配置同步处理器的类型不会因此自动发送。

对角色系统的推导：持久成长、个人界面数据和其他玩家可见外观分别定义同步范围；不把完整私人档案默认广播给观察者。可利用平台同步能力，但仍需验证重连、追踪范围变化、死亡与重生时的实际行为。此前基于旧网页的“必须自行发包”表述已按实际源码修正。