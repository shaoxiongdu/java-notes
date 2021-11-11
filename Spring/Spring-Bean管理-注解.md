## 组件注册

- `@Component/Controller/Service/Repostory` :注册自定义组件到容器中

  1. 加上约定的注解。

  2. 在`@Configuration`注解的类中配置包扫描器

     `@ComponentScan(vlaue = "cn.shaoxiongdu")`

- ### `@Configuration`: 标注配置类

- ### `@Scope` ： 配置是否为单实例

  - `prototype`: 多实例  `懒加载` 每次调用getBean会创建对象
  - `singleton(默认值)` : 单实例  `饿加载` 容器创建完毕就会被创建
    -  `@lazy` 为true 表示设置为 懒加载。（单例模式下）

  - 在Web项目中(过时)
    - request : 同一个请求创建一次
    - session: 同一个会话创建一次

- ### `@Conditional` 按照条件注册Bean

  - 参数是一个`Condition接口的实现类`  该实现类中的matches方法返回true 则添加到容器中。

  - 例子 如果当前系统时window 则添加baiduApi到容器中

    ```java
    	@Bean
        @Conditional(value = WindowsCondition.class)
        public BaiduApi baiduApi(){
            BaiduApi baiduApi = new BaiduApi();
            baiduApi.setKey("key...");
            baiduApi.setToken("token...");
            return baiduApi;
        }
       
    ```

    WindowsCondition是Condition接口的实现类

    ```java
    public class WindowsCondition implements Condition {
    
        /**
         * @param conditionContext 判断条件能使用的上下文环境
         * @param annotatedTypeMetadata 注释信息
         * @return 返回为true 则创建bean
         */
        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
    
            //获取当前环境信息
            Environment environment = conditionContext.getEnvironment();
            //获取系统名称
            String osName = environment.getProperty("os.name");
            if(osName.indexOf("Windows") != -1){
                return true;
            }
            return false;
    
        }
    }
    ```

- ### 直接导入组件: `@Import[Class[]]`  

  ![image-20210724110203379](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210724110203379.png)

  id默认为组件的全类名

  ![image-20210724110254371](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210724110254371.png)

- ### 使用FactoryBean导入组件

  1. 定义一个工厂类实现FactoryBean接口

     ```java
     public class BaiduApiFactory implements FactoryBean<BaiduApi> {
     
         @Override
         public BaiduApi getObject() throws Exception {
             return new BaiduApi("factory 中的key","factory 中的token");
         }
     
         @Override
         public Class<?> getObjectType() {
             return BaiduApi.class;
         }
     
         //是否为单例
         @Override
         public boolean isSingleton() {
             return true;
         }
     }
     ```

  2. 将工厂类注册到容器中

     ```java
     	//方法名即为bean的id
     	@Bean
         public BaiduApiFactory factoryBaiduApi(){
             return new BaiduApiFactory();
         }
     ```

- @Profile 根据当前环境，动态切换

  ```java
  @Configuration
  public class DataSourceConfig {
  
      //开发环境
      @Profile("development")
      @Bean
      public DataSource dataSourceDevelopment(){
          DruidDataSource druidDataSource = new DruidDataSource();
          druidDataSource.setUsername("root");
          druidDataSource.setPassword("1002");
          druidDataSource.setUrl("jdbc:mysql://localhost:3306/mysql");
          druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
          return druidDataSource;
      }
  
      //测试环境
      @Profile("test")
      @Bean
      public DataSource dataSourceTest(){
          DruidDataSource druidDataSource = new DruidDataSource();
          druidDataSource.setUsername("root");
          druidDataSource.setPassword("1002");
          druidDataSource.setUrl("jdbc:mysql://localhost:3306/world");
          druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
          return druidDataSource;
      }
  
      //生产环境
      @Profile("product")
      @Bean
      public DataSource dataSourceProduct(){
          DruidDataSource druidDataSource = new DruidDataSource();
          druidDataSource.setUsername("root");
          druidDataSource.setPassword("1002");
          druidDataSource.setUrl("jdbc:mysql://localhost:3306/sys");
          druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
          return druidDataSource;
      }
  
  }
  ```

  启动时添加参数 -Dspring.profiles.active=XXX 即可动态设置数据源。

## Bean的生命周期

> 容器管理: bean创建 ->  初始化 -> 销毁

### 自定义自定义初始化和销毁方法的四种方式

- #### 通过Bean注解的属性

  首先在bean中定义初始化和销毁方法

  ```java
  public class Car {
  
      public Car() {
          System.out.println("Car构造方法被执行");
      }
  
      //初始化方法
      public void init(){
          System.out.println("car init ");
      }
  
      public void destroy(){
          System.out.println("car destroy");
      }
  
  }
  ```

  然后在配置类中的Bean注解指定

  ```java
  @Bean(initMethod = "init", destroyMethod = "destroy")
      public Car car(){
          return new Car();
      }
  ```

  效果(容器关闭时会回调destory方法，非单例时不会调用)

  ![image-20210724122215517](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210724122215517.png)

- #### 通过让Bean实现InitialContextFactory, DisposableBean接口 重写对应的方法

  ```java
  public class Car implements InitializingBean, DisposableBean {
  
      public Car() {
          System.out.println("Car构造方法被执行");
      }
  
      @Override
      public Context afterPropertiesSet() throws Exception {
          return null;
      }
  
      @Override
      public void destroy() throws Exception {
  
      }
  }
  ```

- #### 使用JSR250规范中的注解: `@PostConstruct` `@PreDestroy`

  ```java
  public class Car{
  
      public Car() {
          System.out.println("Car构造方法被执行");
      }
      
      //初始化方法
      @PostConstruct
      public void init(){
          System.out.println("car init ");
      }
  
      @PreDestroy
      public void destroy(){
          System.out.println("car destroy");
      }
  
  }
  ```

- 通过bean实现BeanPostProcessor接口来实现

  ```java
  public class Car implements BeanPostProcessor {
  
      public Car() {
          System.out.println("Car构造方法被执行");
      }
  
      /**
       *  后置处理器 初始化前进行回调
       * @param bean 注册好的bean对象
       * @param beanName 对象id
       * @return 注册好的bean对象  可以在此处包装
       * @throws BeansException
       */
      @Override
      public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
          System.out.println(bean + ": postProcessBeforeInitialization");
          return bean;
      }
  
      @Override
      public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
          System.out.println(bean + ": postProcessAfterInitialization");
          return bean;
      }
  }
  ```

## bean的属性赋值

- 通过`@Value`:   直接指定

- 通过配置文件赋值 在配置类中加入注解`@PropertySource`注解指出配置文件的位置 

  ```java
  @Configuration
  @PropertySource("baiduApi.properties")
  public class BaiduApiConfig {
  
      @Bean
      public BaiduApi baiduApi(){
          return new BaiduApi();
      }
  
  }
  ```

  通过`@Value("${keyName}")`设置即可

  ```java
  public class BaiduApi {
  
      @Value("${keyName}")
      private String key;
  
      @Value(("${token}"))
      private String token;
  }
  ```


## 自动装配

> Spring利用依赖注入（DI），完成对IOC容器中各个组件的依赖关系赋值。

- ### `@Autowired`  Spring的规范

  - 默认按照类型注入
  - 添加注解`@Qualifier("id名称")`来设置按照名称注入，此时，待注入的属性名称和注入的组件id不同。

  - 属性`required`表示是否强制，true的时候，容器中没有对应的组件，就会报错。如果为false，IOC容器中没找到对应的组件，不报错，待注入的属性为null。
  - 这个注解可以标注在有参构造，方法，属性等位置。此时，方法或者构造方法或者属性的值会从IOC容器中获取。

- ### `@Resource` Java下的规范
  - Resource 默认按照名称注入。也可添加属性name按照名称注入。

- ### `@Inject` Java下的规范

  - @Inject 使用需要导入对应依赖。 不支持require=false;

- ### Aware接口

  自定义组件想要使用SpringIOC容器底层的一些组件（ApplicatonContext,BeanFactory….），可以让自定义组件实现XXXAware接口，在创建对象放入容器中的时候，就会调用对应的方法。获得对应的底层组件。

  

  