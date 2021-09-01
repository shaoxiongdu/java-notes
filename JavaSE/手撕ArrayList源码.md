# 手撕ArrayList源码.md

> 文章已同步至GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes)



### ArrayList一直是面试的重点。今天我们来了解了解它的源码吧！

>  首先看一下集合的继承结构图

![image-20210901114846291](https://gitee.com/ShaoxiongDu/imageBed/raw/master/image-20210901114846291.png)

> 源码分析

```java

/**
 * 首发 Github开源项目: [Java超神之路] <https://github.com/shaoxiongdu/java-notes>
 * 顺序表：线性表的顺序存储结构，内部使用数组实现，非线程安全
 * @param <E> 泛型
 */
// 
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
    
    /**
     * 默认初始容量。
     */
    private static final int DEFAULT_CAPACITY = 10;
    
    /**
     * 要分配的数组的最大大小（除非必要）。一些 VM 在数组中保留一些头字。尝试分配更大的数组可能会导致 OutOfMemoryError：请求的数组大小超出 VM 限制
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
    /**
     * 空数组  在JDK1.8之后 ArrayList采用懒汉式创建对象  会用此值初始化
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};
    
    /**
  用于默认大小的空实例的共享空数组实例。与 EMPTY_ELEMENTDATA 区分开来，以了解添加第一个元素时要膨胀多少。
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    
    /**
        ArrayList 存储当前顺序表的元素 ArrayList 的容量就是这个数组缓冲区的长度。添加第一个元素时，任何带有 elementData == 默认空数组 的 ArrayList 都将扩展为 默认容量 10
     */
    transient Object[] elementData; // non-private to simplify nested class access
    

    // 元素数量
    private int size;

    /*▼ 构造器 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * 构造一个初始容量为 10 的空列表。（此处注释为1.7的版本）1.7为饿汉式 直接创建大小为10的数组
     *
     * 1.8之后为懒汉式，在添加元素的时候会判断是否为空然后创建。
     *
     * 但是到了1.8 注释并没有更新 🙄 首发 Github开源项目: [Java超神之路] <https://github.com/shaoxiongdu/java-notes>
     * ，
     */
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
    
    /**
     * 构造一个具有指定初始容量的空数组。
     *
     * @param initialCapacity 指定初始容量
     *
     * @throws IllegalArgumentException 如果指定的初始容量为负 抛出此异常
     */
    public ArrayList(int initialCapacity) {
        if(initialCapacity>0) {
            this.elementData = new Object[initialCapacity];
        } else if(initialCapacity == 0) {// 如果传入的指定容量为0  则使用默认值10
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            //抛出容量非法异常
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }
    
    /**
     * 按照参数集合的迭代器返回的顺序构造一个包含指定集合元素的列表。
     *
     * @param c 其元素将被放入此列表的集合
     *
     * @throws NullPointerException 如果指定的集合为空
     */
    public ArrayList(Collection<? extends E> c) {
        elementData = c.toArray();
        
        if((size = elementData.length) != 0) {
            /*
             * 防止 c.toArray不返回 Object[]  详情见 https://bugs.openjdk.java.net/browse/JDK-6260652
             */
            if(elementData.getClass() != Object[].class) {
                elementData = Arrays.copyOf(elementData, size, Object[].class);
            }
        } else {
            // 替换为空数组。
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }
    
    /*▲ 构造器 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 存值 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     *将指定的元素追加到此列表的末尾。
     *
     * @param e 要附加到此列表的元素
     *
     * @return {@code true}是否添加成功
     */
    // 将元素e追加到当前顺序表中
    public boolean add(E e) {
        //修改次数
        modCount++;
        add(e, elementData, size);
        return true;
    }
    
    /**
     * 在此列表中的指定位置插入指定元素。将当前在该位置的元素（如果有）和任何后续元素向右移动（向它们的索引添加一个）。
     *
     * @param index  要插入指定元素的索引
     * @param element 要插入的元素
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 将元素element添加到顺序表index处
    public void add(int index, E element) {
        //校验索引是否合法
        rangeCheckForAdd(index);

        //修改次数
        modCount++;
        
        final int s;
        Object[] elementData;
        
        // 如果顺序表已满，则需要扩容
        if((s = size) == (elementData = this.elementData).length) {
            // 对当前顺序表扩容
            elementData = grow();
        }
        
        // 将后方的所有元素后移
        System.arraycopy(elementData, index, elementData, index + 1, s - index);
        
        // 插入元素
        elementData[index] = element;

        //长度+1
        size = s + 1;
    }
    
    
    /**
     按照指定集合的迭代器返回的顺序，将指定集合中的所有元素追加到此列表的末尾。
     *
     * @param c 包含要添加到此列表的元素的集合
     *
     * @return {@code true} 如果此列表因调用而更改
     *
     * @throws NullPointerException 如果指定的集合为空
     * 
     * 首发 Github开源项目: [Java超神之路] <https://github.com/shaoxiongdu/java-notes>
     */
    // 将指定容器中的元素追加到当前顺序表中
    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        
        modCount++;
        
        int numNew = a.length;
        if(numNew == 0) {
            return false;
        }
        
        Object[] elementData;
        final int s;
        //如果当前数组长度小于参数容器
        if(numNew>(elementData = this.elementData).length - (s = size)) {
            //新容量 = 旧 + 参数容器容量
            elementData = grow(s + numNew);
        }
        
        System.arraycopy(a, 0, elementData, s, numNew);
        
        size = s + numNew;
        
        return true;
    }
    
    /**
    从指定位置开始，将指定集合中的所有元素插入此列表。将当前在该位置的元素（如果有）和任何后续元素向右移动（增加它们的索引）。新元素将按照指定集合的迭代器返回的顺序出现在列表中。
     *
     * @param index 从指定集合插入第一个元素的索引
     * @param c     包含要添加到此列表的元素的集合
     *
     * @return {@code true} 如果此列表因调用而更改
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException      如果指定的集合为空
     */
    // 将指定容器中的元素添加到当前顺序表的index处
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);
        
        Object[] a = c.toArray();
        modCount++;
        int numNew = a.length;
        if(numNew == 0) {
            return false;
        }
        Object[] elementData;
        final int s;
        if(numNew>(elementData = this.elementData).length - (s = size)) {
            elementData = grow(s + numNew);
        }
        
        int numMoved = s - index;
        if(numMoved>0) {
            System.arraycopy(elementData, index, elementData, index + numNew, numMoved);
        }
        System.arraycopy(a, 0, elementData, index, numNew);
        size = s + numNew;
        return true;
    }
    
    /*▲ 存值 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 取值 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     *返回此列表中指定位置的元素。
     *
     * @param index 要返回的元素的索引
     *
     * @return此列表中指定位置的元素
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 获取指定索引处的元素
    public E get(int index) {
        //校验索引是否合法
        Objects.checkIndex(index, size);
        return elementData(index);
    }
    
    /*▲ 取值 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 移除 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     *移除此列表中指定位置的元素。将任何后续元素向左移动（从它们的索引中减去一个）。
     *
     * @param index 要删除的元素的索引
     *
     * @return 从列表中删除的元素
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 移除索引index处的元素，返回被移除的元素
    public E remove(int index) {
        Objects.checkIndex(index, size);
        final Object[] es = elementData;

        //该注解忽略编译器的警告
        @SuppressWarnings("unchecked")
        E oldValue = (E) es[index];
        
        // 移除es[index]
        fastRemove(es, index);
        
        return oldValue;
    }
    
    /**
     *  移除指定的元素，返回值指示是否移除成功
     * @param o 要从此列表中删除的元素（如果存在）
     *
     * @return {@code true} 如果此列表包含指定的元素
     */
    public boolean remove(Object o) {
        final Object[] es = elementData;
        final int size = this.size;
        int i = 0;
found:
        {
            //如果参数为Null 则查找第一个null
            if(o == null) {
                for(; i<size; i++) {
                    if(es[i] == null) {
                        break found;
                    }
                }
            } else {//否则查找第一个参数o 调用equals
                for(; i<size; i++) {
                    if(o.equals(es[i])) {
                        break found;
                    }
                }
            }
            return false;
        }
        
        // 移除es[index]
        fastRemove(es, i);
        
        return true;
    }
    
    
    /**
     * @throws NullPointerException {@inheritDoc}
     */
    // 移除满足条件的元素，移除条件由filter决定，返回值指示是否移除成功
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return removeIf(filter, 0, size);
    }
    
    
    /**
     * (匹配则移除)移除当前顺序表中所有与给定容器中的元素匹配的元素
     *
     * @param c 包含要从此列表中删除的元素的集合
     *
     * @return {@code true} 如果此列表因调用而更改
     *
     * @throws ClassCastException   如果此列表的元素的类与指定的集合不兼容
     * @throws NullPointerException 如果此列表包含空元素并且指定的集合不允许空元素
     * @see Collection#contains(Object)
     */
    //
    public boolean removeAll(Collection<?> c) {
        return batchRemove(c, false, 0, size);
    }
    
    /**
     * (不匹配则移除)移除当前顺序表中所有与给定容器中的元素不匹配的元素 （将自身和参数集合的交集赋给自身）
     *
     * @param c 包含要保留在此列表中的元素的集合
     *
     * @return {@code true} 如果此列表因调用而更改
     *
     * @throws ClassCastException  如果此列表的元素的类与指定的集合不兼容
     * @throws NullPointerException 如果此列表包含空元素并且指定的集合不允许空元素
     * @see Collection#contains(Object)
     */
    //
    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, true, 0, size);
    }
    
    
    /**
     * 移除当前顺序表[fromIndex,toIndex]之间的元素
     * @throws IndexOutOfBoundsException
     */
    protected void removeRange(int fromIndex, int toIndex) {
        if(fromIndex>toIndex) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(fromIndex, toIndex));
        }
        
        modCount++;
        
        // 移除from到to之间的元素
        shiftTailOverGap(elementData, fromIndex, toIndex);
    }
    
    
    /**
        将此列表中所有元素赋值为null。此调用返回后，列表将为空。
     */
    // 清空当前顺序表中的元素
    public void clear() {
        modCount++;
        final Object[] es = elementData;
        for(int to = size, i = size = 0; i<to; i++) {
            es[i] = null;
        }
    }
    
    /*▲ 移除 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 替换 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * 将index处的元素更新为element，并返回旧元素
     *
     * @param index  要替换的元素的索引
     * @param element 要存储在指定位置的元素
     *
     * @return 之前在指定位置的元素
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    //
    public E set(int index, E element) {
        Objects.checkIndex(index, size);
        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }
    
    
    // 更新当前顺序表中所有元素，更新策略由operator决定
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        replaceAllRange(operator, 0, size);
        modCount++;
    }
    
    /*▲ 替换 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 包含查询 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * 判断当前顺序表中是否包含指定的元素
     *
     */
    // 
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
    
    /*▲ 包含查询 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 定位 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
	*	返回指定元素的正序索引(正序查找首个匹配的元素)
     */
    // 
    public int indexOf(Object o) {
        // 在[0, size)之间正序搜索元素o，返回首个匹配的索引
        return indexOfRange(o, 0, size);
    }
    
    /**
     *  返回指定元素的逆序索引(逆序查找首个匹配的元素)
     */
    //
    public int lastIndexOf(Object o) {
        // 在[0, size)之间逆序搜索元素o，返回首个匹配的索引
        return lastIndexOfRange(o, 0, size);
    }
    
    /*▲ 定位 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 视图 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * 返回[fromIndex, toIndex)之间的元素的子集合
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IllegalArgumentException  {@inheritDoc}
     */
    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubList<>(this, fromIndex, toIndex);
    }
    
    
    /**
     * 以数组形式返回当前顺序表
     *
     * @return 按适当顺序包含此列表中所有元素的数组
     */
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }
    
    /**
     将当前顺序表中的元素存入数组a后返回，需要将链表中的元素转换为T类型
     *
     * @param a 要存储列表元素的数组，如果它足够大；否则，将为此分配一个相同运行时类型的新数组。
     *
     * @return 包含列表元素的数组
     *
     * @throws ArrayStoreException  如果指定数组的运行时类型不是此列表中每个元素的运行时类型的超类型
     * @throws NullPointerException 如果指定的数组为空
     */
    //
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if(a.length<size){
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        }
        
        System.arraycopy(elementData, 0, a, 0, size);
        
        if(a.length>size) {
            a[size] = null;
        }
        
        return a;
    }
    
    /*▲ 视图 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 迭代 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * 遍历当前顺序表中的元素，并对其应用指定的择取操作
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        final Object[] es = elementData;
        final int size = this.size;
        for(int i = 0; modCount == expectedModCount && i<size; i++) {
            action.accept(elementAt(es, i));
        }
        if(modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
    
    
    /**
     * 返回当前顺序表的一个迭代器（内部类 实现iterable接口）
     *
     * <p>The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @return 以适当顺序遍历此列表中元素的迭代器
     */
    //
    public Iterator<E> iterator() {
        return new Itr();
    }
    
    /**
     * 返回当前顺序表的一个增强的迭代器ListIterator,该接口继承Iterator 在集合迭代器的基础上新增了向前遍历等功能。
     * @see #listIterator(int)
     */
    //
    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }
    
    /**
     * 返回当前顺序表的一个增强的迭代器，且设定下一个待遍历元素为索引index处的元素
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public ListIterator<E> listIterator(int index) {
        rangeCheckForAdd(index);
        return new ListItr(index);
    }
    
    
    /**
     * 返回一个可分割的迭代器
     * @return a {@code Spliterator} 在此列表中的元素上
     */
    //
    @Override
    public Spliterator<E> spliterator() {
        return new ArrayListSpliterator(0, -1, 0);
    }
    
    /*▲ 迭代 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 杂项 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * 返回当前顺序表的元素数量
     * @return 此列表中的元素数
     */
    public int size() {
        return size;
    }
    
    /**
     * Returns {@code true} 判断当前顺序表是否为空
     *
     * @return {@code true} 如果此列表不包含任何元素
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    
    // 使用指定的比较器对当前顺序表内的元素进行排序
    @Override
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super E> c) {
        final int expectedModCount = modCount;
        Arrays.sort((E[]) elementData, 0, size, c);
        if(modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }
    
    
    /**
     * 重新设置顺序表的容量，如果新容量小于元素数量，则会移除超出新容量的元素
     */
    //
    public void trimToSize() {
        modCount++;
        if(size<elementData.length) {
            elementData = (size == 0) ? EMPTY_ELEMENTDATA : Arrays.copyOf(elementData, size);
        }
    }
    
    /**
        确保当前顺序表至少拥有minCapacity的容量
     * @param minCapacity the desired minimum capacity
     */
    public void ensureCapacity(int minCapacity) {
        if(minCapacity>elementData.length && !(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA && minCapacity<=DEFAULT_CAPACITY)) {
            modCount++;
            grow(minCapacity);
        }
    }
    
    /*▲ 杂项 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 序列化 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    private static final long serialVersionUID = 8683452581122892189L; //首发 Github开源项目: [Java超神之路] <https://github.com/shaoxiongdu/java-notes>
    
    
    /**
     *  序列化
     * @param s the stream
     *
     * @throws IOException if an I/O error occurs
     * @serialData The length of the array backing the {@code ArrayList}
     * instance is emitted (int), followed by all of its elements
     * (each an {@code Object}) in the proper order.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        
        int expectedModCount = modCount;
        s.defaultWriteObject();
        
        // 写出大小作为与 clone() 的行为兼容性的容量
        s.writeInt(size);
        
        // 按正确的顺序写出所有元素。
        for(int i = 0; i<size; i++) {
            s.writeObject(elementData[i]);
        }
        
        //验证修改次数
        if(modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
    
    /**
     * 反序列化
     * @param s the stream
     *
     * @throws ClassNotFoundException if the class of a serialized object
     *                                could not be found
     * @throws IOException            if an I/O error occurs
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        
        s.defaultReadObject();
        
        // 反序列化容量
        s.readInt(); // ignored
        
        if(size>0) {
            
            SharedSecrets.getJavaObjectInputStreamAccess().checkArray(s, Object[].class, size);
            Object[] elements = new Object[size];
            
            //按正确顺序读入所有元素。
            for(int i = 0; i<size; i++) {
                elements[i] = s.readObject();
            }
            
            elementData = elements;
        } else if(size == 0) {
            elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new InvalidObjectException("Invalid size: " + size);
        }
    }
    
    /*▲ 序列化 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        
        if(!(o instanceof List)) {
            return false;
        }
        
        final int expectedModCount = modCount;
        // ArrayList 可以被子类化并给出任意行为，但我们仍然可以处理 o 是 ArrayList 的常见情况
        boolean equal = (o.getClass() == ArrayList.class) ? equalsArrayList((ArrayList<?>) o) : equalsRange((List<?>) o, 0, size);
        
        checkForComodification(expectedModCount);
        return equal;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int expectedModCount = modCount;
        int hash = hashCodeRange(0, size);
        checkForComodification(expectedModCount);
        return hash;
    }
    
    /**
     * 浅复制
     *
     * @return a clone of this {@code ArrayList} instance
     */
    public Object clone() {
        try {
            ArrayList<?> v = (ArrayList<?>) super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch(CloneNotSupportedException e) {
            // 这不应该发生，因为是可克隆的
            throw new InternalError(e);
        }
    }
    
    
    
    // 返回元素es[index]
    @SuppressWarnings("unchecked")
    static <E> E elementAt(Object[] es, int index) {
        return (E) es[index];
    }
    
    // 在[start, end)之间正序搜索元素o，返回首个匹配的索引
    int indexOfRange(Object o, int start, int end) {
        Object[] es = elementData;
        if(o == null) {
            for(int i = start; i<end; i++) {
                if(es[i] == null) {
                    return i;
                }
            }
        } else {
            for(int i = start; i<end; i++) {
                if(o.equals(es[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    // 在[start, end)之间逆序搜索元素o，返回首个匹配的索引
    int lastIndexOfRange(Object o, int start, int end) {
        Object[] es = elementData;
        if(o == null) {
            for(int i = end - 1; i >= start; i--) {
                if(es[i] == null) {
                    return i;
                }
            }
        } else {
            for(int i = end - 1; i >= start; i--) {
                if(o.equals(es[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }
    
    boolean equalsRange(List<?> other, int from, int to) {
        final Object[] es = elementData;
        if(to>es.length) {
            throw new ConcurrentModificationException();
        }
        var oit = other.iterator();
        for(; from<to; from++) {
            if(!oit.hasNext() || !Objects.equals(es[from], oit.next())) {
                return false;
            }
        }
        return !oit.hasNext();
    }

    /**
     * 计算哈希码的范围
     * @param from
     * @param to
     * @return
     */
    int hashCodeRange(int from, int to) {
        final Object[] es = elementData;
        if(to>es.length) {
            throw new ConcurrentModificationException();
        }
        int hashCode = 1;
        for(int i = from; i<to; i++) {
            Object e = es[i];
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }

    /**
     * 批量删除
     * @param c
     * @param complement
     * @param from
     * @param end
     * @return
     */
    boolean batchRemove(Collection<?> c, boolean complement, final int from, final int end) {
        Objects.requireNonNull(c);
        final Object[] es = elementData;
        int r;
        // Optimize for initial run of survivors
        for(r = from; ; r++) {
            if(r == end) {
                return false;
            }
            
            if(c.contains(es[r]) != complement) {
                break;
            }
        }
        int w = r++;
        try {
            for(Object e; r<end; r++) {
                if(c.contains(e = es[r]) == complement) {
                    es[w++] = e;
                }
            }
        } catch(Throwable ex) {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            System.arraycopy(es, r, es, w, end - r);
            w += end - r;
            throw ex;
        } finally {
            modCount += end - w;
            shiftTailOverGap(es, w, end);
        }
        return true;
    }
    
    /**
     * 删除所有满足给定谓词的元素，从索引 i（包含）到索引 end（不包含）。
     */
    boolean removeIf(Predicate<? super E> filter, int i, final int end) {
        Objects.requireNonNull(filter);
        
        int expectedModCount = modCount;
        
        final Object[] es = elementData;
        
        // Optimize for initial run of survivors
        while(i<end && !filter.test(elementAt(es, i))) {
            i++;
        }
        
        /*
         * 因此遍历一次以查找要删除的元素，第二遍进行物理删除。
         */
        if(i<end) {
            final int beg = i;
            final long[] deathRow = nBits(end - beg);
            
            deathRow[0] = 1L;   // set bit 0
            
            for(i = beg + 1; i<end; i++) {
                if(filter.test(elementAt(es, i))) {
                    setBit(deathRow, i - beg);
                }
            }
            
            if(modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            
            modCount++;
            int w = beg;
            for(i = beg; i<end; i++) {
                if(isClear(deathRow, i - beg)) {
                    es[w++] = es[i];
                }
            }
            shiftTailOverGap(es, w, end);
            return true;
        } else {
            if(modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            return false;
        }
    }
    
    void checkInvariants() {
        // assert size >= 0;
        // assert size == elementData.length || elementData[size] == null;
    }
    
    // 大容量处理
    private static int hugeCapacity(int minCapacity) {
        if(minCapacity<0) {
            // overflow
            throw new OutOfMemoryError();
        }
        
        return (minCapacity>MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }
    
    /**
     *用于检查 (fromIndex > toIndex) 条件
     */
    private static String outOfBoundsMsg(int fromIndex, int toIndex) {
        return "From Index: " + fromIndex + " > To Index: " + toIndex;
    }
    
    private static long[] nBits(int n) {
        return new long[((n - 1) >> 6) + 1];
    }
    
    private static void setBit(long[] bits, int i) {
        bits[i >> 6] |= 1L << i;
    }
    
    private static boolean isClear(long[] bits, int i) {
        return (bits[i >> 6] & (1L << i)) == 0;
    }
    
    // 对当前顺序表扩容
    private Object[] grow() {
        return grow(size + 1);
    }
    
    /**
     * 增加容量以确保它至少可以容纳由最小容量参数指定的元素数量。
     *
     * @param minCapacity 所需的最小容量
     *
     * @throws OutOfMemoryError 如果 minCapacity 小于零
     */
    // 对当前顺序表扩容，minCapacity是申请的容量
    private Object[] grow(int minCapacity) {
        // 根据申请的容量，返回一个合适的新容量
        int newCapacity = newCapacity(minCapacity);
        return elementData = Arrays.copyOf(elementData, newCapacity);
    }
    
    /**
     * 返回至少与给定最小容量一样大的容量。如果足够，返回当前容量增加 50%。不会返回大于 MAX_ARRAY_SIZE 的容量，除非给定的最小容量大于 MAX_ARRAY_SIZE。
     *
     * @param minCapacity 所需的最小容量
     *
     * @throws OutOfMemoryError如果 minCapacity 小于零
     */
    // 根据申请的容量，返回一个合适的新容量
    private int newCapacity(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;   // 旧容量
        int newCapacity = oldCapacity + (oldCapacity >> 1); // 预期新容量（增加0.5倍）
        
        // 如果预期新容量小于申请的容量
        if(newCapacity - minCapacity<=0) {
            // 如果数组还未初始化
            if(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
                // 返回一个初始容量
                return Math.max(DEFAULT_CAPACITY, minCapacity);
            }
            
            // 溢出
            if(minCapacity<0) {
                // overflow
                throw new OutOfMemoryError();
            }
            
            return minCapacity;
        }
        
        // 在预期新容量大于申请的容量时，按新容量走
        return (newCapacity - MAX_ARRAY_SIZE<=0) ? newCapacity : hugeCapacity(minCapacity);
    }
    
    /**
     *这个辅助方法从 add(E) 中分离出来，以保持方法字节码大小低于 35（-XX:MaxInlineSize 默认值），这有助于在 C1 编译循环中调用 add(E) 。
     */
    // 将元素e添加到elementData[s]
    private void add(E e, Object[] elementData, int s) {
        // 元素填满数组时，需要扩容
        if(s == elementData.length) {
            elementData = grow();
        }
        
        elementData[s] = e;
        
        size = s + 1;
    }
    
    private boolean equalsArrayList(ArrayList<?> other) {
        final int otherModCount = other.modCount;
        final int s = size;
        boolean equal;
        if(equal = (s == other.size)) {
            final Object[] otherEs = other.elementData;
            final Object[] es = elementData;
            if(s>es.length || s>otherEs.length) {
                throw new ConcurrentModificationException();
            }
            for(int i = 0; i<s; i++) {
                if(!Objects.equals(es[i], otherEs[i])) {
                    equal = false;
                    break;
                }
            }
        }
        other.checkForComodification(otherModCount);
        return equal;
    }
    
    private void checkForComodification(final int expectedModCount) {
        if(modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
    
    /**
     * 跳过边界检查并且不返回移除的值的私有移除方法。
     */
    // 移除es[i]
    private void fastRemove(Object[] es, int i) {
        modCount++;
        final int newSize;
        if((newSize = size - 1)>i) {
            System.arraycopy(es, i + 1, es, i, newSize - i);
        }
        es[size = newSize] = null;
    }
    
    // 移除lo~hi之间的元素
    private void shiftTailOverGap(Object[] es, int lo, int hi) {
        System.arraycopy(es, hi, es, lo, size - hi);
        for(int to = size, i = (size -= hi - lo); i<to; i++) {
            es[i] = null;
        }
    }
    
    /**
     *add 和 addAll 使用的 rangeCheck 版本。
     */
    private void rangeCheckForAdd(int index) {
        if(index>size || index<0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
    
    /**
     * 构造一个 IndexOutOfBoundsException 详细消息。在错误处理代码的许多可能重构中，这种“概述”在服务器和客户端 VM 上表现最佳。
     */
    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }
    
    // 更新当前顺序表中[i, end)之间的元素，更新策略由operator决定
    private void replaceAllRange(UnaryOperator<E> operator, int i, int end) {
        Objects.requireNonNull(operator);
        
        final int expectedModCount = modCount;
        final Object[] es = elementData;
        
        while(modCount == expectedModCount && i<end) {
            // 获取元素es[index]
            E element = elementAt(es, i);
            es[i] = operator.apply(element);
            i++;
        }
        
        if(modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
    
    
    
    
    
    
    /**
     * 普通的迭代器  首发 Github开源项目: [Java超神之路] <https://github.com/shaoxiongdu/java-notes>
     *
     *  iterator接口规定
     *
     *  在进行集合遍历的过程中，集合的结构不能发生改变。
     *
     * 开始循环之前，会记录当前集合的修改次数。
     * 每次循环，都会判断记录的值和最新值比较
     *      如果相同 ，说明没有被修改 则继续迭代
     *      如果不同，说明迭代过程中集合被修改了
     *          抛出 ConcurrentModificationException 异常
     *
     */
    private class Itr implements Iterator<E> {
        // 下一个待遍历元素的游标
        int cursor;
        
        // 刚刚遍历过的元素的索引
        int lastRet = -1;

        //期望的修改次数
        int expectedModCount = modCount;

        Itr() {
        }

        /**
         * 是否还有下一个元素
         * @return
         */
        public boolean hasNext() {
            return cursor != size;
        }
        
        @SuppressWarnings("unchecked")
        public E next() {
            //判断集合结构是否被修改
            checkForComodification();
            //记录当前位置
            int i = cursor;
            //越界
            if(i >= size) {
                throw new NoSuchElementException();
            }
            
            Object[] elementData = ArrayList.this.elementData;
            
            if(i >= elementData.length) {
                throw new ConcurrentModificationException();
            }

            //向后移
            cursor = i + 1;
            //返回数据
            return (E) elementData[lastRet = i];
        }
        
        public void remove() {
            if(lastRet<0) {
                throw new IllegalStateException();
            }

            //判断集合结构是否被修改
            checkForComodification();
            
            try {
                ArrayList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch(IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
        
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            final int size = ArrayList.this.size;
            int i = cursor;
            if(i<size) {
                final Object[] es = elementData;
                if(i >= es.length) {
                    throw new ConcurrentModificationException();
                }
                for(; i<size && modCount == expectedModCount; i++) {
                    action.accept(elementAt(es, i));
                }
                // 最后更新一次以减少堆写入流量
                cursor = i;
                lastRet = i - 1;
                checkForComodification();
            }
        }
        
        final void checkForComodification() {
            if(modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    /**
     *增强的迭代器
     */
    private class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            super();
            cursor = index;
        }
        
        public boolean hasPrevious() {
            return cursor != 0;
        }
        
        public int previousIndex() {
            return cursor - 1;
        }
        
        @SuppressWarnings("unchecked")
        public E previous() {
            checkForComodification();
            
            int i = cursor - 1;
            
            if(i<0) {
                throw new NoSuchElementException();
            }
            
            Object[] elementData = ArrayList.this.elementData;
            if(i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            
            cursor = i;
            
            return (E) elementData[lastRet = i];
        }
        
        public int nextIndex() {
            return cursor;
        }
        
        public void add(E e) {
            checkForComodification();
            
            try {
                int i = cursor;
                ArrayList.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch(IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
        
        public void set(E e) {
            if(lastRet<0) {
                throw new IllegalStateException();
            }
            
            checkForComodification();
            
            try {
                ArrayList.this.set(lastRet, e);
            } catch(IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
        
    }
    
    /** 可分割的迭代器*/
    final class ArrayListSpliterator implements Spliterator<E> {
        
        /*
         * 如果 ArrayLists 是不可变的，或者结构上不可变的（没有添加、删除等），
         * 可以使用 Arrays.spliterator 实现它们的拆分器。
         * 相反，在不牺牲太多性能的情况下，在遍历过程中检测到尽可能多的干扰。主要依赖 modCounts。
         * 这些不能保证检测到并发违规，有时对线程内干扰过于保守，
         * 但检测到足够多的问题在实践中是值得的。
         * 为了实现这一点，
         *  （1）延迟初始化fence和expectedModCount，直到我们需要提交到我们正在检查的状态的最新点；从而提高精度。 
         *   (2) 我们在 forEach 的末尾只执行一个 ConcurrentModificationException 检查（对性能最敏感的方法）。
         *          当使用 forEach（而不是迭代器）时，我们通常只能在动作之后检测干扰，而不是之前。进一步的 CME 触发检查适用于所有其他可能违反假设的情况，
 *                  例如 null 或给定 size() 的太小的 elementData 数组，这可能仅由于干扰而发生。这允许 forEach 的内部循环在没有任何进一步检查的情况下运行，并简化了 lambda 解析。
 *                  虽然这确实需要一些检查，但请注意，在 list.stream().forEach(a) 的常见情况下，除了 forEach 本身之外，不会在任何地方进行检查或其他计算。
 *                  其他不太常用的方法不能利用这些流线化中的大部分。
         */
        
        private int index; 
        private int fence; 
        private int expectedModCount; 
        
        /** 创建覆盖给定范围的新拆分器. */
        ArrayListSpliterator(int origin, int fence, int expectedModCount) {
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }
        
        public ArrayListSpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid)
                ? null
                : new ArrayListSpliterator(lo, index = mid, expectedModCount);    // divide range in half unless too small
        }
        
        public boolean tryAdvance(Consumer<? super E> action) {
            if(action == null) {
                throw new NullPointerException();
            }
            int hi = getFence(), i = index;
            if(i<hi) {
                index = i + 1;
                @SuppressWarnings("unchecked")
                E e = (E) elementData[i];
                action.accept(e);
                if(modCount != expectedModCount) {
                    throw new ConcurrentModificationException();
                }
                return true;
            }
            return false;
        }
        
        public void forEachRemaining(Consumer<? super E> action) {
            int i, hi, mc; // 提升机访问和检查循环 首发 Github开源项目: [Java超神之路] <https://github.com/shaoxiongdu/java-notes>
            Object[] a;
            if(action == null) {
                throw new NullPointerException();
            }
            if((a = elementData) != null) {
                if((hi = fence)<0) {
                    mc = modCount;
                    hi = size;
                } else
                    mc = expectedModCount;
                if((i = index) >= 0 && (index = hi)<=a.length) {
                    for(; i<hi; ++i) {
                        @SuppressWarnings("unchecked")
                        E e = (E) a[i];
                        action.accept(e);
                    }
                    if(modCount == mc) {
                        return;
                    }
                }
            }
            throw new ConcurrentModificationException();
        }
        
        public long estimateSize() {
            return getFence() - index;
        }
        
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
        
        private int getFence() { // initialize fence to size on first use
            int hi; // (a specialized variant appears in method forEach)
            if((hi = fence)<0) {
                expectedModCount = modCount;
                hi = fence = size;
            }
            return hi;
        }
    
    }
    
    // 子视图
    private static class SubList<E> extends AbstractList<E> implements RandomAccess {
        private final ArrayList<E> root;
        private final SubList<E> parent;
        private final int offset;
        private int size;
        
        /**
         * 构造任意 ArrayList 的子列表。
         */
        public SubList(ArrayList<E> root, int fromIndex, int toIndex) {
            this.root = root;
            this.parent = null;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }
        
        /**
         *构造另一个 SubList 的子列表。
         */
        private SubList(SubList<E> parent, int fromIndex, int toIndex) {
            this.root = parent.root;
            this.parent = parent;
            this.offset = parent.offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }
        
        public E set(int index, E element) {
            Objects.checkIndex(index, size);
            checkForComodification();
            E oldValue = root.elementData(offset + index);
            root.elementData[offset + index] = element;
            return oldValue;
        }
        
        public E get(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            return root.elementData(offset + index);
        }
        
        public int size() {
            checkForComodification();
            return size;
        }
        
        public void add(int index, E element) {
            rangeCheckForAdd(index);
            checkForComodification();
            root.add(offset + index, element);
            updateSizeAndModCount(1);
        }
        
        public E remove(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            E result = root.remove(offset + index);
            updateSizeAndModCount(-1);
            return result;
        }
        
        public boolean addAll(Collection<? extends E> c) {
            return addAll(this.size, c);
        }
        
        public boolean addAll(int index, Collection<? extends E> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if(cSize == 0) {
                return false;
            }
            checkForComodification();
            root.addAll(offset + index, c);
            updateSizeAndModCount(cSize);
            return true;
        }
        
        public void replaceAll(UnaryOperator<E> operator) {
            root.replaceAllRange(operator, offset, offset + size);
        }
        
        public boolean removeAll(Collection<?> c) {
            return batchRemove(c, false);
        }
        
        public boolean retainAll(Collection<?> c) {
            return batchRemove(c, true);
        }
        
        public boolean removeIf(Predicate<? super E> filter) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified = root.removeIf(filter, offset, offset + size);
            if(modified) {
                updateSizeAndModCount(root.size - oldSize);
            }
            return modified;
        }
        
        public Object[] toArray() {
            checkForComodification();
            return Arrays.copyOfRange(root.elementData, offset, offset + size);
        }
        
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            checkForComodification();
            if(a.length<size) {
                return (T[]) Arrays.copyOfRange(root.elementData, offset, offset + size, a.getClass());
            }
            System.arraycopy(root.elementData, offset, a, 0, size);
            if(a.length>size) {
                a[size] = null;
            }
            return a;
        }
        
        public boolean equals(Object o) {
            if(o == this) {
                return true;
            }
            
            if(!(o instanceof List)) {
                return false;
            }
            
            boolean equal = root.equalsRange((List<?>) o, offset, offset + size);
            checkForComodification();
            return equal;
        }
        
        public int hashCode() {
            int hash = root.hashCodeRange(offset, offset + size);
            checkForComodification();
            return hash;
        }
        
        public int indexOf(Object o) {
            int index = root.indexOfRange(o, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }
        
        public int lastIndexOf(Object o) {
            int index = root.lastIndexOfRange(o, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }
        
        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }
        
        public Iterator<E> iterator() {
            return listIterator();
        }

        /**
         * 返回实现 接口的增强迭代器 (指定索引位置)
         * @param index
         * @return
         */
        public ListIterator<E> listIterator(int index) {
            checkForComodification();
            rangeCheckForAdd(index);
            
            return new ListIterator<E>() {
                int cursor = index;
                int lastRet = -1;
                int expectedModCount = root.modCount;
                
                public boolean hasNext() {
                    return cursor != SubList.this.size;
                }
                
                @SuppressWarnings("unchecked")
                public E next() {
                    checkForComodification();
                    int i = cursor;
                    if(i >= SubList.this.size) {
                        throw new NoSuchElementException();
                    }
                    Object[] elementData = root.elementData;
                    if(offset + i >= elementData.length) {
                        throw new ConcurrentModificationException();
                    }
                    cursor = i + 1;
                    return (E) elementData[offset + (lastRet = i)];
                }
                
                public boolean hasPrevious() {
                    return cursor != 0;
                }
                
                @SuppressWarnings("unchecked")
                public E previous() {
                    checkForComodification();
                    int i = cursor - 1;
                    if(i<0) {
                        throw new NoSuchElementException();
                    }
                    Object[] elementData = root.elementData;
                    if(offset + i >= elementData.length) {
                        throw new ConcurrentModificationException();
                    }
                    cursor = i;
                    return (E) elementData[offset + (lastRet = i)];
                }
                
                public void forEachRemaining(Consumer<? super E> action) {
                    Objects.requireNonNull(action);
                    final int size = SubList.this.size;
                    int i = cursor;
                    if(i<size) {
                        final Object[] es = root.elementData;
                        if(offset + i >= es.length) {
                            throw new ConcurrentModificationException();
                        }
                        for(; i<size && modCount == expectedModCount; i++) {
                            action.accept(elementAt(es, offset + i));
                        }
                        // update once at end to reduce heap write traffic
                        cursor = i;
                        lastRet = i - 1;
                        checkForComodification();
                    }
                }
                
                public int nextIndex() {
                    return cursor;
                }
                
                public int previousIndex() {
                    return cursor - 1;
                }
                
                public void remove() {
                    if(lastRet<0) {
                        throw new IllegalStateException();
                    }
                    checkForComodification();
                    
                    try {
                        SubList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = root.modCount;
                    } catch(IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }
                
                public void set(E e) {
                    if(lastRet<0) {
                        throw new IllegalStateException();
                    }
                    checkForComodification();
                    
                    try {
                        root.set(offset + lastRet, e);
                    } catch(IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }
                
                public void add(E e) {
                    checkForComodification();
                    
                    try {
                        int i = cursor;
                        SubList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = root.modCount;
                    } catch(IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }
                
                final void checkForComodification() {
                    if(root.modCount != expectedModCount) {
                        throw new ConcurrentModificationException();
                    }
                }
            };
        }

        /**
         * 截取指定位置的子数组并返回
         * @param fromIndex
         * @param toIndex
         * @return
         */
        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList<>(this, fromIndex, toIndex);
        }
        
        public Spliterator<E> spliterator() {
            checkForComodification();
            
            //由于后期绑定，此处未使用 ArrayListSpliterator
            return new Spliterator<E>() {
                private int index = offset;
                private int fence = -1; 
                private int expectedModCount;
                
                public ArrayList<E>.ArrayListSpliterator trySplit() {
                    int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
                    
                    return (lo >= mid) ? null //除非太小，否则将范围分成两半
                        : root.new ArrayListSpliterator(lo, index = mid, expectedModCount);
                }
                
                public boolean tryAdvance(Consumer<? super E> action) {
                    Objects.requireNonNull(action);
                    int hi = getFence(), i = index;
                    if(i<hi) {
                        index = i + 1;
                        @SuppressWarnings("unchecked")
                        E e = (E) root.elementData[i];
                        action.accept(e);
                        if(root.modCount != expectedModCount) {
                            throw new ConcurrentModificationException();
                        }
                        return true;
                    }
                    return false;
                }
                
                public void forEachRemaining(Consumer<? super E> action) {
                    Objects.requireNonNull(action);
                    int i, hi, mc; 
                    ArrayList<E> lst = root;
                    Object[] a;
                    if((a = lst.elementData) != null) {
                        if((hi = fence)<0) {
                            mc = modCount;
                            hi = offset + size;
                        } else
                            mc = expectedModCount;
                        if((i = index) >= 0 && (index = hi)<=a.length) {
                            for(; i<hi; ++i) {
                                @SuppressWarnings("unchecked")
                                E e = (E) a[i];
                                action.accept(e);
                            }
                            if(lst.modCount == mc) {
                                return;
                            }
                        }
                    }
                    throw new ConcurrentModificationException();
                }
                
                public long estimateSize() {
                    return getFence() - index;
                }
                
                public int characteristics() {
                    return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
                }
                
                private int getFence() { // 首次使用时将围栏初始化为大小
                    int hi; // (一个专门的变体出现在 forEach 方法中）
                    if((hi = fence)<0) {
                        expectedModCount = modCount;
                        hi = fence = offset + size;
                    }
                    return hi;
                }
            };
        }
        
        protected void removeRange(int fromIndex, int toIndex) {
            checkForComodification();
            root.removeRange(offset + fromIndex, offset + toIndex);
            updateSizeAndModCount(fromIndex - toIndex);
        }

        /**
         * 批量删除
         * @param c
         * @param complement 是否替换
         * @return
         */
        private boolean batchRemove(Collection<?> c, boolean complement) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified = root.batchRemove(c, complement, offset, offset + size);
            if(modified) {
                updateSizeAndModCount(root.size - oldSize);
            }
            return modified;
        }
        
        private void rangeCheckForAdd(int index) {
            if(index<0 || index>this.size) {
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
            }
        }
        
        private String outOfBoundsMsg(int index) {
            return "Index: " + index + ", Size: " + this.size;
        }

        /**
         * 验证最新修改次数和记录的次数
         */
        private void checkForComodification() {
            if(root.modCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }

        /**
         * 修正元素个数和修改次数
         * @param sizeChange
         */
        private void updateSizeAndModCount(int sizeChange) {
            SubList<E> slist = this;
            do {
                slist.size += sizeChange;
                slist.modCount = root.modCount;
                slist = slist.parent;
            } while(slist != null);
        }
        
    }
    
}

```



关注一波，以后还会带来更多的源码分析。让我们一起在java超神之路上进步！





> 文章已同步至GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes) 更多Java相关知识，欢迎访问！