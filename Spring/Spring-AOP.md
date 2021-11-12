# 基于注解的AOP

## 基本过程

1. 导入AOP模块依赖 spring-aspects

2. 定义一个业务逻辑类，在业务逻辑执行的时候进行日志打印

   ```java
   /**
    * 数学工具类
    */
   public class MathCalculatorUtils {
   
       public int div(Integer m, Integer n){
           return m / n;
       }
   
   }
   ```

3. 定义一个日志切面类

   ```java
   @Aspect //切面类
   public class LogAspects {
   
       private Date start = null;
   
       //定义公共切入点表达式
       @Pointcut("execution(* cn.shaoxiongdu.utils.*.*(..))")
       public void pointCut(){}
   
       //通知方法 前置通知： 目标方法执行之前 执行
       @Before(value = "pointCut()")
       public void logStart(JoinPoint joinPoint){
           start = new Date();
           System.out.println("@before {"+joinPoint.getSignature().getName()+"} 执行开始，参数列表{"+ Arrays.toString(joinPoint.getArgs()) +"}");
   
       }
   
       //后置方法 目标方法执行完毕之后 执行（不论目标方法是否成功执行都执行）
       @After("pointCut()")
       public void logEnd(JoinPoint joinPoint){
           System.out.println("@After {"+joinPoint.getSignature().getName()+"}执行结束");
           System.out.println("执行时间: {" + (new Date().getTime() - start.getTime()) + "ms}");
   
       }
   
       //方法正常返回之后执行
       @AfterReturning( value = "pointCut()",returning = "result")
       public void logReturn(JoinPoint joinPoint,Object result){
           System.out.println("@AfterReturning {"+joinPoint.getSignature().getName()+"}正常返回!运行结果{"+result+"}");
   
       }
   
       //方法抛出异常 执行
       @AfterThrowing(value = "pointCut()",throwing = "exception")
       public void logException(JoinPoint joinPoint,Exception exception ){
           System.out.println("@AfterThrowing {"+joinPoint.getSignature().getName()+"}异常,!异常信息{"+exception+"}");
       }
   
   }
   ```

4. 将切面类和目标类都加入到容器中

   ```java
   @Configuration //这是一个配置类
   @EnableAspectJAutoProxy //开启Aspect的aop模式
   public class MainConfigAOP {
   
       @Bean
       public MathCalculatorUtils mathCalculatorUtils(){
           return new MathCalculatorUtils();
       }
   
       @Bean
       public LogAspects logAspects(){
           return new LogAspects();
       }
   
   }
   ```

5. 测试

   ```java
   public class TestMain {
   
       private ApplicationContext context = new AnnotationConfigApplicationContext(MainConfigAOP.class);
   
       @Test
       public void test(){
   
           MathCalculatorUtils mathCalculatorUtils = context.getBean(MathCalculatorUtils.class);
   
           System.out.println(mathCalculatorUtils.div(100, 10));
   
       }
   
   }
   ```

6. 结果

   ![image-20211112100032797](https://images-1301128659.cos.ap-beijing.myqcloud.com/image-20211112100032797.png)

## 基本原理

### 注解@EnableAspectJAutoProxy

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AspectJAutoProxyRegistrar.class})
public @interface EnableAspectJAutoProxy {
    boolean proxyTargetClass() default false;

    boolean exposeProxy() default false;
}
```

该注解给容器中导入了AspectJAutoProxyRegistrar,该类实现了ImportBeanDefinitionRegistrar接口，可以给容器中注册自定义组件。

