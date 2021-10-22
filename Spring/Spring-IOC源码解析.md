## 容器的创建和刷新过程

Spring的IOC是Spring的核心之一，容器的创建和刷新主要的流程如下：

创建和刷新的过程集中在`AbstractApplicationContext`类的`refresh`方法中，该方法定义如下

```java
public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			StartupStep contextRefresh = this.applicationStartup.start("spring.context.refresh");

			// 进行容器刷新前的欲处理工作
			prepareRefresh();

			// 通过GenericApplicationContext类的构造方法创建容器，并且进行一些必要的设置工作
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			// 对创建的容器进行一些主要组件的注册
			prepareBeanFactory(beanFactory);

			try {
				// 容器的前置处理器 空方法 主要用于子类实现之后注册一些必备的组件
				postProcessBeanFactory(beanFactory);

				StartupStep beanPostProcess = this.applicationStartup.start("spring.context.beans.post-process");
				// 对容器中的bean进行循环执行bean的前置处理器方法 （按照优先级）
				invokeBeanFactoryPostProcessors(beanFactory);

				// 容器的后置处理器
				registerBeanPostProcessors(beanFactory);
				beanPostProcess.end();

				// 初始化MessageSource工作包括：国际化，消息绑定，消息解析等。
				initMessageSource();

				// 初始化事件派发器
				initApplicationEventMulticaster();

				// 主要初始化特定上下文子类中的其他特殊 bean。
				onRefresh();

				// 将所有的监听器注册到容器中
				registerListeners();

				// 实例化所有的单实例bean
				finishBeanFactoryInitialization(beanFactory);

				// 最后一步：完成容器的刷新工作 发布相应的事件。 
				finishRefresh();
			}

			catch (BeansException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Exception encountered during context initialization - " +
							"cancelling refresh attempt: " + ex);
				}

				// 如果抛出异常，则销毁已经创建的单例以节约资源。
				destroyBeans();

				// 重写设置激活状态
				cancelRefresh(ex);

				// 将异常抛出，传播给调用者。
				throw ex;
			}

			finally {
				// 重置 Spring 核心缓存
				// might not ever need metadata for singleton beans anymore...
				resetCommonCaches();
				contextRefresh.end();
			}
		}
	}
```

1. ### 首先在代码的第四行，调用了此类中的`prepareRefresh`方法

   这个方法是`进行容器刷新前的预处理工作`的，该方法的定义如下

   ```java
   protected void prepareRefresh() {
           this.startupDate = System.currentTimeMillis(); //记录启动时间
           this.closed.set(false); //是否关闭
           this.active.set(true); //是否激活
           if (this.logger.isDebugEnabled()) {
               if (this.logger.isTraceEnabled()) {
                   this.logger.trace("Refreshing " + this);
               } else {
                   this.logger.debug("Refreshing " + this.getDisplayName());
               }
           }
   
           this.initPropertySources(); //设置属性源
           this.getEnvironment().validateRequiredProperties(); //对设置的属性源进行校验
           if (this.earlyApplicationListeners == null) {
               this.earlyApplicationListeners = new LinkedHashSet(this.applicationListeners);
           } else {
               this.applicationListeners.clear();
               this.applicationListeners.addAll(this.earlyApplicationListeners);
           }
   
           this.earlyApplicationEvents = new LinkedHashSet();
       }
   ```

   1. 在容器刷新的预处理中，代码第2行设置了容器的启动日期，之后设置一些容器的基本标志，包括是否关闭，以及是否激活等状态。
   2. 接下来的代码会判断当前运行环境是否为Debug，如果是，输入一些提示信息。
   3. 直到代码的第13行，执行了`initPropertySources`方法，通过Debug我们发现，这个方法是一个空方法 ,这个方法在此处什么也不做，但是我们可以继承这个类来自定义个性化的属性配置
   4. 接着，流程执行到prepareRefresh方法中的14行，`this.getEnvironment().validateRequiredProperties()`这个方法主要对上一步的属性配置进行校验。
   5. 接着会对域`earlyApplicationListeners`进行非null校验
   6. 执行到22行，创建了一个List集合，用于存储容器中的一些早期事件，如果有事件发生，则保存到此list中，之后对应的事件派发器准备好之后，将事件派发出去。此时容器的预处理工作结束。

2. ### 然后执行到refresh的5行

   通过`obtainFreshBeanFactory`方法获取了`BeanFactory`

   该方法的定义如下

   ```java
   protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
   		refreshBeanFactory(); //容器创建
   		return getBeanFactory();//返回刷新后的容器
   	}
   ```

   1. 方法首先调用`refreshBeanFactory();`进行容器的刷新工作，通过Debug，我们发现流程进入了`GenericApplicationContext`类中，我们发现此类的构造方法创建了一个`BeanFactory`，

      ```java
      public GenericApplicationContext() {
      		this.beanFactory = new DefaultListableBeanFactory();
      }
      ```

   2. 我们回到`refreshBeanFactory`方法，此方法中，设置了`Serialization`的id，然后将创建的`BeanFactory`返回给调用者，

      ```java
      protected final void refreshBeanFactory() throws IllegalStateException {
      		if (!this.refreshed.compareAndSet(false, true)) {
      			throw new IllegalStateException(
      					"GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
      		}
      		this.beanFactory.setSerializationId(getId()); //设置serializationId
      }
      ```

   3. 也就是之前的`AbstractApplicationContext`类中的`refresh`方法的第五行，流程回来。

3. ###  然后执行到6行 this.prepareBeanFactory(beanFactory);

   对`BeanFacotory`进行预处理的工作，对其进行一些设置。

   ```java
   protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
   		// Tell the internal bean factory to use the context's class loader etc.
   		beanFactory.setBeanClassLoader(getClassLoader()); //设置类加载器
   		if (!shouldIgnoreSpel) {
   			beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
   		}
   		beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment())); //设置支持的表达式解析器
   
   		// 设置忽略的自动装配接口
   		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
   		beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
   		beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
   		beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
   		beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
   		beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
   		beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
   		beanFactory.ignoreDependencyInterface(ApplicationStartupAware.class);
   
   		//注册可以解析的自动装配  包括BeanFactory，ResourceLoader，ResourceLoader，ApplicationContext等。
   		beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
   		beanFactory.registerResolvableDependency(ResourceLoader.class, this);
   		beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
   		beanFactory.registerResolvableDependency(ApplicationContext.class, this);
   
   		// 添加后置处理器
   		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));
   
   		// Detect a LoadTimeWeaver and prepare for weaving, if found.
   		if (!NativeDetector.inNativeImage() && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
   			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
   			// Set a temporary ClassLoader for type matching.
   			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
   		}
   
   		// 注册环境变量
   		if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
   			beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
   		}
   		if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
   			beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
   		}
   		if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
   			beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
   		}
   		if (!beanFactory.containsLocalBean(APPLICATION_STARTUP_BEAN_NAME)) {
   			beanFactory.registerSingleton(APPLICATION_STARTUP_BEAN_NAME, getApplicationStartup());
   		}
   	}
   ```

4. ### 执行到`refresh`的16行 postProcessBeanFactory(beanFactory); 方法，此方法是一个空方法 主要用于子类实现之后对容器进行前置工作

5. ### 流程来到`refresh`方法的18行，在Bean Factory标准初始流程之后执行

6. ### 来到`refresh`20行，invokeBeanFactoryPostProcessors方法被执行 该方法定义如下

   ```java
   protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
   		PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
   
   		if (!NativeDetector.inNativeImage() && beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
   			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
   			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
   		}
   	}
   ```

   1. 第一行调用`invokeBeanFactoryPostProcessors`方法，此方法会实例化并调用所有已注册的 BeanFactoryPostProcessor bean，如果给出则遵守显式顺序。 必须在单例实例化之前调用。

      该方法的定义如下

      ```java
      public static void invokeBeanFactoryPostProcessors(
      			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
      
      		Set<String> processedBeans = new HashSet<>();
      
               //判断beanfactory的类型 如果是BeanDefinitionRegistry则对其进行遍历并执行bean的前置处理器
      		if (beanFactory instanceof BeanDefinitionRegistry) {
      			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
      			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
      			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
      
                  // 循环将传入的beanFacotory中bean的前置处理器添加到list集合中
      			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
      				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
      					BeanDefinitionRegistryPostProcessor registryProcessor =
      							(BeanDefinitionRegistryPostProcessor) postProcessor;
      					registryProcessor.postProcessBeanDefinitionRegistry(registry);
      					registryProcessors.add(registryProcessor);
      				}
      				else {
      					regularPostProcessors.add(postProcessor);
      				}
      			
      			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();
      
      			
                      // 通过优先级进行排序 
      			String[] postProcessorNames =
      					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
      			for (String ppName : postProcessorNames) {
                      // 首先判断bean是否实现了PriorityOrdered接口
      				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
      					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
      					processedBeans.add(ppName);
      				}
      			}
      			sortPostProcessors(currentRegistryProcessors, beanFactory);
      			registryProcessors.addAll(currentRegistryProcessors);
                      //执行bean的前置处理器
      			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
      			currentRegistryProcessors.clear();
      
      
      			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
                      
                     
      			for (String ppName : postProcessorNames) {
                      
                      // 其次判断是否实现了Ordered接口
      				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
      					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
      					processedBeans.add(ppName);
      				}
      			}
      			sortPostProcessors(currentRegistryProcessors, beanFactory);
      			registryProcessors.addAll(currentRegistryProcessors);
                      
                      //执行bean的前置处理器
      			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
      			currentRegistryProcessors.clear();
      
      			boolean reiterate = true;
      			while (reiterate) {
      				reiterate = false;
      				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
      				for (String ppName : postProcessorNames) {
      					if (!processedBeans.contains(ppName)) {
      						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
      						processedBeans.add(ppName);
      						reiterate = true;
      					}
      				}
      				sortPostProcessors(currentRegistryProcessors, beanFactory)
                          
                          
      				registryProcessors.addAll(currentRegistryProcessors);
      				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
      				currentRegistryProcessors.clear();
      			}
      
      			//执行没有实现任何优先级的bean的前置处理器;
      			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
      			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
      		}
      
      		else {
      			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
      		}
      
      
      		String[] postProcessorNames =
      				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
      
      			//判断beanfactory的类型 如果是PriorityOrdered则对其进行遍历并执行bean的前置处理器
      		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
      		List<String> orderedPostProcessorNames = new ArrayList<>();
      		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
      		for (String ppName : postProcessorNames) {
      			if (processedBeans.contains(ppName)) {
      			
      			}
      			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
      				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
      			}
      			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
      				orderedPostProcessorNames.add(ppName);
      			}
      			else {
      				nonOrderedPostProcessorNames.add(ppName);
      			}
      		}
      
      		// 首先执行priorityOrderedPostProcessors接口的bean的前置处理器
      		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
      		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);
      
      		//判断beanfactory的类型 如果是BeanFactoryPostProcessor则对其进行遍历并执行bean的前置处理器
      		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
      		for (String postProcessorName : orderedPostProcessorNames) {
      			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
      		}
      		sortPostProcessors(orderedPostProcessors, beanFactory);
      		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);
      
      		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
      		for (String postProcessorName : nonOrderedPostProcessorNames) {
      			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
      		}
      		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);
      
      		//清除缓存的合并 bean 定义，因为后处理器可能已经修改了原始元数据，例如替换值中的占位符..
      		beanFactory.clearMetadataCache();
      	}
      ```

7. ### 来到`refresh`方法的23行。

   registerBeanPostProcessors(beanFactory);

   对容器中的bean进行后置处理  方法的定义如下

   ```java
   protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
   		PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
   	}
   ```

   继续进入`registerBeanPostProcessors`方法

   ```java
   public static void registerBeanPostProcessors(
   			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
   
       	//获取容器中所有的postProcessorNames
   		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
   
   		
   		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
       	// 加入自己的后置处理器
   		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));
   
   		//分离实现 PriorityOrdered 的 BeanPostProcessor
   		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
   		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
   		List<String> orderedPostProcessorNames = new ArrayList<>();
   		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
       	// 循环所有的后置处理器 通过MergedBeanDefinitionPostProcessor接口和Ordered接口进行优先级排序
   		for (String ppName : postProcessorNames) {
   			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
   				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
   				priorityOrderedPostProcessors.add(pp);
                   
   				if (pp instanceof MergedBeanDefinitionPostProcessor) {
   					internalPostProcessors.add(pp);
   				}
   			}
   			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
   				orderedPostProcessorNames.add(ppName);
   			}
   			else {
   				nonOrderedPostProcessorNames.add(ppName);
   			}
   		}
   
   		// 首先，注册实现 PriorityOrdered 的 BeanPostProcessors。
   		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
   		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);
   
   	
   		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
   		for (String ppName : orderedPostProcessorNames) {
   			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
   			orderedPostProcessors.add(pp);
   			if (pp instanceof MergedBeanDefinitionPostProcessor) {
   				internalPostProcessors.add(pp);
   			}
   		}
       
       	//接下来，注册实现 Ordered 的 BeanPostProcessors。
       	sortPostProcessors(orderedPostProcessors, beanFactory);
   		registerBeanPostProcessors(beanFactory, orderedPostProcessors);
   
   		// 现在，注册所有常规 BeanPostProcessor。
   		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
   		for (String ppName : nonOrderedPostProcessorNames) {
   			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
   			nonOrderedPostProcessors.add(pp);
   			if (pp instanceof MergedBeanDefinitionPostProcessor) {
   				internalPostProcessors.add(pp);
   			}
   		}
   		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);
   
   		// 最后，重新注册所有内部 BeanPostProcessor。
   		sortPostProcessors(internalPostProcessors, beanFactory);
   		registerBeanPostProcessors(beanFactory, internalPostProcessors);
   
   		// 将用于检测内部 bean 的后处理器重新注册为 ApplicationListeners，
   		// 将其移动到处理器链的末端（用于获取代理等）。
   		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
   	}
   ```

8. ### 来到`refresh`方法的27行。 initMessageSource（）

    初始化MessageSource工作包括：国际化，消息绑定，消息解析等。

   定义如下

   ```java
   protected void initMessageSource() {
       
       	//获取容器
   		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
       
       	//判断容器中是否有id为MESSAGE_SOURCE_BEAN_NAME的组件
   		if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
               //如果有 赋值给自己的属性
   			this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
   			// 使 MessageSource 知道父 MessageSource。
   			if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {				
   				HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
   				if (hms.getParentMessageSource() == null) {
                       // 如果容器中没有id为MESSAGE_SOURCE_BEAN_NAME的组件 则创建一个默认的
   					hms.setParentMessageSource(getInternalParentMessageSource());
   				}
   			}
               
               //用于debug环境下打印提示信息
   			if (logger.isTraceEnabled()) {
   				logger.trace("Using MessageSource [" + this.messageSource + "]");
   			}
   		}
   		else {
   			//给容器中注册空的 MessageSource 使得容器可以接受 getMessage 的调用
   			DelegatingMessageSource dms = new DelegatingMessageSource();
   			dms.setParentMessageSource(getInternalParentMessageSource());
   			this.messageSource = dms;
   			beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
   			if (logger.isTraceEnabled()) {
   				logger.trace("No '" + MESSAGE_SOURCE_BEAN_NAME + "' bean, using [" + this.messageSource + "]");
   			}
   		}
   	}
   ```

9. ###  接着来到`refresh`方法的30行				initApplicationEventMulticaster();

   ```java
   protected void initApplicationEventMulticaster() {
       
       	//获取容器
   		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
       	//如果容器中存在APPLICATION_EVENT_MULTICASTER_BEAN_NAME
   		if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
   
               //将自己的组件设置为容器中的组件
               this.applicationEventMulticaster =
   					beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
   			if (logger.isTraceEnabled()) {
   				logger.trace("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
   			}
   		}
       	
   		else {
               //如果上一步灭有配置  则创建一个默认的
   			this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
               // 并且注册到容器中
   			beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
   			if (logger.isTraceEnabled()) {
   				logger.trace("No '" + APPLICATION_EVENT_MULTICASTER_BEAN_NAME + "' bean, using " +
   						"[" + this.applicationEventMulticaster.getClass().getSimpleName() + "]");
   			}
   		}
   	}
   ```

10. ### 接着来到`refresh`方法的32行  onRefresh();

    ​	主要初始化特定上下文子类中的其他特殊 bean。

    ```java
    protected void onRefresh() throws BeansException {
    		// For subclasses: do nothing by default.
    	}
    ```

    空实现，留给子类重写，然后在容器刷新的时候，自定义逻辑。

11. ### 接着来到`refresh`方法的36行  registerListeners();

    将所有的监听器注册到容器中

    ```java
    protected void registerListeners() {
    		// 拿到所有的监听器
    		for (ApplicationListener<?> listener : getApplicationListeners()) {
    			getApplicationEventMulticaster().addApplicationListener(listener);
    		}
    
    		// 将每个监听器添加到事件派发器中
    		String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
    		for (String listenerBeanName : listenerBeanNames) {
    			getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
    		}
    
    		// 派发之前步骤产生的事件
    		Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
    		this.earlyApplicationEvents = null;
    		if (!CollectionUtils.isEmpty(earlyEventsToProcess)) {
    			for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
    				getApplicationEventMulticaster().multicastEvent(earlyEvent);
    			}
    		}
    	}
    ```

12. ###  接着来到`refresh`方法的39行 finishBeanFactoryInitialization

    实例化所有的单实例bean

```java
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
	//转换
    if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
				beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
			beanFactory.setConversionService(
					beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
		}

		if (!beanFactory.hasEmbeddedValueResolver()) {
			beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
		}

		//尽早初始化 LoadTimeWeaverAware bean 以允许尽早注册它们的转换器。
		String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
		for (String weaverAwareName : weaverAwareNames) {
			getBean(weaverAwareName);
		}

		// 停止使用临时 ClassLoader 进行类型匹配
		beanFactory.setTempClassLoader(null);

		//允许缓存所有 bean 定义元数据 因为是单实例模式 
		beanFactory.freezeConfiguration();

		// 初始化剩下的单实例bean
		beanFactory.preInstantiateSingletons();
	}
```

​	在此方法中，所有的代码都是为了最后一步初始化单实例bean，26行中调用了容器的`preInstantiateSingletons`方法 定义如下

```java
public void preInstantiateSingletons() throws BeansException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Pre-instantiating singletons in " + this);
        }
		// 首先获取所有的bean
        List<String> beanNames = new ArrayList(this.beanDefinitionNames);
        Iterator var2 = beanNames.iterator();

    	// 遍历所有的bean 依次实例化
        while(true) {
            String beanName;
            Object bean;
            do {
                while(true) {
                    RootBeanDefinition bd;
                    do {
                        do {
                            do {
                                if (!var2.hasNext()) {
                                    var2 = beanNames.iterator();

                                    while(var2.hasNext()) {
                                        beanName = (String)var2.next();
                                        Object singletonInstance = this.getSingleton(beanName);
                                        
                                        //如果bean是单实例的
                                        if (singletonInstance instanceof SmartInitializingSingleton) {
                                            StartupStep smartInitialize = this.getApplicationStartup().start("spring.beans.smart-initialize").tag("beanName", beanName);
                                            SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton)singletonInstance;
                                            if (System.getSecurityManager() != null) {
                                                AccessController.doPrivileged(() -> {
                                                    smartSingleton.afterSingletonsInstantiated();
                                                    return null;
                                                }, this.getAccessControlContext());
                                            } else {
                                                smartSingleton.afterSingletonsInstantiated();
                                            }

                                            smartInitialize.end();
                                        }
                                    }

                                    return;
                                }

                                beanName = (String)var2.next();
                                bd = this.getMergedLocalBeanDefinition(beanName);
                            } while(bd.isAbstract());
                        } while(!bd.isSingleton());
                    } while(bd.isLazyInit());

                    if (this.isFactoryBean(beanName)) {
                        bean = this.getBean("&" + beanName);
                        break;
                    }

                    this.getBean(beanName);
                }
            } while(!(bean instanceof FactoryBean));

            FactoryBean<?> factory = (FactoryBean)bean;
            boolean isEagerInit;
            if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                SmartFactoryBean var10000 = (SmartFactoryBean)factory;
                ((SmartFactoryBean)factory).getClass();
                isEagerInit = (Boolean)AccessController.doPrivileged(var10000::isEagerInit, this.getAccessControlContext());
            } else {
                //判断是否是SmartFactoryBean接口的bean
                isEagerInit = factory instanceof SmartFactoryBean && ((SmartFactoryBean)factory).isEagerInit();
            }
			// 如果是 则调用ban自己重写的getObject方法创建组件
            if (isEagerInit) {
                this.getBean(beanName);
            }
        }
    }
```

​	对所有的Bean进行实例化主要的步骤如下： 

1. 获取所有的bean , 然后进行遍历  ，
2. 判断如果bean是单实例的，并且没有实现`SmartFactoryBean`接口，则进行实例化，
3. 如果实现了`SmartFactoryBean`，则调用它重写的getObjece()方法，实例化组件。
   1. 首先调用`getBean(name)`方法，73行所示
   2. 通过Debug，发现调用了`doGetBean(name,null,null,false)`方法
      1. 首先获取缓存中保存的单实例bean,如果能获取到，说明之前已经被创建过了，（所有的单实例bean在创建的时候都会被缓存）
      2. 如果没有拿到，则开始创建过程
         1. 首先获取父工厂，然后标记当前的bean已经被创建（防止多线程下的安全问题）
         2. 获取bean的定义信息，并获取 bean依赖的其他bean。将按照同样的getBean方法对依赖的进行创建。
         3. 调用`createBean`方法
            1. 给`beanPostProcessor`创建代理对象的机会。
            2. 如果1中返回的不为Null,则利用代理对象进行创建bean，并成功返回bean
            3. 如果1中返回为Null，则进入`doCreateBean(name)`
               1. 调用`createBeanInstance`方法，此时，Debug进入bean的构造方法，实例出bean对象。
               2. 添加到缓存中。
               3. 执行`populateBean()`
                  1. 拿到bean的所有后置处理器并执行方法。
                  2. 执行`applyPropertyValues（）`方法 为bean的属性利用`反射机制`中获取的set方法进行赋值。
                  3. 判断bean中`是否实现了执行init接口`或者`是否在注解中指定了Init方法`，如果有，则回调bean的初始化方法。即自定义的Init方法。
                  4. 执行后置处理器初始化之后的方法。
               4. `doCreateBean(name)`执行完毕，创建bean完成。
            4. 判断bean中`是否实现了销毁接口`或者`是否在注解中指定了销毁方法`，如果有，注册bean的销毁方法（非执行）。
         4. `doCreateBean(name)`结束，bean对象创建完成。
      3. `createBean`方法完成，bean对象创建完成。将其添加到单实例bean的列表中。
   3. 循环中的bean创建完成
4. 继续循环迭代，直到数组`beanNames`中的所有bean创建完毕。

13. ### 接着来到`refresh`方法的39行 finishRefresh();

    最后一步：完成容器的初始化工作，发布相应的事件。

    ```java
    protected void finishRefresh() {
    		// 清除上下文级资源缓存
    		clearResourceCaches();
    
    		//初始化和生命周期有关的后置处理器。
    		initLifecycleProcessor();
    
    		getLifecycleProcessor().onRefresh();
    
    		//发布最终事件。
    		publishEvent(new ContextRefreshedEvent(this));
    
    
    		if (!NativeDetector.inNativeImage()) {
    			LiveBeansView.registerApplicationContext(this);
    		}
    	}
    ```

14. ### 至此，Spring的IOC容器创建完毕。

## 总结

1. ### Spirng容器启动的时候，首先保存所有已经注册的bean的定义信息

   1. 通过XML，或者注解定义bean的信息。

2. ### 容器会在合适的时机创建bean。

   1. 单例模式，会在启动容器的时候，统一循环创建。
   2. 非单例模式时，在用到bean的时候，利用getBean创建，并保存在容器中

3. ### 每一个Bean创建完成都会使用各种后置处理器来增强Bean的功能。

   - 比如`AutowiredAnnotationBeanPostProcessor`Bean用来处理自动注入的功能。
   - 比如`AnnotationAwareAspectJAutoProxyCreator`Bean用来AOP切面功能。
   - ……

4. ### 事件驱动模型

   1. `ApplicationListener` : 事件监听
   2. `ApplicationEventMuiticaster`: 事件派发