package positive;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class Dog implements Eatable,Swimable{
    @Override
    public void eat() {
        System.out.println("狗吃屎");
    }

    @Override
    public void swim() {
        System.out.println("狗刨");
    }
}
