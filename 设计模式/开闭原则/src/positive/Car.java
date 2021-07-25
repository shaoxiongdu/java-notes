package positive;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 * 当添加新功能价格打折 使用开闭原则的做法 扩展代码  新增加一个子类 重写get方法
 */
public class Car {

    private double price;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
