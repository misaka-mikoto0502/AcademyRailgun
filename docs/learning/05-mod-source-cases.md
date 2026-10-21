# 知名模组的关键源码案例

阅读日期：2026-10-21。下面是对指定文件的源码观察和工程启发，不是对完整模组的审计，也没有运行这些仓库。五个案例均选择构建配置确认的 Minecraft 1.20.1 分支，便于对照；这不代表项目已经选择 1.20.1。

具体提交与文件记录在 [source-pins.json](source-pins.json)。后续阅读应使用固定提交链接；仓库默认分支可能已经转向其他版本或加载器。

## Create：网络汇总与成员更新

读取 `KineticNetwork` 的成员增删、容量/负载计算和同步方法。它分别保存动力来源与网络成员，汇总值变化后更新成员；计算时也检查某些已失效的方块实体引用。

这说明网络状态需要维护成员生命周期和聚合数据，不能只保存几条连线。该文件主要处理汇总，不足以代表完整连接传播算法。[固定源码](https://github.com/Creators-of-Create/Create/blob/588124239141029c5321afd38d20c2eaf872b32b/src/main/java/com/simibubi/create/content/kinetics/KineticNetwork.java#L58)

项目推导：若以后存在运行中的能力网络，应分别考虑成员、连接、派生数值和通知范围；改变成员时再安排重算，而不是让每个界面每帧重新遍历整个世界。

## Botania：足额消耗与平台边界

`ManaItemHandlerImpl.requestManaExact` 先检查可参与的资源来源并记录扣除计划，确认足够后才执行相应消耗；该路径可避免资源不足时只扣掉一部分。这里不能推导出所有世界修改都具有事务性。[固定源码](https://github.com/VazkiiMods/Botania/blob/0ddec883d6ec4c7be78525267e29cbf1bd98c672/Xplat/src/main/java/vazkii/botania/common/impl/mana/ManaItemHandlerImpl.java#L109)

所查分支还将共享内容放在 Xplat，另有 Forge 和 Fabric 目录。案例中的资源查询通过平台抽象完成，提供了隔离部分平台接口的实际例子。

项目推导：能力可以先生成资源变更计划，再统一校验和提交；不过涉及库存、世界和外部模组时，仍需定义失败处理，不能只依赖普通 Map 的一次扣费。

## Applied Energistics 2：节点、网络与服务

`Grid` 将成员添加、移除和节点数据保存通知交给网络服务，也把不同 tick 阶段分发给相应服务。`GridNode` 管理连接、所属网络和销毁后的重新验证，并使用拆分检测。[Grid](https://github.com/AppliedEnergistics/Applied-Energistics-2/blob/1c2f96e70ca6b61e8120482923e9775d772fd9e7/src/main/java/appeng/me/Grid.java#L93)、[GridNode](https://github.com/AppliedEnergistics/Applied-Energistics-2/blob/1c2f96e70ca6b61e8120482923e9775d772fd9e7/src/main/java/appeng/me/GridNode.java#L191)

项目推导：连接关系、成员资格、资源服务和状态通知可以分开建模。运行网络允许怎样的环路，应由其规则决定；技能解锁图通常需要避免无法解锁的循环，两者不能直接共用同一条限制。本次没有研究 AE2 全部寻路、频道和能量算法。

## Mekanism：网络包基础设施

`BasePacketHandler` 统一创建通道、检查协议版本、登记消息方向，并提供发给本人、维度和跟踪者等不同发送入口。包的编解码辅助也集中在此，而不是每个功能各自建立一套通信方式。[固定源码](https://github.com/mekanism/Mekanism/blob/96c736241e308a9cf1c73f7dcfa9aa221ea559d3/src/main/java/mekanism/common/network/BasePacketHandler.java#L124)

项目推导：技能系统应维护明确的消息类型、版本和接收范围，效果广播与私有进度同步分开。上述基类本身不能证明某个具体消息已经做了权限、目标或频率校验；这些需要继续查看对应处理器。

## Ars Nouveau：技能部件、上下文与执行器

`SpellResolver` 先验证法术结构及资源，经过可取消事件后调用施放方式；示例路径根据结果决定是否扣除魔力，效果解析另有循环及前后事件。`SpellContext` 保存施法者、进度、取消和延迟等状态；`AbstractSpellPart` 则定义部件身份、成本和配置入口。[执行器](https://github.com/baileyholl/Ars-Nouveau/blob/2c74064bc753600d9b6600f102f8163e2d6764dd/src/main/java/com/hollingsworth/arsnouveau/api/spell/SpellResolver.java#L54)、[上下文](https://github.com/baileyholl/Ars-Nouveau/blob/2c74064bc753600d9b6600f102f8163e2d6764dd/src/main/java/com/hollingsworth/arsnouveau/api/spell/SpellContext.java#L111)、[部件定义](https://github.com/baileyholl/Ars-Nouveau/blob/2c74064bc753600d9b6600f102f8163e2d6764dd/src/main/java/com/hollingsworth/arsnouveau/api/spell/AbstractSpellPart.java)

项目推导：技能定义、一次施法的状态和执行调度分开后，才能继续支持延迟、中断或组合效果。扣费时机需与执行结果契约一起确定，不能只规定“效果前扣”或“效果后扣”就认为问题已经解决。

## 与 AcademyCraft 的联系

之前已定位 AcademyCraft 的 Skill、AbilityData、CPData、Context 和 Railgun 等入口，见 [项目调研](../project-research.md)。其能力前置、玩家进度和客户端表现分工，与上述案例可以相互参照；但之前读到的在线 master 尚未逐文件与本地 1.1.3 二进制核对。

本轮可复用的是职责划分和验证方式。具体代码或资产是否可直接采用，需要核对对应文件和版本的许可；这批学习文档没有拷入第三方源码、贴图或音频。
