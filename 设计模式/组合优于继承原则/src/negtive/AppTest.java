package negtive;

import java.util.HashSet;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 *
 * 需求： 要求制作一个集合 要求记录曾经加过多少个元素（非size）
 *
 */
//自定义类
class MySet extends HashSet {

    private int addCount = 0;

    @Override
    public boolean add(Object o) {
        addCount++;
        return super.add(o);
    }

    public int getAddCount() {
        return addCount;
    }
}
public class AppTest {

    public static void main(String[] args) {

        MySet mySet = new MySet();
        mySet.add("张三");
        mySet.add("张4");
        mySet.add("张5");

        System.out.println("mySet.getAddCount() = " + mySet.getAddCount());

    }

}
