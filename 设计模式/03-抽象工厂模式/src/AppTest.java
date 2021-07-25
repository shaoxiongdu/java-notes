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

//抽象产品 饮料
interface Drink{
    public void drink();
}
//具体产品 可乐
class Cola implements Drink{
    @Override
    public void drink() {
        System.out.println("可口可乐，你值得拥有！");
    }
}
//具体产品 缤纷
class IcePeak implements Drink{
    @Override
    public void drink() {
        System.out.println("西安人从小就喝");
    }
}
//抽象产品工厂 工厂
interface Factory{
    public Food getFood();
    public Drink getDrink();
}

class KFCFactory implements Factory{
    @Override
    public Food getFood() {
        return new Hamburger();
    }

    @Override
    public Drink getDrink() {
        return new Cola();
    }
}

class SanQinFactory implements Factory{
    @Override
    public Food getFood() {
        return new RiceNoodle();
    }

    @Override
    public Drink getDrink() {
        return new IcePeak();
    }
}
