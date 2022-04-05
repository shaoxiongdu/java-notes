# Java成长之路

<br>
<div align="center">
    <img src="https://img.shields.io/badge/JVM-底层-blue">
    <img src="https://img.shields.io/badge/Java-SE-yellow">
    <img src="https://img.shields.io/badge/数据库-MySQL-orange">
    <img src="https://img.shields.io/badge/Spring-源码解析-green">
    <img src="https://img.shields.io/badge/Redis-知识整理-red">
    <img src="https://img.shields.io/badge/计算机网络-分层概述-purple">
    <img src="https://img.shields.io/badge/nginx-知识整理-orange">
    <img src="https://img.shields.io/badge/RabbitMQ-消息中间件-orange">
    <img src="https://visitor-badge.glitch.me/badge?page_id=shaoxiongdu.java-notes">

<h3><a href="https://shaoxiongdu.github.io/java-notes/#/" target="_blank">在线站点（阅读体验更好）</a></h3>
</div>

## 前言

在大三准备面试的时候，我开源了 Java-notes 。

我把自己准备面试过程中的一些总结都毫不保留地分享了出来。

开源java-notes初始想法源于自己的个人那一段比较迷茫的学习经历。

主要目的是为了通过这个开源平台来帮助一些在学习 Java 或者面试过程中遇到问题的小伙伴。

- **对于 Java 初学者来说：** 本文档倾向于给你提供一个比较详细的学习路径，让你对于 Java 整体的知识体系有一个初步认识。另外，本文的一些文章也是你学习和复习 Java 知识不错的实践；
- **对于非 Java 初学者来说：** 本文档更适合回顾知识，准备面试，搞清面试应该把重心放在那些问题上。要搞清楚这个道理：提前知道那些面试常见，不是为了背下来应付面试，而是为了让你可以更有针对的学习重点。

相比于其他通过java-notes 学到东西或者说助力获得 offer 的朋友来说 ， java-notes 对我的意义更加重大。不夸张的说，有时候真的感觉像是自己的孩子一点一点长大一样，我一直用心呵护着它。虽然，我花了很长时间来维护它，但是，我觉得非常值得！非常有意义！

希望大家对面试不要抱有侥幸的心理，打铁还需自身硬！ 我希望这个文档是为你学习 Java 指明方向，而不是用来应付面试用的。加油！奥利给！

## 一、目录

---

1. [JavaSE](/?id=JavaSE)
2. [JavaEE](/?id=JavaEE)
3. [JVM](/?id=JVM)
4. [Java设计模式](/?id=Java设计模式)
5. [MySQL数据库](/?id=MySQL数据库)
6. [计算机网络](/?id=计算机网络)
7. [Spring](/?id=Spring)
8. [SpringBoot](/?id=SpringBoot)
9. [Redis](/?id=Redis)
10. [Nginx](/?id=Nginx)
11. [RabbitMQ](/?id=RabbitMQ消息中间件)



## 二、清单 

---

### 2.1 JavaSE

- [Java之多线程详解](./JavaSE/Java之多线程.md)
- [ArrayList源码解析](./JavaSE/手撕ArrayList源码.md)
- [HashMap源码解析](./JavaSE/手撕HashMap源码.md)

### 2.2 JavaEE

- [Servlet](./JavaEE/Servlet.md)

### 2.3 JVM

> 已整理至开源项目: [JVM底层原理解析](https://github.com/shaoxiongdu/JVMStudy)

### 2.4 Java设计模式

> 已整理至开源项目: [详解Java设计模式](https://github.com/shaoxiongdu/java-design-pattern)

### 2.5 MySQL数据库

- [事务](./MySQL/事务.md)
- [视图](./MySQL/视图.md)
- [存储过程&函数](./MySQL/存储过程和函数.md)
- [索引优化之路](./MySQL/索引.md)
- [性能分析](./MySQL/性能分析.md)
- [如何避免索引失效](./MySQL/如何避免索引失效.md)
- [锁](./MySQL/锁.md)
- [一条SQL语句是如何执行的?](./MySQL/一条SQL语句是如何执行的？.md)

### 2.6 计算机网络

> Java设计模式相关内容已整理至开源项目: [详解计算机网络](https://github.com/shaoxiongdu/ComputerNetworks)

### 2.7 Spring

- [Spring-IOC](./Spring/Spring-IOC.md)
- [Spring-Bean管理-注解](./Spring/Spring-Bean管理-注解.md)
- [Spring-IoC容器-源码解析](./Spring/Spring-IOC源码解析.md)
- [SpringMVC](./Spring/SpringMVC.md)
---

### 2.8 SpringBoot
-  [SpringBoot-自动配置-源码解析](SpringBoot/SpringBoot自动配置原理解析.md)
-  [SpringBoot-REST风格-源码解析](Spring/Spring-REST风格-源码解析.md)

### 2.9 Redis

-  [基本数据类型及常用命令](./Redis/redis基本数据类型及常见命令.md)
-  [redis5新增数据类型](./Redis/redis5新增数据类型.md)

### 2.10 Nginx

- [Nginx的常见功能](./Nginx/Nginx常见功能.md)
- [Nginx的原理轻探](./Nginx/Nginx原理解析.md)

### 2.11 RabbitMQ消息中间件

- [RabbitMQ简述](./RabbitMQ/RabbitMQ.md)
- [简单模型](./RabbitMQ/简单模型.md)
- [广播模型](./RabbitMQ/广播模型.md)
- [工作队列模型/任务模型](./RabbitMQ/工作队列模型（任务队列）.md)
- [路由模型](./RabbitMQ/路由模型.md)
- [SpringBoot整合RabbitMQ](./RabbitMQ/SpringBoot整合RabbitMQ.md)

## 三、反馈及改进

---

如果您在学习的时候遇到了任何问题，或者清单有任何可以改进的地方，

非常欢迎提出`issues`,看到就会回馈.并且将您添加到项目贡献者列表中。

## 四、参与贡献（非常欢迎！）

---

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request，填写必要信息。
5. 等待审核即可。通过之后会邮件通知您。

## 五、许可证

---

在 MIT 许可下分发。有关更多信息，请参阅[`LICENSE`](./LICENSE)。

## 六、致谢

---

>  1. 感谢BiliBili提供的在线课程平台 [BiliBili官网](https://www.bilibili.com)
>  2. 感谢JetBrains提供的配套开发环境许可证 [JetBrains官网](https://www.jetbrains.com/)
