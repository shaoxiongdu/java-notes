##  容器创建过程

### Spring容器的refresh方法

```java
public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			StartupStep contextRefresh = this.applicationStartup.start("spring.context.refresh");

			// 1. 进行创建Bean工厂的前置处理
			prepareRefresh();

			// 2. 创建容器，并且进行一些必要的设置工作
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			// 3. 对创建好的容器进行准备工作
			prepareBeanFactory(beanFactory);

			try {
				// 4. Bean工厂初始化之后的后置处理器 空方法 主要用于子类实现之后注册一些必备的组件
				postProcessBeanFactory(beanFactory);

				StartupStep beanPostProcess = this.applicationStartup.start("spring.context.beans.post-process");
				// 5. 执行Bean工厂的后置处理器 （按照优先级）
				invokeBeanFactoryPostProcessors(beanFactory);

				// 6. 组件注册的后置处理器
				registerBeanPostProcessors(beanFactory);
				beanPostProcess.end();

				// 7. 初始化MessageSource工作 包括：国际化，消息绑定，消息解析等。
				initMessageSource();

				// 8. 初始化事件派发器
				initApplicationEventMulticaster();

				// 9. 主要初始化特定上下文子类中的其他特殊 bean。
				onRefresh();

				// 10.将所有的监听器注册到容器中
				registerListeners();

				// 11.实例化所有的单实例bean
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

1. #### prepareRefresh()Bean工厂创建前的前置处理

   ```java
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
   
           this.initPropertySources(); //初始化一些属性设置 空方法 子类实现之后 自定义个性化属性
           this.getEnvironment().validateRequiredProperties(); //对自定义的属性进行合法校验
           if (this.earlyApplicationListeners == null) {
               this.earlyApplicationListeners = new LinkedHashSet(this.applicationListeners);
           } else {
               this.applicationListeners.clear();
               this.applicationListeners.addAll(this.earlyApplicationListeners);
           }
   
           this.earlyApplicationEvents = new LinkedHashSet(); //保存容器中的事件，在合适的时机进行派发。
       }
   ```

2. ####   ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory(); 获取Bean工厂

3. ####  prepareBeanFactory(beanFactory);  对bean工厂进行预准备工作

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
   		......
   
   		beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);//
   		//注册可以解析的自动装配 XXXAware接口相关  包括BeanFactory，ResourceLoader，ResourceLoader，ApplicationContext等。
   		......
   
   		// 添加后置处理器
   		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));
   
   		if (!NativeDetector.inNativeImage() && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
   			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
   			// Set a temporary ClassLoader for type matching.
   			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
   		}
   
   		// 注册环境变量等信息
   		if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
   			beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
   		}
       	//注册系统属性
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

4. postProcessBeanFactory  容器初始化之后的后置处理器 空方法 主要用于子类实现之后注册一些必备的组件

5. invokeBeanFactoryPostProcessors  Bean工厂的后置处理器 bean工厂标准初始化之后进行执行

   ```java
   public static void invokeBeanFactoryPostProcessors(
   			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
   
   		Set<String> processedBeans = new HashSet<String>();
   
   		if (beanFactory instanceof BeanDefinitionRegistry) {
   			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
   			List<BeanFactoryPostProcessor> regularPostProcessors = new LinkedList<BeanFactoryPostProcessor>();
   			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new LinkedList<BeanDefinitionRegistryPostProcessor>();
   
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
   			}
   
   			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<BeanDefinitionRegistryPostProcessor>();
   
   			//首先，调用实现 PriorityOrdered接口的bean注册前的前置处理器
   			String[] postProcessorNames =
   					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
   			for (String ppName : postProcessorNames) {
   				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
   					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
   					processedBeans.add(ppName);
   				}
   			}
                //对其进行排序
   			sortPostProcessors(currentRegistryProcessors, beanFactory);
               // 按照排好的顺序执行bean注册前的前置处理器
   			registryProcessors.addAll(currentRegistryProcessors);
   			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
   			currentRegistryProcessors.clear();
   
   			//然后，调用实现 Ordered接口的bean注册前的前置处理器
   			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
   			for (String ppName : postProcessorNames) {
   				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
   					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
   					processedBeans.add(ppName);
   				}
   			}
               //对其进行排序
   			sortPostProcessors(currentRegistryProcessors, beanFactory);
               // 按照排好的顺序执行bean的前置处理器
   			registryProcessors.addAll(currentRegistryProcessors);
   			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
   			currentRegistryProcessors.clear();
   
   			// 执行其他的bean注册前的前置处理器
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
   				sortPostProcessors(currentRegistryProcessors, beanFactory);
   				registryProcessors.addAll(currentRegistryProcessors);
   				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
   				currentRegistryProcessors.clear();
   			}
   
   			// 现在，调用到目前为止处理的所有处理器的 postProcessBeanFactory 回调。
   			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
   			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
   		}
   
   		else {
   			
   			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
   		}
   
   		//获取所有的BeanFactoryPostProcessor 
   		String[] postProcessorNames =
   				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
   
   		//将实现不同接口的放入不同的集合中
   		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<BeanFactoryPostProcessor>();
   		List<String> orderedPostProcessorNames = new ArrayList<String>();
   		List<String> nonOrderedPostProcessorNames = new ArrayList<String>();
   		for (String ppName : postProcessorNames) {
   			if (processedBeans.contains(ppName)) {
   				// skip - already processed in first phase above
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
   
   		// 首先执行实现了priorityOrdered接口的beanFactoryProcessor
   		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
   		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);
   
   		//  然后执行实现Ordere接口d的beanFactoryProcessor
   		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<BeanFactoryPostProcessor>();
   		for (String postProcessorName : orderedPostProcessorNames) {
   			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
   		}
   		sortPostProcessors(orderedPostProcessors, beanFactory);
   		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);
   
   		// 最后执行其他的beanFactoryProcessor
   		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<BeanFactoryPostProcessor>();
   		for (String postProcessorName : nonOrderedPostProcessorNames) {
   			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
   		}
   		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);
   
   		//清空缓存
   		beanFactory.clearMetadataCache();
   	}
   ```

6. 组件注册的后置处理器  registerBeanPostProcessors(beanFactory);  拦截Bean的创建过程

      ```java
      public static void registerBeanPostProcessors(
      			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
      
          	//获取容器中所有的postProcessorNames
      		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
      
      		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
          	// 加入自己的后置处理器
      		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));
      
      		//分离实现 PriorityOrdered接口的Bean 的 BeanPostProcessor接口的Bean
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
      
      		// 首先，注册实现 PriorityOrdered接口 的Bean和实现 BeanPostProcessors接口的Bean。
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

7. initMessageSource 初始化MessageSource 包括消息绑定，消息解析等

      ```java
      protected void initMessageSource() {
      		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
          
          	//如果容器中有messageSource组件 则赋值给自己的属性
      		if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
      			this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
      			if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
      				HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
      				if (hms.getParentMessageSource() == null) {
      				
      					hms.setParentMessageSource(getInternalParentMessageSource());
      				}
      			}
      			if (logger.isDebugEnabled()) {
      				logger.debug("Using MessageSource [" + this.messageSource + "]");
      			}
      		}
      		else {
      			// 如果没有 创建一个默认的DelegatingMessageSource
      			DelegatingMessageSource dms = new DelegatingMessageSource();
      			dms.setParentMessageSource(getInternalParentMessageSource());
      			this.messageSource = dms;
      			beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
      			if (logger.isDebugEnabled()) {
      				logger.debug("Unable to locate MessageSource with name '" + MESSAGE_SOURCE_BEAN_NAME +
      						"': using default [" + this.messageSource + "]");
      			}
      		}
      	}
      ```

8. initApplicationEventMulticaster 初始化事件派发器

      ```java
      protected void initApplicationEventMulticaster() {
      		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
          
          	//从容器中获取事件派发器
      		if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
      			this.applicationEventMulticaster =
      					beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
      			if (logger.isDebugEnabled()) {
      				logger.debug("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
      			}
      		}
      		else {
                   //如果容器中不存在，则创建默认的SimpleApplicationEventMulticaster
      			this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
                  //注册到容器中
      			beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
      			if (logger.isDebugEnabled()) {
      				logger.debug("Unable to locate ApplicationEventMulticaster with name '" +
      						APPLICATION_EVENT_MULTICASTER_BEAN_NAME +
      						"': using default [" + this.applicationEventMulticaster + "]");
      			}
      		}
      	}
      ```

9. onRefresh 初始化剩下的bean 空方法，留给子类实现。

10. registerListeners 注册监听器到容器中

      ```java
      protected void registerListeners() {
      		// 将所有的监听器添加到事件派发器重
      		for (ApplicationListener<?> listener : getApplicationListeners()) {
      			getApplicationEventMulticaster().addApplicationListener(listener);
      		}
      
      	
      		String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
      		for (String listenerBeanName : listenerBeanNames) {
      			getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
      		}
      
      		Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
      		this.earlyApplicationEvents = null;
      		if (earlyEventsToProcess != null) {
      			for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
                      //派发事件
      				getApplicationEventMulticaster().multicastEvent(earlyEvent);
      			}
      		}
      	}
      ```

11. finishBeanFactoryInitialization 实例化所有的单实例bean

       ```java
       @Override
       	public void preInstantiateSingletons() throws BeansException {
       		if (this.logger.isDebugEnabled()) {
       			this.logger.debug("Pre-instantiating singletons in " + this);
       		}
       		//1. 获取容器中所有的bean的名字
       		List<String> beanNames = new ArrayList<String>(this.beanDefinitionNames);
       
       		// 2. 循环将bean进行初始化
       		for (String beanName : beanNames) {
       			RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
                   //当bean不是抽象类，是单例，不是懒加载的时候，进行初始化
       			if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
                       
                       //开始实例化
       				if (isFactoryBean(beanName)) {
       					final FactoryBean<?> factory = (FactoryBean<?>) getBean(FACTORY_BEAN_PREFIX + beanName);
       					boolean isEagerInit;
                           
                            //如果当前bean是工厂bean 则调用ban自己重写的getObject方法创建组件 
       					if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
       						isEagerInit = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
       							@Override
       							public Boolean run() {
       								return ((SmartFactoryBean<?>) factory).isEagerInit();
       							}
       						}, getAccessControlContext());
       					}
       					else {
       						isEagerInit = (factory instanceof SmartFactoryBean &&
       								((SmartFactoryBean<?>) factory).isEagerInit());
       					}
       					if (isEagerInit) {
       						getBean(beanName);
       					}
       				}
       				else {
                            //如果不是工厂bean 则使用此方法实例化bean
       					getBean(beanName);
       				}
       			}
       		}
       
       		for (String beanName : beanNames) {
       			Object singletonInstance = getSingleton(beanName);
       			if (singletonInstance instanceof SmartInitializingSingleton) {
       				final SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
       				if (System.getSecurityManager() != null) {
       					AccessController.doPrivileged(new PrivilegedAction<Object>() {
       						@Override
       						public Object run() {
       							smartSingleton.afterSingletonsInstantiated();
       							return null;
       						}
       					}, getAccessControlContext());
       				}
       				else {
       					smartSingleton.afterSingletonsInstantiated();
       				}
       			}
       		}
       	}
       ```

    上边代码的getBean方法

       ```java
       protected <T> T doGetBean(
       			final String name, final Class<T> requiredType, final Object[] args, boolean typeCheckOnly)
       			throws BeansException {
       
       		final String beanName = transformedBeanName(name);
       		Object bean;
       
       		// 先从缓存中获取
       		Object sharedInstance = getSingleton(beanName);
               //如果缓存中有 
       		if (sharedInstance != null && args == null) {
       			if (logger.isDebugEnabled()) {
       				if (isSingletonCurrentlyInCreation(beanName)) {
       					logger.debug("Returning eagerly cached instance of singleton bean '" + beanName +
       							"' that is not fully initialized yet - a consequence of a circular reference");
       				}
       				else {
       					logger.debug("Returning cached instance of singleton bean '" + beanName + "'");
       				}
       			}
                    //将缓存中的bean保存起来
       			bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
       		}
       		//如果缓存中没有 则创建bean
       		else {
       	
       			if (isPrototypeCurrentlyInCreation(beanName)) {
       				throw new BeanCurrentlyInCreationException(beanName);
       			}
       
       			//判断是否有父工厂
       			BeanFactory parentBeanFactory = getParentBeanFactory();
       			if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
       				// Not found -> check parent.
       				String nameToLookup = originalBeanName(name);
       				if (args != null) {
       					// Delegation to parent with explicit args.
       					return (T) parentBeanFactory.getBean(nameToLookup, args);
       				}
       				else {
       					// No args -> delegate to standard getBean method.
       					return parentBeanFactory.getBean(nameToLookup, requiredType);
       				}
       			}
       
       			if (!typeCheckOnly) {
                        //标记当前bean背创建  多线程下的安全问题
       				markBeanAsCreated(beanName);
       			}
       
       			try {
                        //获取bean的定义信息
       				final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
       				checkMergedBeanDefinition(mbd, beanName, args);
       
                       //获取当前bean的其他依赖bean
       				String[] dependsOn = mbd.getDependsOn();
       				if (dependsOn != null) {
       					for (String dep : dependsOn) {
       						if (isDependent(beanName, dep)) {
       							throw new BeanCreationException(mbd.getResourceDescription(), beanName,
       									"Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
       						}
       						registerDependentBean(dep, beanName);
                                //循环获取依赖的bean
       						getBean(dep);
       					}
       				}
       
       				// 如果是单实例bean
       				if (mbd.isSingleton()) {
       					sharedInstance = getSingleton(beanName, new ObjectFactory<Object>() {
       						@Override
       						public Object getObject() throws BeansException {
       							try {
                                       //实例化bean并返回
       								return createBean(beanName, mbd, args);
       							}
       							catch (BeansException ex) {
       							
       								destroySingleton(beanName);
       								throw ex;
       							}
       						}
       					});
       					bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
       				}
       
       				else if (mbd.isPrototype()) {
       					// It's a prototype -> create a new instance.
       					Object prototypeInstance = null;
       					try {
       						beforePrototypeCreation(beanName);
       						prototypeInstance = createBean(beanName, mbd, args);
       					}
       					finally {
       						afterPrototypeCreation(beanName);
       					}
       					bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
       				}
       
       				else {
       					String scopeName = mbd.getScope();
       					final Scope scope = this.scopes.get(scopeName);
       					if (scope == null) {
       						throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
       					}
       					try {
       						Object scopedInstance = scope.get(beanName, new ObjectFactory<Object>() {
       							@Override
       							public Object getObject() throws BeansException {
       								beforePrototypeCreation(beanName);
       								try {
       									return createBean(beanName, mbd, args);
       								}
       								finally {
       									afterPrototypeCreation(beanName);
       								}
       							}
       						});
       						bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
       					}
       					catch (IllegalStateException ex) {
       						throw new BeanCreationException(beanName,
       								"Scope '" + scopeName + "' is not active for the current thread; consider " +
       								"defining a scoped proxy for this bean if you intend to refer to it from a singleton",
       								ex);
       					}
       				}
       			}
       			catch (BeansException ex) {
       				cleanupAfterBeanCreationFailure(beanName);
       				throw ex;
       			}
       		}
       
       		if (requiredType != null && bean != null && !requiredType.isInstance(bean)) {
       			try {
       				return getTypeConverter().convertIfNecessary(bean, requiredType);
       			}
       			catch (TypeMismatchException ex) {
       				if (logger.isDebugEnabled()) {
       					logger.debug("Failed to convert bean '" + name + "' to required type '" +
       							ClassUtils.getQualifiedName(requiredType) + "'", ex);
       				}
       				throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
       			}
       		}
       		return (T) bean;
       	}
       ```


12. finishRefresh 刷新容器  发布相应的事件

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