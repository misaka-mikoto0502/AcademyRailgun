# Java 与构建基础

核对日期：2026-10-21。以下是面向模组开发的学习重点，不是完整 Java 教材。早期 Oracle Tutorials 以 JDK 8 为背景；新语法和具体 API 应查对应 JDK 的文档。

## 1. 定义、对象与接口

Java 的类描述状态和行为，实例承载某次运行中的状态；接口表达调用契约，允许替换实现。针对本项目，能力资料、某玩家的进度、一次施法上下文应当是不同对象，不能因为使用相同技能就共享可变进度。

接口适合划分存档、时间、奖励规则和效果执行等边界。它只规定方法的形状，不自动保证纯函数、线程安全或游戏逻辑正确。组合多个小职责通常比不断给角色增加继承层更便于调整；这是项目设计判断，不是 Java 的强制规则。[Oracle 接口与继承](https://docs.oracle.com/javase/tutorial/java/IandI/index.html)

## 2. 泛型与集合

泛型可以在编译期检查资料和容器之间的类型关系，减少运行时强制转换。原始类型、未经检查的转换，以及把不同含义的对象塞进同一无类型 Map，都会削弱这种保证。[Oracle 泛型](https://docs.oracle.com/javase/tutorial/java/generics/index.html)

Map 的键用于稳定标识，集合的迭代顺序则取决于具体实现。不能依赖 HashMap 的遍历顺序作为存档编号或网络协议编号；改变键对象中影响相等比较的字段也会破坏查找行为。[Map API](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html)

项目推导：使用明确的内容 ID 保存关联；需要顺序时单独定义顺序。制作快照时复制容器，再暴露只读视图，并检查容器中的值是否仍是可变引用。“字段是 final”并不意味着整个对象树不可变。

## 3. 异常与资源生命周期

文件、流和压缩包读取应明确关闭资源；Java 的 try-with-resources 能为 AutoCloseable 对象建立退出时的关闭路径。[Oracle 资源管理](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)

项目推导：玩家请求被规则拒绝与程序异常应分开处理。缺少资源可以返回可解释的拒绝结果；配置损坏、文件写入失败或程序错误需要记录具体原因。不能先扣费再在异常处理里假装整个动作已经回滚。

## 4. 线程与共享状态

Java 允许线程共享对象，但跨线程读写的可见性和顺序需要遵守内存模型。final、volatile、锁和并发集合解决的问题不同；换成 ConcurrentHashMap 不会把“读取余额、判断、扣费、产生效果”自动变成一个原子操作。[JLS 17：线程与锁](https://docs.oracle.com/javase/specs/jls/se17/html/jls-17.html)

对模组的工程选择：游戏状态通常交给相应服务端主线程；耗时文件解析可以在独立任务里处理纯数据，再调度结果提交。不要让工作线程持有 Entity、Level 或玩家容器并直接修改它们。实际调度 API 由目标平台确定。[Forge 网络线程](https://docs.minecraftforge.net/en/1.20.1/networking/simpleimpl/)

## 5. 编译兼容不等于模组兼容

Java 源码由 javac 编译成 class，JVM 加载这些类；JAR 负责打包代码和资源。javac 的 `--release` 同时限定目标 Java 语言规则、class 目标和可见的标准平台 API；它不会提供某个 Minecraft 或 Forge 版本的 API。[javac 手册](https://docs.oracle.com/en/java/javase/17/docs/specs/man/javac.html)

因此，现有原型使用 `--release 8` 只说明核心使用了相应 Java 兼容范围。它不能证明能加载到 1.12.2、能在旧 ForgeGradle 下构建，或能跨游戏版本发布同一个模组包。

## 6. 构建时有多套版本

| 层次 | 需要记录什么 |
| --- | --- |
| Gradle 进程 | 用什么 JDK 启动 Gradle；是否在该 Gradle 的支持范围 |
| 编译器 | 使用哪个 Java toolchain；语言和 API 目标是什么 |
| 游戏进程 | 运行目标 Minecraft 所需的 Java；启动任务实际选了哪个 JVM |
| 开发插件 | ForgeGradle 或其他插件与 Gradle、游戏的兼容关系 |
| 游戏名称 | 反编译和调试时使用的映射名称及相关工具链 |

Gradle toolchain 与 `--release` 是不同设置；前者选择工具，后者约束交叉编译范围。IDE 的 Gradle JVM 也可能与项目编译器不同。[Gradle Toolchains](https://docs.gradle.org/current/userguide/toolchains.html)

项目应使用自己的 Gradle Wrapper，并记录对应 Gradle 分发版本；不能因为机器装了更高版本就直接替换旧项目的构建组合。具体支持范围查对应版本的兼容表。[Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)、[兼容表](https://docs.gradle.org/current/userguide/compatibility.html)

## 7. 后续 Java 学习与实证

后续接入时需要实际验证：集合快照是否隔离可变数据、配置异常是否带上文件和字段位置、资源操作是否一次提交、工作线程结果是否回到游戏线程、多个存档是否互相隔离。性能方面先测量热点、分配量和存活引用，再决定缓存或异步处理。

当前笔记覆盖概念阅读；没有把“读过语言说明”记作完整 Java 熟练度认证。
