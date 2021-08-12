# Nginx原理解析

> 文章已同步至GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes)

## master和worker

![image-20210812200852626](https://gitee.com/ShaoxiongDu/imageBed/raw/master/image-20210812200852626.png)



- 当linux启动的时候，会有两个和nginx相关的进程，一个是`master`,一个是`worker`。

  ![image-20210812203128324](https://gitee.com/ShaoxiongDu/imageBed/raw/master/image-20210812203128324.png)

## master如何工作

![image-20210812201111349](https://gitee.com/ShaoxiongDu/imageBed/raw/master/image-20210812201111349.png)



- 当客户端发送请求到`nginx`之后，`master`会接收到这个请求，然后通知所有的`worker`进程，此时，`worker`会对这个请求进行争抢。某个`worker`抢到请求之后，就会根据设置好的步骤进行请求转发。
- 一个`master`和多个`worker`的好处
  - 可以使用`nginx -s reload`热部署。 当进行热部署的时候，正常的`worker`会重启，但是正在处理请求的`worker`不会，等请求处理完毕之后，才会进行重启。
  - 对于每一个独立的`worker`，在进行并发的时候，不需要考虑加锁的问题。而且各个`worker`之间不会互相影响。降低了业务瞬间失效的可能。

## worker的设置

### worker的数量

- nginx和redis一样采用了io多路复用机制，每个worker都是一个独立的进程。每个`worker`的进程都会将cpu的性能发挥到极致。
- 所以worker的数量和服务器的cpu数量相等是最合适的。（几核就设置几个worker）
- 设置少了会浪费cpu性能，导致处理业务请求的速度变低。
- 设置多了会造成cpu频繁切换上下文带来的损耗。

> 文章已同步至GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes) 更多Java相关知识，欢迎访问！