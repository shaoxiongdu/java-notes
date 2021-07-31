## 1. 基本概念

- ### 程序（Program）

  程序是为了完成特定任务，用某种语言编写的一组指令的集合，即指`一段静态代码块`，静态对象。

- ### 进程(process)

    - 程序是静态的，进程为动态的。
    - 进程是程序的一次执行过程，或者是`正在执行的一个程序`，是一个动态的过程。有它自生 产生，存在，消亡的过程。

    - 进程作为资源分配的单位，每个进程启动时系统都会为他分配不同的内存区域。

- ### 线程（Thread）
    - 进程可以进一步细化为线程，是一个线程内部的一条执行路径。
    - 线程作为调度和执行的单位，每个线程都有`独立的运行栈和` `程序计数器`（PC寄存器）

- ### 单核CPU/多核CPU

    - 单核CPU，同一个时间单元内，只能执行一个线程。多核才可以更好的发挥多线程的效率。

- ### 并行与并发

    - 并发：一个CPU同时执行多个线程（时间片论转），比如： 秒杀 `单车道`
    - 并行：多个CPU同时执行多个人物  `多车道`

- ### 使用多线程的优点

    - 提高应用程序的响应。增强用户体验
    - 提高计算机CPU的利用率
    - 改善程序结构，将复杂的进程分为多个线程。

## 2. 线程的创建和使用

- ### 创建

  ```java
  /**
   * @author: 【写Bug的小杜 <email@shaoxiongdu.cn>
   * @date: 2021/07/20
   * @description: 写一个遍历100以内偶数的线程
   */
  
  class MyThread extends Thread{
  
      @Override
      public void run() {
  
          for (int i = 0; i < 100; i++) {
  
              if(i % 2 == 0){
                  System.out.println( Thread.currentThread().getName() +  "   --- > i = " + i);
              }
  
          }
  
      }
  }
  ```

- ### 使用

  ```java
  public static void main(String[] args) {
  
          MyThread myThread = new MyThread();
          myThread.start();
  
          MyThread myThread1 = new MyThread();
          myThread1.start();
  
      }
  ```

- ### Thread的常用方法

    - `currentThread `返回当前线程
    - `set/getName `设置获取当前线程名字
    - `static sleep(long time)` ： 阻塞当前线程，但不释放锁
    - `static yield ` 回到就绪状态，释放锁，接下来也可能会再次抢到执行权。
    - `join` 实例方法，将当前线程阻塞，等实例结束之后，在执行。
        - 比如： 在线程A中 ，调用线程B的jion方法，那么A会阻塞，等B执行完，才继续执行。
    - `isAlive`: 判断是否存活
    - `get/setPriority`: 获取/设置优先级

- ### 线程的优先级

    - 共分为10级

  ```java
    /**
         * 最低
         */
        public final static int MIN_PRIORITY = 1;
    
       /**
         * 默认
         */
        public final static int NORM_PRIORITY = 5;
    
        /**
         *最高
         */
        public final static int MAX_PRIORITY = 10;
  ```

    - 高优先级会抢占低优先级的资源（非绝对高优先级一定会先执行）

- ### 创建线程的两种方式比较

    - #### 继承Thread

        - 继承Thread之后，不可以继承其他类，因为Java是单继承的。
        - 多个线程数据共享需通过static实现。

    - #### 实现Runable(优先选择)

        - 实现该接口之后，还可以实现其他接口
        - 可以共享数据，放入实现类即可，给多个线程传递同一个实现类就可以实现多个线程数据共享

## 3. 线程的生命周期

- ### 五种状态

    - #### 新建

      当一个Thread类或其子类的对象被创建，则处于新建状态。

    - #### 就绪

      新建状态的线程调用start方法之后，处于就绪状态，此时将进入线程队列等待CPU时间片，此时可以运行，但未获得CPU资源

    - #### 运行

      就绪状态的线程获得CPU资源，进入运行状态。

    - #### 阻塞

      被人为挂起或者执行输入输出时，让出CPU资源，临时终止自己的执行，进入阻塞状态。

    - #### 死亡

      线程完成了他的全部工作或者被提前强制终止或者抛出异常。

- ### 线程的生命周期

  ![image-20210721135149797](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210721135149797.png)

## 4. 线程的同步

- ### 问题描述

  当某个线程在操作共享数据的时候，尚未完成操作，其他线程进入操作数据，就会产生线程安全问题。

- ### 如何解决

  ​	当一个线程操作共享数据的时候，其他线程不能参与进来，直到当前线程结束。

- ### Java中如何做

  ​	通过同步机制，解决线程安全问题。

    - #### 方式1 同步代码块

      ```java
      //保证同步监视器必须是多线程共享的对象
      synchronized(同步监视器){
        //操作共享数据的代码
      }
      ```

    - #### 方式2 同步方法

      ```java
      public synchronized void setBlance(){
          //
      }
      ```

        - 同步方法中的监视器
            - 成员方法`锁为this`。
            - 静态方法 `锁为类`。

    - #### 方式3 Lock锁

      > JDK5.0新增

      ```java
      class Window3 implements Runnable{
      
          private int ticket = 100;
      
          //实例化一个锁
          private Lock lock = new ReentrantLock();
      
          @Override
          public void run() {
      
              while (ticket > 0){
      
                  try {
                      //加锁
                      lock.lock();
                      if(ticket > 0){
                          System.out.println(Thread.currentThread().getName() + "售票，票号为:" + ticket);
                          ticket--;
                      }
                  } finally {
                      //释放锁
                      lock.unlock();
                  }
              }
      
          }
      }
      ```

- ### synchronized和lock的异同

    - 同

      都可以解决线程安全问题

    - 异
        - lock手动加锁，解锁，更加灵活。
        - synchronized 自动加锁释放锁。

- ### 线程安全之单例模式-懒汉式

  ```java
  
  //单例
  class Bank{
      //构造私有 防止外部创建对象
      private Bank(){}
      
      private static Bank instance = null;
  
      //懒汉式 用到时在创建
      public static Bank getInstance(){
  
          if(instance == null){
              synchronized (Bank.class) {
                  if(instance == null){
                      instance = new Bank();
                  }
              }
          }
          return instance;
      }
  
  }
  ```

- ### 死锁

    - 不同的线程分别占用对方的锁不放弃，都在等待对方放弃自己需要的锁，形成线程的死锁。

      ```java
      Object o1 = new Object();
              Object o2 = new Object();
      
              new Thread( ()-> {
                  //先锁o1
                  synchronized (o1){
                      System.out.println(Thread.currentThread().getName() + "锁住了o1");
                      try {
                          Thread.sleep(100);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                      System.out.println(Thread.currentThread().getName() + "等待锁o2");
                      synchronized (o2){
                      }
                  }
      
      
              } ).start();
      
              new Thread( ()-> {
                  //先锁o2
                  synchronized (o2){
                      System.out.println(Thread.currentThread().getName() + "锁住了o2");
                      // 锁o1
                      System.out.println(Thread.currentThread().getName() + "等待锁o1");
                      synchronized (o1){
                      }
                  }
      
              } ).start();
      ```

      造成了死锁现象。程序无法正常结束，也不会报异常。

      ![image-20210721154559637](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210721154559637.png)



## 5. 线程的通信

- ### 涉及线程通信的三个方法

    - `wait `当前线程进入阻塞状态,释放锁
    - `notify` 唤醒被wait的线程，如果有多个，唤醒优先级最高的
    - `notifyAll`: 唤醒全部被wait的线程
    - ==以上三个方法必须出现在同步代码块或者同步方法中==
    - ==调用者必须是锁== 因为该方法会释放锁，必须用有锁的对象在有锁的地方调用

## 6. JDK5.0新增的线程创建方式

- ### 实现Callable接口

    - call可以有泛型返回值

    - 可以抛出异常

    - 需要借助FutureTask类，比如获取返回结果

      ```java
      // 创建一个实现Callable的实现类
      class MyCallable implements Callable{
          
          //实现call方法
          @Override
          public Object call() throws Exception {
              System.out.println(Thread.currentThread().getName() + "is run !");
              return null;
          }
      }
      
      public static void main(String[] args) {
      
          //创建Callable接口实现类的对象
          Callable c = new MyCallable();
          //将此接口实现类的对象传入到FutureTask中
           FutureTask futureTask = new FutureTask<>(c);
      
           new Thread(futureTask).start();
              
           //futureTask的get方法可以获取call的返回值
      
          }
      ```

- ### 使用线程池

    - 好处

        - 提高响应速度 （减少了创建线程的时间）
        - 降低资源消耗（重复使用线程池中的对象）
        - 便于线程管理

    - ```java
    static class Something implements Runnable{
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " : some is run!");
            }
        }
    
        public static void main(String[] args) {
    
            //通过Executors工具类返回一个可以重复使用的指定数量的线程池
            //ExecutorService是一个接口 
            ExecutorService service = Executors.newFixedThreadPool(100);
    
            //调用execute方法 传递Runable接口的实现类 自动start
            service.execute(new Something());
    
            //通过getClass 可以知道实际创建的是ThreadPoolExecutor的对象  ThreadPoolExecutor实现了ExecutorService接口
            System.out.println(service.getClass()); //ThreadPoolExecutor
            
            //将其强转为ThreadPoolExecutor
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) service;
            int corePoolSize = threadPoolExecutor.getCorePoolSize();
            System.out.println("corePoolSize = " + corePoolSize);
    
            //关闭连接池
            service.shutdown();
    
        }
    ```

    - 