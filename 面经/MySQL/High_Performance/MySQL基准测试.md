## 为什么需要基准测试
基准测试可以完成以下工作，或者更多：
- 验证基于系统的一些假设，确认这些假设是否符合实际情况。（验证假设）
- 重现系统中的某些异常行为，以解决这些异常。（复现问题）
- 测试系统当前的运行情况。如果不清楚系统当前的性能，就无法确认某些优化的效果如何。也可以利用历史的基准测试结果来分析诊断一些无法预测的问题。（测试当前系统性能，分析诊断问题）
- 模拟比当前系统更高的负载，以找出系统随着压力增加而可能遇到的扩展性瓶颈。（压力测试）
- 规划未来的业务增长。基准测试可以评估在项目未来的负载下，需要什么样的硬件，需要多大容量的网络，以及其他相关资源。这有助于降低系统升级和重大变更的风险。（规划未来业务，方便未来系统升级，降低风险）
- 测试应用适应可变环境的能力。例如，通过基准测试，可以发现系统在随机的并发峰值下的性能表现，或者是不同配置的服务器之间的性能表现。基准测试也可以测试系统对不同数据分布的处理能力。（测试应用适应可变环境的能力）
- 测试不同的硬件、软件和操作系统配置。
- 证明新采购的设备是否配置正确。（此处基准测试就相当于手机或者电脑跑分，测试新采购的设备的性能指标）

## 基准测试的策略
基准测试有两种主要的策略：一是针对整个系统的整体测试，另外是单独测试MySQL。这两种策略被称为集成式以及单组件式基准测试。

针对整个系统做集成测试，而不是单独测试MySQL的原因主要有以下几点：
- 测试整个应用系统，包括Web服务器、应用代码、网络和数据库时非常有用的，因为用户关注的不仅仅是MySQL本身的性能，而是应用整体的性能。
- MySQL并非总是应用的瓶颈，通过整体的测试可以揭示这一点。
- 只有对应用做整体测试，才能发现各部分之间缓存带来的影响。
- 整体应用的集成式测试能更能揭示应用的真实表现，而单独组建的测试很难做到这一点。

基于以下情况，可以选择只测试MySQL:
- 需要比较不同的schema或者查询的性能
- 针对应用中某个具体问题的测试。
- 为了避免漫长的基准测试，可以通过一个短期的基准测试，做快速的“周期循环”，来检测出某些调整后的结果。

### 测试何种指标
吞吐量：指的是单位时间内的事务处理数。常用的测试单位是每秒事务数（TPS),或者每分钟事务数（TPM).
响应时间或延迟：用于测试任务所需的整体时间。
并发性：并发性是一个非常重要又经常被误解和误用的指标。Web服务器的并发性更准确的度量指标，应该是在任意时间有多少同时发生的并发请求。并发性基准测试需要关注的是正在工作的并发操作，或者时同时工作中的线程数或者连接数。
可扩展性：在系统的业务压力可能发生变化的情况下，测试扩展性

如果经常执行基准测试，那么指定一些原则是很有必要的。选择一些合适的测试工具并深入地学习。可以建立一个脚本库，用于配置基准测试，收集输出结果，系统性能和状态信息，以及分析结果。熟练地使用一种绘图工具如gnuplot或者R绘图。


