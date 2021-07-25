package negtive;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class Person {
    public void feed(Dog dog){
        System.out.println("人开始喂狗");
        dog.eat();
    }

    public void feed(Cat cat){
        System.out.println("人开始喂猫");
        cat.eat();
    }
}
