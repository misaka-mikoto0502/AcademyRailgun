# 工具与仓库使用说明

本目录保存可版本管理的入口和来源记录。便携 JDK、Gradle、依赖缓存、构建日志放在根目录 `.tools/` 与 `.gradle-user-home/`，不随 Git 上传。

## 本机验证入口

从仓库根目录运行：

```powershell
.\prototype-core\verify.ps1
.\tools\Invoke-ProbeGradle.ps1 build --no-daemon --console=plain
.\tools\Invoke-ProjectGit.ps1 status --short
```

`prototype-core/verify.ps1` 调用当前进程 PATH 下的 javac、java、jar；需要完整 JDK，现有代码使用 --release 8 生成原型字节码。推荐使用本项目 JDK 21；这不代表目标游戏采用 Java 8。

如果仅使用本机便携 JDK，可为当前命令临时选择工具，运行后恢复 PATH：

```powershell
$projectJdk = @(Get-ChildItem .tools\jdk-21 -Directory | Where-Object {
    Test-Path -LiteralPath (Join-Path $_.FullName 'bin\javac.exe')
})
if ($projectJdk.Count -ne 1) { throw 'Expected one portable JDK 21.' }
$previousPath = $env:PATH
try {
    $env:PATH = (Join-Path $projectJdk[0].FullName 'bin') + [IO.Path]::PathSeparator + $previousPath
    .\prototype-core\verify.ps1
} finally {
    $env:PATH = $previousPath
}
```

`Invoke-ProbeGradle.ps1` 为命令进程选择项目内 JDK、Gradle 缓存及已有 Windows 代理，结束时恢复环境变量。已有完整依赖时可增加 --offline；第一次准备环境需要下载依赖。它不会安装工具或改动系统 Java 配置。

`Invoke-ProjectGit.ps1` 使用单次 safe.directory 参数处理本机 exFAT 目录信任，不修改全局 Git 信任配置。

## 克隆仓库后的环境准备

仓库包含源码、构建脚本和 Gradle Wrapper，不包含本机便携工具与已下载的 Minecraft 开发资源。

Windows 项目入口期望目录结构如下：

```text
.tools/
  jdk-21/
    <解压后的 JDK 目录>/bin/javac.exe
  gradle/
    gradle-9.2.1/bin/gradle.bat
```

JDK 目录必须恰好有一个符合条件的子目录。Gradle 本地目录可缺省，平台入口会改用随仓库提供的 Wrapper；首次运行 Wrapper 仍需下载固定版本分发包。

按 [JDK 来源](jdk-source.json) 和 [Gradle 来源](gradle-source.json) 的上游地址及 SHA256 准备相应工具。它们记录本检查点使用的精确版本，没有自动下载脚本。使用其他平台或已有 JDK 21 开发终端时，也可直接在 `platform-probes/neoforge-1.21.1` 中调用相应 gradlew 脚本；本次尚未验证其他机器或操作系统。

正式平台未锁定，完整环境与运行限制见 [环境状态](../docs/workspace-migration.md)。

## 记录文件

| 文件 | 用途 |
| --- | --- |
| [environment-lock.json](environment-lock.json) | 当前候选平台、工具组合、已验证与未验证范围 |
| [jdk-source.json](jdk-source.json) | 便携 JDK 的上游、传输来源与校验值 |
| [gradle-source.json](gradle-source.json) | Gradle 分发包来源与校验值 |
| [platform-probe-source.json](platform-probe-source.json) | 官方模板固定提交和本地环境调整 |
| [migration-manifest.json](migration-manifest.json) | 迁移时 32 个自建文件的校验快照 |
| [cache-repair.json](cache-repair.json) | 本机可再生 Gradle 索引的隔离修复记录 |
| [source-reading.json](source-reading.json) | 本地生成源码及实际阅读区域 |

JSON 中的绝对路径记录当时环境与证据位置，不是其他机器必须采用的目录。迁移清单中的哈希也不要求后续编辑过的文件继续与迁移时一致。

## GitHub 上传与检查点

上传仓库根目录下被 Git 管理的文件，保留原型、平台小样、文档和工具记录的目录层次。不要手动加入 `.tools/`、`.gradle-user-home/`、构建产物、运行存档、生成的游戏源码或旧整合包。

仓库保留 Gradle Wrapper JAR 和两份前期 Word 阅读版；这些是有意纳入的文件。官方模板的许可文件保留。没有新增根目录 GitHub Actions 工作流；模板内部 `.github/workflows` 按来源保留，不代表当前仓库已配置自动构建。

本轮整理不创建 GitHub 仓库、不选择公开或私有、不配置远程或执行推送。用户可把整理后的本地仓库关联到自己选择的 GitHub 仓库后上传。

检查本地状态与最新提交：

```powershell
.\tools\Invoke-ProjectGit.ps1 status --short
.\tools\Invoke-ProjectGit.ps1 log -1 --oneline
```

今天的设计与工程快照见 [2026-10-21 检查点](../docs/checkpoints/2026-10-21.md)。
