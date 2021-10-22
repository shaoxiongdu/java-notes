# Spring-IOC

> 文章首发于GitHub开源项目: [Java成长之路](https://github.com/shaoxiongdu/java-notes) 欢迎大家star!

## IOC概念和原理

1. ### 什么是IOC

   控制反转，为了将系统的耦合度降低，把对象的创建和对象直接的调用过程权限交给Spring进行管理。

2. ### IOC底层原理

    1. XML解析

       ​ 通过Java代码解析XML配置文件或者注解得到对应的类的全路径，获取对应的Class类

       ```java
       Class clazz = Class.forName("全路径");
       ```

    2. 工厂设计模式

    3. 反射

       ​ 利用第一步得到的clazz，和工厂模式创建对应的对象并返回

       ```java
       public static Object factory(){
           return clazz.newInstance();
       }
       ```

![image-20211022200111974](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211022200111974.png)

## IOC接口

### Spring提供了IOC容器实现的两种方式。

1. ## BeanFactory 接口 （懒加载）

   ![image-20211022201715272](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211022201715272.png)

   BeanFactory 是 Spring 的“心脏”。它就是 Spring IoC 容器的真面目。

   Spring 使用 BeanFactory 来实例化、配置和管理 Bean。是IOC容器的最顶级核心接口， 它定义了IOC的基本功能。

   主要定义了一些获取bean的方法。

   ![image-20211022201534986](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211022201534986.png)

2. ## ApplicationContext (饿加载)

   ApplicationContext由BeanFactory派生而来，提供了更多面向实际应用的功能。

   在BeanFactory中，很多功能需要以编程的方式实现，而在ApplicationContext中则可以通过配置实现。

   ### 主要实现类有两个

    - #### FileSystemXmlApplicatonContext

      通过系统绝对路径加载配置文件

    - #### ClassPathXmlApplicationContext

      通过类路径（src）加载配置文件

   ![image-20211022202536581](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211022202536581.png)

## 二者区别

- BeanFactroy采用的是延迟加载形式来注入Bean的，即只有在使用到某个Bean时，才对该Bean进行加载实例化，这样，我们就不能发现一些存在的Spring的配置问题。
- 而ApplicationContext则相反，它是在容器启动时，一次性创建了所有的Bean。这样，在容器启动时，我们就可以发现Spring中存在的配置错误。 相对于基本的BeanFactory，ApplicationContext
  唯一的不足是占用内存空间。当应用程序配置Bean较多时，程序启动较慢。但是在后期调用的时候，不需要等待创建。

> 文章首发于GitHub开源项目: [Java成长之路](https://github.com/shaoxiongdu/java-notes) 欢迎大家star!