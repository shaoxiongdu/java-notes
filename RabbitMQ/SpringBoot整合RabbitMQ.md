# SpringBoot整合RabbitMQ

1. 添加依赖

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-amqp</artifactId>
   </dependency>
   ```

2. 配置连接属性

   ```yml
   spring:
     rabbitmq:
       host: 152.136.194.158
       port: 5672
       virtual-host: ems
       username: admin
       password: admin
   ```

3. 编写生产者

   ```java
   	@Autowired
       RabbitTemplate rabbitTemplate;
   
       @Test
       public void test(){
   
           //发送消息 队列名称 消息内容
           rabbitTemplate.convertAndSend("hello","hello springboot rabbitmq");
   
       }
   ```

4. 编写消费者

   ```java
   @Component
   @RabbitListener(queuesToDeclare = @Queue("hello")) //表示这是一个消费者 监听hello队列
   public class HelloCustomer {
   
       @Autowired
       private RabbitTemplate rabbitTemplate;
   
   
       @RabbitHandler //表示  这是一个消费者方法
       public void receivel(String message){
   
           System.out.println(message);
   
       }
   
   }
   ```

5. 运行生产者 可以看到消费者可以正常消费。

   ![image-20211122180103402](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211122180103402.png)

   