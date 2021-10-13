# Servlet

> 文章首发于GitHub开源项目: [Java成长之路](https://github.com/shaoxiongdu/java-notes) 欢迎大家star!

## Servlet

 是JavaEE三大组件之一，其他有过滤器，监听器。

![image-20211013151918534](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211013151918534.png)

- Servlet是JavaEE中制定的关于如何接受请求完成响应的标准的接口

- 当第一次请求到达应用服务器（如tomcat），tomcat会查找web.xml配置文件，找到请求对应的servlet实现类，然后tomcat会创建对象，并调用init方法。然后调用service方法处理请求。

- 之后过i来的请求都会直接调用service方法。也就是单例的。

- 在tomcat停止运行的时候，会调用destroy方法。

## Servlet继承体系图

![Servlet继承图](https://images-1301128659.cos.ap-beijing.myqcloud.com/Servlet%E7%BB%A7%E6%89%BF%E5%9B%BE.png)

## HttpServletRequest类

​ 每次只要有请求进入 Tomcat 服务器，Tomcat 服务器就会把请求过来的 HTTP 协议信息解析好封装到 Request 对象中。 然后传递到 service 方法（doGet 和 doPost）中给我们使用。我们可以通过
HttpServletRequest 对象，获取到所有请求的信息。

### 主要的方法如下

![image-20211013193201231](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211013193201231.png)

> 文章首发于GitHub开源项目: [Java成长之路](https://github.com/shaoxiongdu/java-notes) 欢迎大家star!