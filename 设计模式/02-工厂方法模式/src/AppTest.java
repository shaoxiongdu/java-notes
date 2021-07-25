/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */

//抽象产品 食物
interface Food{
    void eat();
}

//具体产品 汉堡包
class Hamburger implements Food{
    @Override
    public void eat() {
        System.out.println("吃汉堡包!");
    }
}

//具体产品 米线
class RiceNoodle implements  Food{

    @Override
    public void eat() {
        System.out.println("吃米线");
    }
}

interface FoodFactory{
    public Food getFood();
}

//具体产品
class HamburgerFactory implements FoodFactory{

    @Override
    public Food getFood() {
        return new Hamburger();
    }
}

//具体产品
class RiceNoodleFactory implements FoodFactory{

    @Override
    public Food getFood() {
        return new RiceNoodle();
    }
}

//测试
public class AppTest {
    public static void main(String[] args) {
        //工厂接口
        FoodFactory foodFactory = new RiceNoodleFactory();
        foodFactory.getFood().eat();
    }
}
