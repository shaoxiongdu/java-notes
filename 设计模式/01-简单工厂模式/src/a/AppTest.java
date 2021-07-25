package a;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */

//抽象产品
interface Food{
    void eat();
}
//具体产品
class Hamburger implements Food{
    @Override
    public void eat() {
        System.out.println("吃汉堡包!");
    }
}

public class AppTest {
    public static void main(String[] args) {
        Food food = new Hamburger();
        food.eat();
    }
}
