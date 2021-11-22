## MQ引言

### 1.1 什么是MQ

​		MQ(Message Quene) :  翻译为消息队列,通过典型的生产者和消费者模型,生产者不断向消息队列中生产消息，消费者不断的从队列中获取消息。因为消息的生产和消费都是异步的，而且只关心消息的发送和接收，没有业务逻辑的侵入,轻松的实现系统间解耦。别名为 消息中间件通过利用高效可靠的消息传递机制进行平台无关的数据交流并基于数据通信来进行分布式系统的集成。

### 1.2 不同的MQ产品的特点

![image-20211122092650436](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211122092650436.png)

​		ActiveMQ 是Apache出品，最流行的，能力强劲的开源消息总线。它是一个完全支持JMS规范的的消息中间件。丰富的API,多种集群架构模式让ActiveMQ在业界成为老牌的消息中间件,在中小型企业 颇受欢迎!

![image-20211122092706884](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211122092706884.png)

​		Kafka是LinkedIn开源的分布式发布-订阅消息系统，目前归属于Apache顶级项目。Kafka主要特点是基于Pull的模式来处理消息消费，追求高吞吐量，一开始的目的就是用于日志收集和传输。0.8版本开始支持复制，不支持事务，对消息的重复、丢失、错误没有严格要求，适合产生大量数据的互联网服务的数据收集业务。



 ![image-20211122092730452](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211122092730452.png)

​		RocketMQ是阿里开源的消息中间件，它是纯Java开发，具有高吞吐量、高可用性、适合大规模分布式系统应用的特点。RocketMQ思路起源于Kafka，但并不是Kafka的一个Copy，它对消息的可靠传输及事务性做了优化，目前在阿里集团被广泛应用于交   易、充值、流计算、消息推送、日志流式处理、binglog分发等场景。

 ![image-20211122092745948](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211122092745948.png)

- RabbitMQ是使用Erlang语言开发的开源消息队列系统，基于AMQP协议来实现。AMQP的主要特征是面向消息、队列、路由（包括点对点和发布/订阅）、可靠性、安全。AMQP协议更多用在企业系统内对数据一致性、稳定性和可靠性要求很高的场景，对性能和吞吐量的要求还在其次。
- RabbitMQ比Kafka可靠，Kafka更适合IO高吞吐的处理，一般应用在大数据日志处理或对实时性（少量延迟），可靠性（少量丢数据）要求稍低的场景使用，比如ELK日志收集。		

## RabbitMQ安装

​	[https://blog.csdn.net/unique_perfect/article/details/108643804 ](https://blog.csdn.net/unique_perfect/article/details/108643804 )

## 支持的消息模型

![2020103018025840](https://images-1301128659.cos.ap-beijing.myqcloud.com/2020103018025840.png)

![20201030180315852](https://images-1301128659.cos.ap-beijing.myqcloud.com/20201030180315852.png)

## 实战

1. 引入依赖

   ```xml
   		<!--引入rabbitmq相关依赖-->
           <dependency>
               <groupId>com.rabbitmq</groupId>
               <artifactId>amqp-client</artifactId>
               <version>5.7.2</version>
           </dependency>
   ```

2. 创建新的虚拟主机及用户，将虚拟主机分配给用户。

   ![image-20211122103814772](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211122103814772.png)

3. 生产者代码

   ```java
   /**
    * 生产者
    */
   public class Provider {
   
       /**
        * 生产消息
        */
       @Test
       public void sendMessage() throws IOException, TimeoutException {
   
           //创建连接MQ的工厂对象
           ConnectionFactory connectionFactory = new ConnectionFactory();
   
           //设置连接属性
           connectionFactory.setHost("152.136.194.158");
           connectionFactory.setPort(5672);
           connectionFactory.setVirtualHost("ems");
           connectionFactory.setUsername("ems");
           connectionFactory.setPassword("ems");
   
           //获取连接对象
           Connection connection = connectionFactory.newConnection();
   
           //获取连接中通道
           Channel channel = connection.createChannel();
   
           /**
            * 通道绑定队列
            * 队列名称 不存在自动创建
            * 是否持久化
            * 是否独占队列
            * 是否在消费完成后自动删除队列
            * 附加参数
            */
           channel.queueDeclare("hello",false,false,false,null);
   
           /**
            * 发布消息
            * 1. 交换机名称
            * 2. 队列名称
            * 3. 传递消息额外设置
            * 4. 具体消息
            */
           channel.basicPublish("","hello",null,"hello rabbitmq".getBytes());
           
           channel.close();
           connection.close();
   
       }
   
   }
   ```

   执行之后，我们发现在web管理控制台中有了hello队列，并且有三条消息（运行了三次）

   ![image-20211122110053084](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211122110053084.png)

4. 消费者代码

   ```java
   /**
    * 消费者
    */
   public class Customer {
   
       public static void main(String[] args) throws IOException, TimeoutException {
   
           //创建连接MQ的工厂对象
           ConnectionFactory connectionFactory = new ConnectionFactory();
   
           //设置连接属性
           connectionFactory.setHost("152.136.194.158");
           connectionFactory.setPort(5672);
           connectionFactory.setVirtualHost("ems");
           connectionFactory.setUsername("ems");
           connectionFactory.setPassword("ems");
   
           //获取连接对象
           Connection connection = connectionFactory.newConnection();
   
           //获取连接中通道
           Channel channel = connection.createChannel();
   
           /**
            * 通道绑定队列
            * 队列名称 不存在自动创建
            * 是否持久化
            * 是否独占队列
            * 是否在消费完成后自动删除队列
            * 附加参数
            */
           channel.queueDeclare("hello",false,false,false,null);
   
           /**
            * 消费消息
            * 1. 消费的队列
            * 2. 开始消息的自动确认机制
            * 3. 回调接口 重写该接口的handleDelivery方法 处理消息
            */
           channel.basicConsume("hello",true,new DefaultConsumer(channel){
   
               @Override
               public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                   System.out.println("消费者拿到的消息:"+ new String(body) );
               }
           });
   
       }
   
   }
   ```

5. 启动消费者，然后启动生产者，消费者就会拿到对应的消息。

   ![image-20211122111923312](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211122111923312.png)

6. 封装获取连接工具类

   ```java
   /**
    * 单例模式 饿汉式
    */
   public class RabbitMQUtils {
   
   
       private static ConnectionFactory connectionFactory;
   
       static {
           connectionFactory = new ConnectionFactory();
           //设置连接属性
           connectionFactory.setHost("152.136.194.158");
           connectionFactory.setPort(5672);
           connectionFactory.setVirtualHost("ems");
           connectionFactory.setUsername("ems");
           connectionFactory.setPassword("ems");
       }
   
       /**
        * 获取连接
        * @return
        */
       public static Connection getConnection(){
   
           try {
               return connectionFactory.newConnection();
           } catch (IOException | TimeoutException e) {
               e.printStackTrace();
           }
           return null;
       }
   
       public static void close(Connection connection, Channel channel){
   
           try {
               if(channel != null) channel.close();
               if(connection != null) connection.close();
   
           }catch (Exception e){
               e.printStackTrace();
           }
   
       }
   
   }
   ```

   