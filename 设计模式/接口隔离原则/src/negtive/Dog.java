package negtive;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class Dog implements Animal{

    @Override
    public void eat() {
        System.out.println("狗吃骨头");
    }

    @Override
    public void swim() {
        System.out.println("狗刨");
    }

    @Override
    public void fly() {
        throw new RuntimeException("你行你来");
    }
}
