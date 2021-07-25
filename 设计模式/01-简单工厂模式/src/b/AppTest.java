package b;

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

//食物工厂
class FoodFactory{

    //获取食物
    public static Food getFood(int type){
        Food food = null;
        switch (type){
            case 1 :
                food = new Hamburger();
                break;
            case 2 :
                food = new RiceNoodle();
                break;
        }
        return food;
    }
}
//测试
public class AppTest {
    public static void main(String[] args) {
        FoodFactory.getFood(1).eat();
        FoodFactory.getFood(2).eat();
    }
}
