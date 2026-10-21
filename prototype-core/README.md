# 通用规则原型

这里保留迁移前的纯 Java 核心，用于前置、资源、冷却和状态恢复等规则验证。它尚未接入 Minecraft，不是可放入 mods 目录的游戏模组。

运行 `./verify.ps1` 可验证原有场景。本原型使用 Java 8兼容字节码；实际游戏工程另按目标版本工具链配置，不据此选择旧版 Minecraft。

项目现行边界见 [项目边界](../docs/design/project-boundaries.md)。新平台验证工程见 [工程入口](../README.md)。
