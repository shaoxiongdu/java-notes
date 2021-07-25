package positive;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class Person {
    public void feed(Animal animal) {
        System.out.println("人开始喂动物");
        animal.eat();
    }
}
