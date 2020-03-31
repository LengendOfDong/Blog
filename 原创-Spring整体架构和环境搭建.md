# 原创：Spring整体架构和环境搭建

# Spring的整体架构

Spring框架是一个分层架构，它包含一系列的功能要素，并被分为大约20个模块。<br/>
这些模块被总结为以下几个部分：<br/>
1）Core Container<br/>
Core Container(核心容器)包含有Core、Beans、Context和Expression Language模块。<br/>
Core和Beans模块是框架的基础部分，提供IoC(转控制）和依赖注入特性。这里的基础概念是BeanFactory,它提供对Factory模式的经典实现来消除对程序性单例模式的需要，并真正允许你从程序逻辑中分离出依赖关系和配置。

<li>Test<br/>
Test模块支持使用JUnit和TestNG对Spring组件进行测试。</li>
