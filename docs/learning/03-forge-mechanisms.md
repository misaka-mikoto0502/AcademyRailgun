# Forge 的核心工作机制

核对日期：2026-10-21。现代 API 举例以 1.20.1 文档为主，旧版差异见版本笔记。以下是对机制的理解，尚未建立对应 MDK 的运行验证。

## 1. 模组入口与元数据

现代 Forge 的模组元数据包含身份、版本和依赖等信息，入口类通过 `@Mod` 与模组 ID 对应。元数据与实际构建依赖必须一致，否则“编译成功”和“加载成功”会出现差异。[Mod Files](https://docs.minecraftforge.net/en/1.20.1/gettingstarted/modfiles/)

项目推导：启动入口负责连接平台，不负责持有所有玩家的业务状态。初始化应分工到注册、网络、事件、客户端和资料加载等明确的组件中。

## 2. 加载生命周期与世界生命周期不同

1.12 的典型加载阶段是 preInit、init、postInit；物品等内容在该版本也已有注册事件，不应把所有注册都塞进旧阶段方法。[1.12 加载阶段](https://docs.minecraftforge.net/en/1.12.x/conventions/loadstages/)

现代 Forge 则通过模组事件总线发送设置等事件，部分生命周期事件会并行发送，需要时使用相应的排队机制处理不适合并发访问的工作。[现代生命周期](https://docs.minecraftforge.net/en/1.20.1/concepts/lifecycle/)

模组加载不是每个玩家登录时重新执行。项目推导：玩家加入、世界加载、服务器停止、资源重载应各自绑定事件和清理路径；关闭一个存档后，不能把其中的实体和玩家状态留到下一个存档。

## 3. 事件总线与回调

1.20.1 的 Forge 主事件总线处理大量游戏行为，模组事件总线处理特定加载事件。监听方法必须注册在该事件实际发送的总线上。取消能力、结果语义和阶段也要以具体事件定义为准。[Events](https://docs.minecraftforge.net/en/1.20.1/concepts/events/)

项目推导：奖励事件需要核对逻辑端、阶段、责任玩家和目标，而不是“收到了受伤事件就增加经验”。同一行为可能涉及多种事件，必须防止重复结算。

## 4. 内容注册

注册把物品、方块、声音等定义交给游戏识别。每种注册表有自己的键空间；`DeferredRegister` 是 1.20.1 文档推荐的注册方式，延迟供应对象以适配正确的注册时机。[Registries](https://docs.minecraftforge.net/en/1.20.1/concepts/registries/)

项目推导：保留稳定命名空间，避免提前获取尚未注册的对象。技能资料是否进入自定义注册表、动态注册表或独立目录，需要根据重载、同步和第三方扩展需求决定；没有必要先把所有业务对象都变成 Forge 注册对象。

## 5. 玩家数据与 Capability

Capability 的核心是对对象暴露一组功能接口；也可用于附加自己的玩家数据。实现时要考虑提供者的有效期、序列化、失效处理和玩家克隆。死亡重生与从末地返回不能不加区分地重复累加数据。[Capabilities](https://docs.minecraftforge.net/en/1.20.1/datastorage/capabilities/)

持久化、死亡后保留和发给客户端是不同问题。正式接入前应画出“原玩家对象 → 新玩家对象”“离线数据 → 登录状态”“当前状态 → 新观察者”的数据流。

## 6. 网络与线程

1.20.1 的 SimpleChannel 管理包类型、编码、解码、处理和协议版本。处理服务端请求时，从网络上下文取得真实发送者，并按平台要求调度到主线程；不能相信消息中自报的玩家、伤害或奖励结果。[SimpleImpl](https://docs.minecraftforge.net/en/1.20.1/networking/simpleimpl/)

项目推导：协议传操作意图和必要输入，由服务端重新构造上下文。私有成长发给本人，公开外观或效果发给合适的观察者。重复消息、快速连发和过期会话的处理属于适配层的一部分，不能由“界面按钮已禁用”代替。

## 7. 数据、配置与资源

客户端资源与服务端数据具有不同职责：纹理、模型和语言等由资源系统使用；配方、掉落等由服务端数据系统使用。资源包或数据包能覆盖的内容，不等同于可在游戏运行中重新注册任意 Java 对象。[客户端资源](https://docs.minecraftforge.net/en/1.20.1/resources/client/)、[服务端数据](https://docs.minecraftforge.net/en/1.20.1/resources/server/)

Codec 可以把结构化对象与 JSON、NBT 等表示连接起来；DataResult 还表达错误或部分结果。业务资料不能因为得到部分结果就直接接受一半技能树。[Codecs](https://docs.minecraftforge.net/en/1.20.1/datastorage/codecs/)

项目推导：资料重载分成解析、全量校验、创建新快照、发布这几步。失败应保留上一套可用资料并给出位置明确的错误；重载时还要规定正在施法的会话继续使用旧资料还是被取消。

## 8. 界面、实体与存档分别接入

Menu 用于管理交互及需要同步的数据关系，Screen 负责客户端画面。只有查看信息的能力图未必需要库存式 Menu；需要服务端动作的按钮仍需走请求和确认流程。[Menus](https://docs.minecraftforge.net/en/1.20.1/gui/menus/)

自定义实体可以区分生成时的数据和运行中变化的数据，动态同步参数应在自己的实体类内定义。[实体同步](https://docs.minecraftforge.net/en/1.20.1/networking/entities/)

SavedData 和 BlockEntity 的修改标记关系到游戏是否写回变化，不能把网络已更新当成数据已经保存。[SavedData](https://docs.minecraftforge.net/en/1.20.1/datastorage/saveddata/)、[BlockEntity](https://docs.minecraftforge.net/en/1.20.1/blockentities/)

## 9. 开发验证需要逐层完成

MDK 提供构建和游戏启动任务；官方还强调专用服务器环境的验证。[Forge 开发入口](https://docs.minecraftforge.net/en/1.20.1/gettingstarted/)

GameTest 使用游戏中的场景和成功条件验证交互行为，适合覆盖部分世界逻辑，但不会自动证明皮肤、光束或声音的观感正确。[Game Tests](https://docs.minecraftforge.net/en/1.20.1/misc/gametest/)

项目应分别记录：纯 Java 规则验证、客户端启动、专用服务器加载、双端交互、存档迁移和表现检查。每一项都应有自己的证据。
