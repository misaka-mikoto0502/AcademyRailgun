# 现有项目调研

> 历史研究对象为旧整合包，不是当前独立开发仓库。当前项目进度见 [文档索引](README.md) 和 [环境状态](workspace-migration.md)。

调研日期：2026-10-20。范围：本地文件、JAR 元数据与资源索引，以及 AcademyCraft 官方仓库的相关源码。

## 当前结论

本地项目是“某科学的魔花物语”HMCL 整合包及游戏实例，包含 Minecraft 1.12.2、Forge、已编译模组、配置、资源和存档。目前未发现 Java/Scala 源码、Gradle 工程或整合包自定义脚本。

核心参考对象是 AcademyCraft 的能力框架，而整合包主要提供组合玩法与实际配置样本。

## 本地构成与证据

| 内容 | 已确认事实 | 本地证据 |
| --- | --- | --- |
| 整合包 | 名称“某科学的魔花物语”，版本 1.0，游戏版本 1.12.2 | `modpack.zip` 内 `modpack.json`；实例 `modpack.cfg` 的 manifest |
| 游戏平台 | Forge 1.12.2-14.23.5.2837 | 实例版本 JSON 第 19 行；历史日志也记录此版本 |
| 能力系统 | AcademyCraft 1.1.3，modid 为 `academy` | `炮姐AcademyCraft-1.1.3.jar` 内 `mcmod.info` |
| 公共支持库 | LambdaLib2 0.2.0 随 AcademyCraft 加载 | JAR 的 CorePlugin 声明及历史日志中的模组列表 |
| 超能力敌人 | Academy Monster 1.1.0a | 对应 JAR 的 `mcmod.info` 与 `academymonster.cfg` |
| 其他玩法 | Botania、Cyclic、Lost Cities、怪物强化及辅助模组 | `mods` 中的文件和元数据；顶层有 27 个 `.jar` 文件 |
| 角色表现参考 | CosmicNPCs 的说明是录制玩家动作并回放为 NPC | 对应 JAR 的 `mcmod.info` |

实例目录为 `.minecraft/versions/某科学的魔花物语/`。配置文件可能包含已经移除的模组留下的内容，不能仅凭配置文件名断定某模组仍在加载。

## 能力实现逻辑

### 1. 定义、玩家状态、执行与界面分开

本地 JAR 可定位到 `Category`、`Skill`、`AbilityData`、`CPData`、`CooldownData`、`PresetData`、`Context` 与 `SkillTreeAppUI`。这些入口分别对应能力分类、技能定义、玩家进度、资源、冷却、快捷配置、执行上下文和技能树展示。

官方源码中的 `AbilityData` 保存能力类别、已学技能、技能熟练度与能力等级，并处理数据同步和 NBT 存储。这是后续拆分“定义”和“玩家实例状态”的参考。[AbilityData 源码](https://github.com/LambdaInnovation/AcademyCraft/blob/master/src/main/java/cn/academy/datapart/AbilityData.java)

### 2. 技能树包含前置关系和熟练度条件

`Skill.setParent` 与 `addSkillDep` 用于设置学习条件。`CatElectromaster` 展示了跨分支依赖：超电磁炮除依赖雷击之枪，还要求磁场控制达到指定熟练度。铁砂技能在该源码入口仍是未完成的注释，不能把语言文件中的铁砂名称当作功能已实现的证据。[技能定义](https://github.com/LambdaInnovation/AcademyCraft/blob/master/src/main/java/cn/academy/ability/Skill.java)、[电击使分类](https://github.com/LambdaInnovation/AcademyCraft/blob/master/src/main/java/cn/academy/ability/vanilla/electromaster/CatElectromaster.java)

### 3. 计算力与过载是两个维度

`CPData` 包含消耗、恢复、最大值成长和过载状态。配置支持调整恢复速度、初始上限及各技能消耗倍率。这里值得参考的是资源与约束的组合方式，数值本身暂不沿用。[CPData 源码](https://github.com/LambdaInnovation/AcademyCraft/blob/master/src/main/java/cn/academy/datapart/CPData.java)

### 4. 超电磁炮包含输入节奏、结算与表现

`Railgun.scala` 包含抛硬币时机判定，以及金属物品的蓄力路径；服务端执行消耗、射线伤害、熟练度和冷却，客户端处理手部、光束与音效。后续可借鉴流程分工，输入方式和效果形式仍待设计。[Railgun 源码](https://github.com/LambdaInnovation/AcademyCraft/blob/master/src/main/scala/cn/academy/ability/vanilla/electromaster/skill/Railgun.scala)

### 5. 原有成长入口与新目标可以独立处理

源码中的技能经验会增加能力升级进度，能力开发操作负责获得类别或提升等级。现有逻辑适合研究，但不能据此把未来成长方式限定为训练、击杀或某一种等级模型。[能力进度](https://github.com/LambdaInnovation/AcademyCraft/blob/master/src/main/java/cn/academy/datapart/AbilityData.java)、[能力开发操作](https://github.com/LambdaInnovation/AcademyCraft/blob/master/src/main/java/cn/academy/ability/develop/action/DevelopActionLevel.java)

## 本地重点入口

| 文件 | 位置 | 可参考内容 |
| --- | --- | --- |
| `config/academy.cfg` | 第 5、18、30 行 | 可磁化实体、普通金属和弱金属清单 |
| 同上 | 第 43、46 行 | 玩家伤害和方块破坏开关 |
| `config/academy-craft-data.conf` | 第 6–20 行 | 全局资源恢复、成长和上限 |
| 同上 | 第 56–85 行 | 电击使技能配置入口 |
| `config/academymonster.cfg` | 第 4 行 | 配置声明替换部分技能，以支持怪物侧的矢量反射 |
| 同上 | 第 74–77 行 | 怪物超电磁炮的权重和破坏方块配置 |
| AcademyCraft JAR | `assets/academy/lang/zh_cn.lang` | 技能名称与说明，可用于检索功能入口 |
| AcademyCraft JAR | `assets/academy/config/default.conf` | 内置能力数据配置默认值 |

## 源码与本地版本的关系

官方仓库有 `1.1.3` 标签，标签页对应提交缩写为 `83974c8`。[官方标签](https://github.com/LambdaInnovation/AcademyCraft/tags)

本次已阅读的在线源码来自 `master`；尚未逐文件与 `1.1.3` 标签、本地 JAR 做一致性核对，因此这里记录的是实现参考，不承诺源码与本地二进制逐行对应。

官方构建脚本使用 Java 8、Scala 和 ForgeGradle 2.3，并打包 LambdaLib2。它包含旧仓库地址，尚未验证今天能否直接构建。[官方构建脚本](https://github.com/LambdaInnovation/AcademyCraft/blob/master/build.gradle)

官方 README 写明 GPLv3，并附有额外许可声明。后续若直接采用原代码或资产，应逐项核对相应许可；本次框架采用新写的核心代码。[官方说明](https://github.com/LambdaInnovation/AcademyCraft#license)

本次没有启动 Minecraft；历史日志也不能代表当前运行状态。未验证游戏内手感、视觉效果或多人兼容性。
