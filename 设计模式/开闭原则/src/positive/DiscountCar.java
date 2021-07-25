package positive;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 * 继承原有类 重写方法
 */
public class DiscountCar extends Car{

    @Override
    public double getPrice() {
        return super.getPrice() * 0.8;
    }
}
