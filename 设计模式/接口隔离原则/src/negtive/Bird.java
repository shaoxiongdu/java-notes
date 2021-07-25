package negtive;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class Bird implements Animal{
    @Override
    public void eat() {
        System.out.println("鸟吃虫子");
    }

    @Override
    public void swim() {
        throw new RuntimeException("鸟不会飞啊");
    }

    @Override
    public void fly() {
        System.out.println("鸟会飞");
    }
}
