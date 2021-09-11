# 手撕ArrayList源码

> 文章首发于GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes)

## ArrayList 简介

ArrayList 是一个数组列表。它的主要底层实现是`Object`数组，但与 Java 中的数组相比，它的**容量能动态变化**，可看作是一个动态数组结构。特别注意的是，当我们装载的是基本类型的数据 int，long，boolean，short，byte… 的时候，我们只能存储他们对应的包装类。

### ArrayList 特点

- 元素有序，可重复
- 增删元素的速度慢，每次增加删除元素，都需要更改数组长度、拷贝元素及移动元素位置，故增删速度相对会较慢。
- 查询元素的速度快，由于底层数据结构是基于 Object 数组实现的。而数组在内存中是一块连续空间，因此可以根据`地址 + 索引`的方式快速获取对应位置上的元素。
- 线程不安全

- 实现 Serializable 标记性接口。ArrayList 实现该标记性接口可提供为类提供序列化和反序列化功能，这意味着 ArrayList 支持序列化，能通过序列化去传输。
- 实现 Cloneable 标记性接口。类若要使用`clone`方法必须要实现`Cloneable`接口，提供了克隆功能。
- 实现 RandomAccess 标记性接口。为 ArrayList 提供了随机访问功能，也就是通过下标获取元素对象的功能。
- 实现 List 接口，是 List 的实现类之一。
- 实现 Iterable 接口，可以使用`for-each`迭代。

>  首先看一下集合的继承结构图

![image-20210901114846291](https://gitee.com/ShaoxiongDu/imageBed/raw/master/image-20210901114846291.png)

> 源码分析

## ArrayList 相关成员变量

在下文分析中会调用到相关的成员变量，为方便分析核心源码，故提前将其说明。

```java
   /**
     * 默认初始化容量
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * 用于空实例的共享空数组实例，是为了优化创建ArrayList空实例时产生不必要的空数组，
     * 使得所有ArrayList空实例都指向同一个空数组。
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};

    /**
     * 用于默认大小的空实例的共享空数组实例。
     * 我们将其与EMPTY_ELEMENTDATA分开来，以了解添加第一个元素时要扩容多少。
     * 是为了确保无参构成函数创建的实例在添加第一个元素时，最小的容量是默认大小10。
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     * 集合真正存储数据的容器，存储ArrayList元素的数组缓冲区，ArrayList的容量是这个数组缓冲区的长度。
     * 当第一个元素被添加的时候，elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA 将
     * 被扩展到 DEFAULT_CAPACITY（默认容量）
     */
    transient Object[] elementData;

    /**
     * ArrayList的大小（它包含的元素数）
     */
    private int size;
```

## ArrayList 构造方法

ArrayList 一共有三个构造方法：无参构造方法，指定初始容量值的构造方法，包含指定集合元素列表的构造方法并按照集合的迭代器返回它们的顺序。

### 默认的空参构造方法

在网上看过一些博文在介绍 ArrayList 空参构造方法时，通常会这么写道，“当调用该空参构造函数时，会创建一个容量为 10 的数组”。为了验证这种说法，我们可以看看基于 `jdk 1.8(201)` 的 ArrayList 源码：

```java
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
```

我们可以看到该空参方法，仅仅只是将`DEFAULTCAPACITY_EMPTY_ELEMENTDATA`这个空数组赋值给`elementData`，并没有指定初始容量为 10 的一个动作。那么为何会产生这种说法呢？原因是**在 jdk 1.2 ~ jdk 1.6 中，ArrayList 的确是会通过空参构造方法生成一个指定底层数据结构容量为 10 的空数组**。

而在 jdk 1.7 后，ArrayList 的空参构造方法为了避免无用内存占用，仅仅只是创建了一个底层数据结构长度为 0 的空数组。**只有在初次添加元素时才将容量扩容为 10。**具体扩容过程在下文会有更详细的分析。

### 生成具有指定初始容量值的方法

在知道要添加多少元素到 ArrayList 的时候要优先使用此构造方法，可以节省 ArrayList 因扩容时所耗费的资源。

```java
   /**
     * 构造一个具有指定初始容量的空列表。
     *
     * @param  initialCapacity 指定的初始容量
     * @throws 如果指定的初始容量为负，则抛出IllegalArgumentException
     */
    public ArrayList(int initialCapacity) {
        // 判断指定的初始容量initialCapacity是否大于0
        if (initialCapacity > 0) {
            // 生成指定容量的数组，将其赋值给 elementData
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            // 如果指定的容量为 0，将 EMPTY_ELEMENTDATA地址赋值给 elementData，相当于创建空数组
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }
```

### 构造一个包含指定集合元素的列表，按照集合的迭代器返回它们的顺序

```java
    /**
     * 构造一个包含指定集合的元素的列表，其顺序由集合迭代器返回。
     *
     * @param c 将其元素放入此列表的集合
     * @throws NullPointerException 如果指定的集合为null
     */
    public ArrayList(Collection<? extends E> c) {
        // 将构造方法中的集合参数转成数组
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            // c.toArray可能（不正确）不返回Object []
            // jdk bug（Arrays内部实现的ArrayList的toArray()方法的行为与规范不一致） 15年修复；<https://bugs.openjdk.java.net/browse/JDK-6260652>
            // 再次判断
            if (elementData.getClass() != Object[].class)
                // 数组的创建和拷贝
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // 把空数组的地址赋值给集合存元素的数组
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }
```

在什么情况下 c.toArray() 可能（不正确）不返回Object []？见下程序示例。

```java
    public static void main(String[] args) {

        List list = new ArrayList(Arrays.asList("small", "min"));
        // class java.util.ArrayList
        System.out.println(list.getClass());
        Object[] object = list.toArray();
        // class [Ljava.lang.Object;
        System.out.println(object.getClass());

        List asList = Arrays.asList("small", "min");
        // class java.util.Arrays$ArrayList
        System.out.println(asList.getClass());
        Object[] objects = asList.toArray();
        // class [Ljava.lang.String;
        System.out.println(objects.getClass());
    }
```

通过运行程序结果，我们可以得知`java.util.ArrayList.toArray()`方法会返回`Object[]`，没有问题。而`java.util.Arrays`的私有内部类ArrayList的`toArray()`方法可能不返回 `Object[]`

为什么会有这种情况呢，我们看 ArrayList 的 `toArray()` 方法源码：

```java
public Object[] toArray() {
    return Arrays.copyOf(elementData, size);
}
```

使用了 `Arrays.copyOf()` 方法：

```java
public static <T> T[] copyOf(T[] original, int newLength) {
    // original.getClass() 是 class [Ljava.lang.Object
    return (T[]) copyOf(original, newLength, original.getClass());
}
```

`copyOf()`的具体实现：

```java
public static <T,U> T[] copyOf(U[] original,
          int newLength, Class<? extends T[]> newType) {
    @SuppressWarnings("unchecked")
    /**
     * 如果newType是Object[] copy 数组类型就是 Object，否则就是 newType 类型。
     * 不管三元运算符结果如何，都会创建一个新的数组。
     * 新数组的长度一定是和集合的size一样
     */
    T[] copy = ((Object)newType == (Object)Object[].class)
        ? (T[]) new Object[newLength]
        : (T[]) Array.newInstance(newType.getComponentType(), newLength);
    // 数组的拷贝
    System.arraycopy(original, 0, copy, 0,
                     Math.min(original.length, newLength));
    // 返回新数组
    return copy;
}
```

我们知道 ArrayList 中`elementData`就是 `Object[]` 类型，所以 ArrayList 的`toArray()`方法必然会返回 `Object[]`。

我们再看一下`java.util.Arrays`的内部 ArrayList 源码（截取的部分源码）：

```java
private static class ArrayList<E> extends AbstractList<E>
        implements RandomAccess, java.io.Serializable
    {
    	// 存储元素的数组
        private final E[] a;

        ArrayList(E[] array) {
            // 直接将接受的数组赋值给 a
            // Objects.requireNonNull(T object) 方法作用，如果传进来的对象不为null，则返回改对象
            a = Objects.requireNonNull(array);
        }

        @Override
        public Object[] toArray() {
            return a.clone();
        }
}
```

这是 `Arrays.asList()`方法源码

```java
public static <T> List<T> asList(T... a) {
    return new ArrayList<>(a);
}
```

不难看出来 `java.util.Arrays` 的内部 ArrayList 的`toArray()`方法，是构造方法接收什么类型的数组，就返回什么类型的数组。

所以，在这种情况下， c.toArray() 可能（不正确）不返回Object []。

## **添加方法**

### add(E e) 将元素添加到列表末尾方法

```java
	/**
     * 将指定的元素追加到此列表的末尾。
     *
     * @param e 要添加到此列表的元素
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        // 添加元素之前，先调用 ensureCapacityInternal 对内部容量进行校验
        // 因为要添加一个元素，故方法里是 size + 1
        ensureCapacityInternal(size + 1);
        // 元素添加进去实质就是给最后x一个数组元素赋值
        elementData[size++] = e;
        return true;
    }
```

### ensureCapacityInternal() 方法

```java
	private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }
	// 得到最小扩容量
    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        // 判断集合存数据的数组是否等于空容量的数组，实际就是看数组有没有存在数据
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            // 通过最小容量和默认容量求出最大值
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        return minCapacity;
    }
```

### ensureExplicitCapacity() 方法

```java
    private void ensureExplicitCapacity(int minCapacity) {
        // 实际修改集合次数++ (在扩容的过程中没用，主要是用于迭代器中)
        modCount++;

        // 预防溢出
        if (minCapacity - elementData.length > 0)
            // 调用grow方法进行扩容
            grow(minCapacity);
    }
```

针对上面的方法，现作如下概述：

- 当我们添加 1 个元素到 ArrayList 中时，如若 ArrayList 为没有存放任何元素的空集合，那么在执行`ensureCapacityInternal()`中 `calculateCapacity()` 方法过后，minCapacity 会变为 **10**。此时，`minCapacity - elementData.length > 0`成立，会进入到 `grow(minCapacity)` 方法。
- 当 add 添加第 2 个元素时，minCapacity 为 2，此时 `elementData.length` （容量）在添加第一个元素后扩容成 10 了。此时，`minCapacity - elementData.length > 0` 不成立，所以不会进入`grow(minCapacity)` 方法。
- 倘若一直添加元素，直至添加第 11 个元素，`minCapacity - elementData.length > 0`成立（即 11 - 10 > 0），进入 grow() 方法进行扩容。

### grow() 扩容方法

```java
   /**
     * 要分配的数组的最大 size。
     * 一些虚拟机在数组中保留一些头字。
     * 尝试分配更大的阵列可能会导致
     * OutOfMemoryError：请求的阵列大小超出了 VM 限制
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	/**
     * 增加容量以确保其至少可以容纳
     *
     * @param minCapacity 最小容量参数指定的元素数。
     */
    private void grow(int minCapacity) {
        // 记录原数组的实际长度
        int oldCapacity = elementData.length;
        // 核心扩容算法，扩容后的容量为原容量的 1.5 倍。
        // oldCapacity >> 1 移位运算（更高效），结果上等于 oldCapacity / 2 。
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        // 检查扩容后的容量是否大于最小需要容量，若还是小于最小需要的容量，那么就把最小需要容量当作数组的新容量
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        // 再检查新容量是否超出了ArrayList所定义的最大容量，
        // 若超出了，则调用 hugeCapacity() 来比较 minCapacity 和 MAX_ARRAY_SIZE
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    /**
     * 最大容量返回 Integer.MAX_VALUE
     */
	private static int hugeCapacity(int minCapacity) {
        // 溢出，抛出异常
        if (minCapacity < 0)
            throw new OutOfMemoryError();
        // 如果 minCapacity 大于 MAX_ARRAY_SIZE，则新容量则为 Interger.MAX_VALUE，
        // 否则，新容量大小则为 MAX_ARRAY_SIZE。
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
```

### **add(int index, E element) 在指定索引处添加元素方法**

```java
/**
     * 将指定的元素插入此列表中的指定位置。
     * 将当前位于该位置的元素（如果有的话）和任何后续元素右移（将其索引添加一个）,最后size +1。
     *
     * @param index 指定元素要插入的索引
     * @param element 要插入的元素
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        // 范围检查
        rangeCheckForAdd(index);

        ensureCapacityInternal(size + 1);  // Increments modCount!!
        // arraycopy()方法实现数组自己复制自己
        // elementData:源数组;index:源数组中的起始位置;
        // elementData：目标数组；index + 1：目标数组中的起始位置；
        // size - index：要复制的数组元素的数量；
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        elementData[index] = element;
        size++;
    }

	/**
     * add和addAll使用的rangeCheck版本。
     */
    private void rangeCheckForAdd(int index) {
        // 如若超出范围则抛出异常
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
```

**这里补充一点比较重要，但是容易被忽视掉 的知识点：**

- java 中的 `length` 属性是针对数组说的，比如说你声明了一个数组，想知道这个数组的长度则用到了 length 这个属性。
- java 中的 `length()` 方法是针对字符串说的，如果想看这个字符串的长度则用到 `length()` 这个方法。
- java 中的 `size()` 方法是针对泛型集合说的，如果想看这个泛型有多少个元素，就调用此方法来查看！

## ensureCapacity方法

ArrayList 源码中有一个 `ensureCapacity` 方法不知道大家注意到没有，这个方法 ArrayList 内部没有被调用过，所以很显然是提供给用户调用的，那么这个方法有什么作用呢？

```java
    /**
    如有必要，增加此 ArrayList 实例的容量，以确保它至少可以容纳由minimum capacity参数指定的元素数。
     *
     * @param   minCapacity   所需的最小容量
     */
    public void ensureCapacity(int minCapacity) {
        int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
            // any size if not default element table
            ? 0
            // larger than default for default empty table. It's already
            // supposed to be at default size.
            : DEFAULT_CAPACITY;

        if (minCapacity > minExpand) {
            ensureExplicitCapacity(minCapacity);
        }
    }
```

**最好在 add 大量元素之前用 `ensureCapacity` 方法，以减少增量重新分配的次数**

## 与其他容器的联系

### ArrayList 与 Vector

**相同点**

- 都实现了 `java.util.List`接口。
- 底层数据结构都是用数组实现的。
- 在第一次添加元素时，默认的初始容量都是 10。

**不同点**

- Vector 是线程安全，ArrayList 则是非线程安全。

- Vector 不支持序列化操作。而 ArrayList 实现了 Serializable 接口，可支持序列化。

- Vector 在扩容时与扩容因子有关。如若指定了扩容因子，并且该系数大于 0 ，那么当需要扩容时，新的容量为旧的容量 + 扩容因子的数值。

  倘若没有指定扩容因子，那么 Vector 扩容后的容量默认为旧的容量的**两倍**。

  ```java
  import java.util.Vector;
  
  public class VectorTest {
      public static void main(String[] args) {
          /**
           * 指定初始容量为 2 ，扩容因子为 5
           */
          Vector list = new Vector(2, 5);
          list.add("小民");
          System.out.println("扩容前，容器的容量：" + list.capacity());
          list.add("同学");
          list.add("博客");
          System.out.println("扩容后，容器的容量：" + list.capacity());
          // 输出
          // 扩容前，容器的容量：2
          // 扩容后，容器的容量：7
  
          /**
           * 如若没有指定，默认初始容量为 10 ，扩容后的容量为原容器容量的两倍
           */
          Vector list2 = new Vector();
          list2.add("小民");
          System.out.println("扩容前，容器的容量：" + list2.capacity());
          list2.add("同学");
          list2.add("博客");
          for (int i = 0; i < 8; i++) {
              list2.add("填满默认容量");
          }
          System.out.println("扩容后，容器的容量：" + list2.capacity());
          // 输出
          // 扩容前，容器的容量：10
          // 扩容后，容器的容量：20
      }
  }
  ```

  ## Arraylist 与 LinkedList 区别

  1. **是否保证线程安全：** `ArrayList` 和 `LinkedList` 都是不同步的，也就是不保证线程安全；
  2. **底层数据结构：** `Arraylist` 底层使用的是 **`Object` 数组**；`LinkedList` 底层使用的是 **双向链表** 数据结构（JDK1.6 之前为循环链表，JDK1.7 取消了循环。注意双向链表和双向循环链表的区别，下面有介绍到！）
  3. **插入和删除是否受元素位置的影响：** ① **`ArrayList` 采用数组存储，所以插入和删除元素的时间复杂度受元素位置的影响。** 比如：执行`add(E e)`方法的时候， `ArrayList` 会默认在将指定的元素追加到此列表的末尾，这种情况时间复杂度就是 O(1)。但是如果要在指定位置 i 插入和删除元素的话（`add(int index, E element)`）时间复杂度就为 O(n-i)。因为在进行上述操作的时候集合中第 i 和第 i 个元素之后的(n-i)个元素都要执行向后位/向前移一位的操作。 ② **`LinkedList` 采用链表存储，所以对于`add(E e)`方法的插入，删除元素时间复杂度不受元素位置的影响，近似 O(1)，如果是要在指定位置`i`插入和删除元素的话（`(add(int index, E element)`） 时间复杂度近似为`o(n))`因为需要先移动到指定位置再插入。**
  4. **是否支持快速随机访问：** `LinkedList` 不支持高效的随机元素访问，而 `ArrayList` 支持。快速随机访问就是通过元素的序号快速获取元素对象(对应于`get(int index)`方法)。
  5. **内存空间占用：** `ArrayList` 的空间浪费主要体现在在 list 列表的结尾会预留一定的容量空间，而 `LinkedList` 的空间花费则体现在它的每一个元素都需要消耗比 `ArrayList` 更多的空间（因为要存放直接后继和直接前驱以及数据）。

  ### ArrayList 执行增删操作一定比 LinkedList 的速度慢吗

  非也。

  ## 移除方法

  ### 移除指定下标元素方法

  ```java
  /**
   * 移除列表中指定下标位置的元素
   * 将所有的后续元素，向左移动
   *
   * @param 要移除的指定下标
   * @return 返回被移除的元素
   * @throws 下标越界会抛出IndexOutOfBoundsException
   */
  public E remove(int index) {
      rangeCheck(index);
  
      modCount++;
      E oldValue = elementData(index);
  
      int numMoved = size - index - 1;
      if (numMoved > 0)
              System.arraycopy(elementData,
                      index+1, elementData, index,  numMoved);
      // 将引用置空，让GC回收
      elementData[--size] = null;
  
      return oldValue;
  }
  ```

  ### 移除指定元素方法

  ```java
  /**
   * 移除第一个在列表中出现的指定元素
   * 如果存在，移除返回true
   * 否则，返回false
   *
   * @param o 指定元素
   */
  public boolean remove(Object o) {
      if (o == null) {
          for (int index = 0; index < size; index++)
              if (elementData[index] == null) {
                  fastRemove(index);
                  return true;
              }
      } else {
          for (int index = 0; index < size; index++)
              if (o.equals(elementData[index])) {
                  fastRemove(index);
                  return true;
              }
      }
      return false;
  }
  ```

  > 移除方法名字、参数的个数都一样，使用的时候要注意。

  ### 私有移除方法

  ```java
  /*
   * 私有的 移除 方法 跳过边界检查且不返回移除的元素
   */
  private void fastRemove(int index) {
      modCount++;
      int numMoved = size - index - 1;
      if (numMoved > 0)
          System.arraycopy(elementData, index+1, elementData, index,
                           numMoved);
      // 将引用置空，让GC回收
      elementData[--size] = null;
  }
  ```

  ## 查找方法

  ### 查找指定元素的所在位置

  ```java
  /**
   * 返回指定元素第一次出现的下标
   * 如果不存在该元素，返回 -1
   * 如果 o ==null 会特殊处理
   */
  public int indexOf(Object o) {
      if (o == null) {
          for (int i = 0; i < size; i++)
              if (elementData[i]==null)
                  return i;
      } else {
          for (int i = 0; i < size; i++)
              if (o.equals(elementData[i]))
                  return i;
      }
      return -1;
  }
  ```

  ### **查找指定位置的元素**

  ```java
  /**
   * 返回指定位置的元素
   *
   * @param  index 指定元素的位置
   * @throws index越界会抛出IndexOutOfBoundsException
   */
  public E get(int index) {
      rangeCheck(index);
  
      return elementData(index);
  }
  ```

  > 该方法直接返回elementData数组指定下标的元素，效率还是很高的。所以ArrayList，for循环遍历效率也是很高的。

  ## **序列化方法**

  ```java
  /**
   * 将ArrayLisy实例的状态保存到一个流里面
   */
  private void writeObject(java.io.ObjectOutputStream s)
      throws java.io.IOException{
      // Write out element count, and any hidden stuff
      int expectedModCount = modCount;
      s.defaultWriteObject();
  
      // Write out size as capacity for behavioural compatibility with clone()
      s.writeInt(size);
  
      // 按照顺序写入所有的元素
      for (int i=0; i<size; i++) {
          s.writeObject(elementData[i]);
      }
  
      if (modCount != expectedModCount) {
          throw new ConcurrentModificationException();
      }
  }
  ```

  ## 反序列化方法

  ```java
  /**
   * 根据一个流(参数)重新生成一个ArrayList
   */
  private void readObject(java.io.ObjectInputStream s)
      throws java.io.IOException, ClassNotFoundException {
      elementData = EMPTY_ELEMENTDATA;
  
      // Read in size, and any hidden stuff
      s.defaultReadObject();
  
      // Read in capacity
      s.readInt();
  
      if (size > 0) {
          // be like clone(), allocate array based upon size not capacity
          ensureCapacityInternal(size);
  
          Object[] a = elementData;
          // Read in all elements in the proper order.
          for (int i=0; i<size; i++) {
              a[i] = s.readObject();
          }
      }
  }
  ```

  > 看完序列化，反序列化方法，我们终于又能回答开篇的第二个问题了。elementData之所以用transient修饰，是因为JDK不想将整个elementData都序列化或者反序列化，而只是将size和实际存储的元素序列化或反序列化，从而节省空间和时间。

  ## 创建子数组

  ```java
  public List<E> subList(int fromIndex, int toIndex) {
      subListRangeCheck(fromIndex, toIndex, size);
      return new SubList(this, 0, fromIndex, toIndex);
  }
  ```

  我们看一下简短版的`SubList`：

  ```java
  private class SubList extends AbstractList<E> implements RandomAccess {
      private final AbstractList<E> parent;
      private final int parentOffset;
      private final int offset;
      int size;
  
      SubList(AbstractList<E> parent,
              int offset, int fromIndex, int toIndex) {
          this.parent = parent;
          this.parentOffset = fromIndex;
          this.offset = offset + fromIndex;
          this.size = toIndex - fromIndex;
          this.modCount = ArrayList.this.modCount;
      }
  
      public E set(int index, E e) {
          rangeCheck(index);
          checkForComodification();
          E oldValue = ArrayList.this.elementData(offset + index);
          ArrayList.this.elementData[offset + index] = e;
          return oldValue;
      }
  
      // 省略代码...
  }
  ```

  - SubList的set()方法，**是直接修改ArrayList**中`elementData`数组的，使用中应该注意
  - SubList是没有实现`Serializable`接口的，**是不能序列化的**

  ## 迭代器

  ### 创建迭代器方法

  ```java
  public Iterator<E> iterator() {
      return new Itr();
  }
  ```

  ### Itr属性

  ```java
  // 下一个要返回的元素的下标
  int cursor;
  // 最后一个要返回元素的下标 没有元素返回 -1
  int lastRet = -1;
  // 期望的 modCount
  int expectedModCount = modCount;
  ```

  ### Itr的hasNext() 方法

  ```java
  public boolean hasNext() {
      return cursor != size;
  }
  ```

  ### Itr的next()方法

  ```java
  public E next() {
      checkForComodification();
      int i = cursor;
      if (i >= size)
          throw new NoSuchElementException();
      Object[] elementData = ArrayList.this.elementData;
      if (i >= elementData.length)
          throw new ConcurrentModificationException();
      cursor = i + 1;
      return (E) elementData[lastRet = i];
  }
  
  final void checkForComodification() {
      if (modCount != expectedModCount)
          throw new ConcurrentModificationException();
  }
  ```

  > 在迭代的时候，会校验modCount是否等于expectedModCount，不等于就会抛出著名的ConcurrentModificationException异常。什么时候会抛出ConcurrentModificationException？

  ```java
  public static void main(String[] args) {
      ArrayList arrayList = new ArrayList();
      for (int i = 0; i < 10; i++) {
          arrayList.add(i);
      }
      remove(arrayList);
      System.out.println(arrayList);
  }
  
  public static void remove(ArrayList<Integer> list) {
      Iterator<Integer> iterator = list.iterator();
      while (iterator.hasNext()) {
          Integer number = iterator.next();
          if (number % 2 == 0) {
              // 抛出ConcurrentModificationException异常
              list.remove(number);
          }
      }
  }
  ```

  > 那怎么写才能不抛出ConcurrentModificationException？很简单，将list.remove(number);换成iterator.remove();即可。why？请看Itr的remove()源码…

  ### Itr的remove()方法

  ```java
  public void remove() {
      if (lastRet < 0)
          throw new IllegalStateException();
      checkForComodification();
  
      try {
          ArrayList.this.remove(lastRet);
          cursor = lastRet;
          lastRet = -1;
          // 移除之后将modCount 重新赋值给 expectedModCount
          expectedModCount = modCount;
      } catch (IndexOutOfBoundsException ex) {
          throw new ConcurrentModificationException();
      }
  }
  ```

  原因就是因为Itr的`remove()`方法，移除之后将`modCount`重新赋值给 `expectedModCount`。这就是源码，不管单线程还是多线程，只要违反了规则，就会抛异常。



我是开源君，关注一波，以后还会带来更多的源码分析。让我们一起在java学习之路上进步！



> 文章首发于GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes) 更多Java相关知识，欢迎访问！