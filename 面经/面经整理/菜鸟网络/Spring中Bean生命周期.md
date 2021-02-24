# Bean生命周期
Bean的生命周期分为以下几步：
- 实例化
- 填充属性
- 调用BeanNameAware的setBeanName方法
- 调用BeanFactoryAware的setBeanFactory方法
- 调用ApplicationContextAware的setApplicationContext方法
- 调用BeanPostProcessor的postProcessBeforeInitialization方法
- 调用InitializingBean的afterPropertiesSet方法
- 调用定制的初始化方法
- 调用BeanPostProcessor的postProcessAfterInitialization方法
- Bean准备就绪
- 调用DispostableBean的destory方法
- 调用定制的销毁方法

