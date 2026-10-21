# 独立目录迁移与开发环境

记录与检查点整理日期：2026-10-21。设计进度见 [当日检查点](checkpoints/2026-10-21.md)，当前讨论见 [角色设计](design/character-design.md)。

## 新开发根目录

`E:\Dev\RailgunMod`

来源：`E:\Game\PCL II\某科学的魔花物语-by骑士空\railgun-framework`。

采用复制并逐文件校验，迁移32个自建文件。清单见 `tools/migration-manifest.json`。旧整合包的mods、config、saves和.minecraft没有迁入，也未删除。旧自建框架入口留下MIGRATED.md指引；后续新开发以本目录为准。

`prototype-core`保存前期纯Java规则原型，`docs`保存设计与研究，`platform-probes`用于平台验证。本地 Git 已初始化，未配置远程；本次整理将源码、文档和工具记录作为本地检查点保存，最终提交状态可通过 `tools/Invoke-ProjectGit.ps1 log -1 --oneline` 查询。GitHub 上传由用户处理。迁移后的纯Java核心全部场景验证通过，但仍未连接游戏。

## 已准备的技术验证环境

| 项目 | 固定版本或位置 |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.264 |
| ModDevGradle | 2.0.148 |
| Gradle | 9.2.1 |
| JDK | Temurin21.0.12.1+1 64位 |
| 官方MDK提交 | f7002b1b6011723c7b12fde95147689491a6ddab |
| 平台小样 | platform-probes/neoforge-1.21.1 |
| 便携工具 | .tools |
| 独立Gradle缓存 | .gradle-user-home |

这是候选平台的技术验证，不等于正式锁定产品版本。Minecraft最低1.20、角色与世界内容分开推进的用户要求仍然有效。

JDK与Gradle包经官方SHA256核验，来源分别记录在 `tools/jdk-source.json`、`tools/gradle-source.json`；公开镜像只用于传输加速。官方MDK来源与本地修改记录在 `tools/platform-probe-source.json`。保留了模板许可与原始下载副本。

Minecraft客户端、服务端开发JAR、映射与资源由开发工具链获取。已生成合并、修补后的可读源码，位于小样的 `build/moddev/artifacts/neoforge-21.1.264-sources.jar`。生成内容与游戏资源均不纳入Git。

## 实际验证结果

| 检查 | 结果与范围 |
| --- | --- |
| 迁移校验 | 32个文件复制时SHA256一致 |
| 纯Java规则原型 | 原有全部场景通过；本轮用项目内 JDK 21 重新运行通过 |
| 官方小样build | 前期编译与打包成功，修复后默认入口通过；本轮离线 build 成功，5 个任务均为 UP-TO-DATE |
| runData | 启动加载与数据生成入口通过；示例未注册数据提供器，没有自建内容产出 |
| prepareClientRun | 客户端启动所需文件与资源准备成功 |
| prepareServerRun | 服务端启动文件准备成功 |
| 图形客户端进入世界 | 尚未执行 |
| 实际专用服务器启动 | 尚未执行 |
| 主题角色技能与联动 | 尚未实现或验证 |

官方资源下载记录为3888个文件，约787MiB。编译产物为模板的 `build/libs/examplemod-1.0.0.jar`，不能作为炮姐Mod成品交付。默认模板注册的示例内容不代表后续产品需求。

## 环境问题及处理

首次Java直连NeoForge仓库超时。Windows已有代理，构建入口仅在命令进程中沿用该配置，并在结束时恢复环境变量；未修改系统Java、代理或全局Gradle设置。

发现Gradle文件访问索引损坏后，先停止本工程JVM，再将该可再生索引隔离，保留所有已下载依赖。记录见 `tools/cache-repair.json`。修复后的数据启动及默认构建均成功，日志没有再次报告该索引异常。

技术小样默认关闭配置缓存，Wrapper补充官方分发校验值与网络超时设置。模板原Wrapper与声明的9.2.1校验值不同，已保留原件并换用官方同版本校验通过的Wrapper。

E盘为exFAT，Gradle自动禁用不支持的文件系统监视，不影响本次成功构建。Git需要目录信任时，使用 `tools/Invoke-ProjectGit.ps1` 的单命令参数，不添加全局通配信任。

## 使用入口

在新目录运行：

```powershell
.\prototype-core\verify.ps1
.\tools\Invoke-ProbeGradle.ps1 build --no-daemon --console=plain
.\tools\Invoke-ProjectGit.ps1 status --short
```

源码学习见 [现代环境与设计影响](learning/07-modern-environment.md)，精确阅读范围见 `tools/source-reading.json`。已经核对玩家恢复、实体附件保存、伤害入口、物品组件与网络注册器，并发现21.1.264提供网页未描述的可选附件同步API。没有声称已掌握全部Minecraft源码。

## 应用项目绑定

迁移当时的对话曾绑定旧整合包目录，因此前期记录包含重新打开项目的提醒。当前这轮工作上下文已位于 `E:\Dev\RailgunMod`，后续以该目录为开发根目录。文件迁移与应用中保存的项目设置是两件事，旧提醒作为迁移背景保留在此说明。
