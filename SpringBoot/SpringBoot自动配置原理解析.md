# SpringBoot自动配置原理解析

> 文章已同步至GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes)

​	SpringBoot的主旨是`约定大于配置`，开发项目初期阶段，我们不需要做过多的配置，SpringBoot已经帮我们自动配置好了大部分的内容，比如`仲裁依赖机制`，自动引入需要的依赖，自动配置等内容。让我们能够将更多的精力放在业务逻辑上，那么，它是如何实现自动配置的呢？

​	首先我们可以看到，在SpringBoot的启动类上，有一个`@SpringBootApplication`的注解。

​	接下来，我们分析这个注解。点进去，发现它主要是由以下的几个注解组合而成的。

```java
@SpringBootConfiguration // 表示这是一个配置类
@EnableAutoConfiguration
@ComponentScan // 包扫描规则
```

我们挨个分析。

## @SpringBootConfiguration

点进去我们发现，它就是一个`Configuration`

```java
@Configuration
@Indexed
public @interface SpringBootConfiguration {
    @AliasFor(
        annotation = Configuration.class
    )
    boolean proxyBeanMethods() default true;
}
```

Spring中我们已经学过这个注解了，他代表当前是一个配置类，所以，在 `SpringBootApplicaton`中标注的`@SpringBootConfiguration`注解的作用就是标注此启动类是一个配置类。

## @ComponentScan

从之前的Spring中我们也知道，这个注解表示IoC容器在进行注册的时候，从此注解中指定的方式进行包扫描，也不用过多纠结。

## @EnableAutoConfiguration

```java
@AutoConfigurationPackage // 通过主程序的所在的包名进行批量注册
@Import(AutoConfigurationImportSelector.class) //
public @interface EnableAutoConfiguration {

	String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

	Class<?>[] exclude() default {};

	String[] excludeName() default {};

}
```

这个注解主要由两个注解组成。我们一一分析

- ### `@AutoConfigurationPackage` :自动配置包

  ```java
  @Import(AutoConfigurationPackages.Registrar.class) //通过主程序的所在的包名进行批量注册
  public @interface AutoConfigurationPackage {
  	String[] basePackages() default {};
  	Class<?>[] basePackageClasses() default {};
  
  }
  ```

  我们发现，这个注解通过`@Import(AutoConfigurationPackages.Registrar.class)`给IoC容器中导入了一个组件`AutoConfigurationPackages.Registrar`

  我们点进去发现，这是由连个方法组成的类，如下所示

  ```java
  static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {
  
  		@Override
  		public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
  			register(registry, new PackageImports(metadata).getPackageNames().toArray(new String[0]));
  		}
  
  		@Override
  		public Set<Object> determineImports(AnnotationMetadata metadata) {
  			return Collections.singleton(new PackageImports(metadata));
  		}
  
  	}
  ```

  我们将断点打到此处，然后进行Debug进行分析。

  我们发现，这个方法给容器中导入了一系列的组件

  通过Debug发现，`metadata`参数代表的是最原始的那个`SpringBootApplication`启动类

  ![image-20210725205925975](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210725205925975.png)

  通过代码我们看到，它new了一个PackageImports对象，将启动类传进去，然后调用了getPackageNames()方法得到了一个包名，debug发现，返回的包名就是我们自己项目中的包名`cn.shaoxiongdu`,然后我们发现它将这个包名封装到了String数组中作为参数，调用了`register`方法。

  所以`register`这个方法就是通过包名，进行组件的批量注册，也就是主程序类所在的包。所以这就是为什么默认的包扫描规则是主程序类所在的包。

  所以注解`EnableAutoConfiguration`的第一部分，`AutoConfigurationPackage`的作用就是通过主程序的所在的包名进行批量注册，我们接下来看第二个注解。

- ### `@Import(AutoConfigurationImportSelector.class)`

  我们发现，这是一个类，点进去，发现了主要的方法如下

  ```java
  @Override
  	public String[] selectImports(AnnotationMetadata annotationMetadata) {
  		if (!isEnabled(annotationMetadata)) {
  			return NO_IMPORTS;
  		}
  		AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(annotationMetadata);
  		return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
  	}
  ```

  通过方法名称发现这个方法返回了我们需要给容器中注册的bean名称的数组。那么我们的重点就在这里。

  ```java
  AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(annotationMetadata); //我们需要给容器中注册的bean名称的数组
  ```

  点进去这个方法，我们继续分析这个方法。

  ```java
  protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
  		if (!isEnabled(annotationMetadata)) {
  			return EMPTY_ENTRY;
  		}
  		AnnotationAttributes attributes = getAttributes(annotationMetadata);
  		List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes); // 获取所有的需要注册的候选组件
  		configurations = removeDuplicates(configurations); // 移除重复的组件
  		Set<String> exclusions = getExclusions(annotationMetadata, attributes);
  		checkExcludedClasses(configurations, exclusions);
  		configurations.removeAll(exclusions);
  		configurations = getConfigurationClassFilter().filter(configurations);
  		fireAutoConfigurationImportEvents(configurations, exclusions);
  		return new AutoConfigurationEntry(configurations, exclusions);
  	}
  ```

  通过Debug我们发现，执行到了第7行的时候`configurations`这个List中已经有了一百多个bean的名称，之后的操作就是对List集合进行一些常规处理并返回。

  ![image-20210725212042406](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210725212042406.png)

  所以我们只需要分析第6行这个方法`getCandidateConfigurations(annotationMetadata, attributes);`

  是它返回了我们需要给容器中默认注册的bean的名称的字符数组。

  我们重新Debug，进入方法

  ```java
  protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
  		List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
  				getBeanClassLoader()); // 获取需要注册的组件集合
  		Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
  				+ "are using a custom packaging, make sure that file is correct.");
  		return configurations;
  	}
  ```

  通过分析，我们发现主要的流程在2行，通过工厂模式加载需要注册的容器集合。

  继续Debug进去此方法

  ```java
  public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
          ClassLoader classLoaderToUse = classLoader;
          if (classLoader == null) {
              classLoaderToUse = SpringFactoriesLoader.class.getClassLoader();
          }
  
          String factoryTypeName = factoryType.getName();
          return (List)loadSpringFactories(classLoaderToUse).getOrDefault(factoryTypeName, Collections.emptyList()); //返回需要注册的组件集合
      }
  ```

  重点在最后一行，通过`loadSpringFactories`方法返回了对应的集合。

  继续Debug进去此方法

  ```java
  private static Map<String, List<String>> loadSpringFactories(ClassLoader classLoader) {
          Map<String, List<String>> result = (Map)cache.get(classLoader); 
          if (result != null) {
              return result;
          } else {
              HashMap result = new HashMap(); 
  
              try {
                  Enumeration urls = classLoader.getResources("META-INF/spring.factories");
  
                  while(urls.hasMoreElements()) {
                      URL url = (URL)urls.nextElement();
                      UrlResource resource = new UrlResource(url);
                      Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                      Iterator var6 = properties.entrySet().iterator();
  
                      while(var6.hasNext()) {
                          Entry<?, ?> entry = (Entry)var6.next();
                          String factoryTypeName = ((String)entry.getKey()).trim();
                          String[] factoryImplementationNames = StringUtils.commaDelimitedListToStringArray((String)entry.getValue());
                          String[] var10 = factoryImplementationNames;
                          int var11 = factoryImplementationNames.length;
  
                          for(int var12 = 0; var12 < var11; ++var12) {
                              String factoryImplementationName = var10[var12];
                              ((List)result.computeIfAbsent(factoryTypeName, (key) -> {
                                  return new ArrayList();
                              })).add(factoryImplementationName.trim());
                          }
                      }
                  }
  
                  result.replaceAll((factoryType, implementations) -> {
                      return (List)implementations.stream().distinct().collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
                  });
                  cache.put(classLoader, result);
                  return result;
              } catch (IOException var14) {
                  throw new IllegalArgumentException("Unable to load factories from location [META-INF/spring.factories]", var14);
              }
          }
      }
  ```

  这个方法，就是返回了需要注册的组件集合。我们分析此方法即可。
  
  首先，debug发现，代码来到了第6行，创建了一个HashMap。然后在try里边，我们发现它加载了一个资源文件`META-INF/spring.factories`，并且是循环的扫描所有依赖中的此文件。通过查看，我们发现，大部分的依赖都有这个文件，少部分的没有。
  
  ![image-20210725222133111](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210725222133111.png)
  
  我们打开`spring-boot-autoconfiguration`依赖，打开他的`spring.factories`文件
  
  有一个key为`org.springframework.boot.autoconfigure.EnableAutoConfiguration`的项
  
  ![image-20210725222546632](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210725222546632.png)
  
  值都叫XXXConfiguration。一个XXXConfiguration对应一个依赖的自动配置类
  
  也就是说，在`spring-boot-autoconfiguration依赖`的`spring.factories`文件里面写死了spring-boot一启动就要给容器中加载的所有配置类，而我们运行下面的主方法
  
  ```java
  public static void main(String[] args) {
  
          //返回IoC容器
          ConfigurableApplicationContext run = SpringApplication.run(Springboot01HelloApplication.class, args);
          
          int beanDefinitionCount = run.getBeanDefinitionCount();
          System.out.println("beanDefinitionCount = " + beanDefinitionCount);
  
      }
  ```
  
  发现结果只有143个。
  
  ![image-20210725223820792](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210725223820792.png)
  
  也就是说，不是所有的组件都会被注册到容器中，通过查看此依赖中的部分配置类，我们发现，
  
  ![image-20210725224121170](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210725224121170.png)
  
  大部分的类都会有`@Conditional`注解，也就是说注册在容器中有条件的，并不是一定会被加载。只有条件满足才会被加载。

## 结论

- ### 对于我们自定义的组件：
  -  通过`@AutoConfigurationPackage`注解
  - 最终调用`register(registry, new PackageImports(metadata).getPackageNames();`方法，得到启动类的包下的组件进行循环注册。

- ### 对于其他的组件：

  - SpringBoot先加载所有的自动配置类  xxxxxAutoConfiguration
  - 每个自动配置类按照条件进行生效，默认都会绑定配置文件指定的值。xxxxProperties里面拿。xxxProperties和配置文件进行了绑定
  - 条件满足则注册到容器中
  - 定制化配置
    - 用户直接自己@Bean替换底层的组件
    - 用户去看这个组件是获取的配置文件什么值就去修改。

**xxxxxAutoConfiguration ---> 组件  --->** **xxxxProperties里面拿值  ----> application.properties**

以上就是SpringBoot的自动配置功能的底层原理了，抛砖引玉，欢迎大家指出不足。

> 文章已同步至GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes) 更多Java相关知识，欢迎访问！