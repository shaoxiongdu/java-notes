package negtive;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 * 当添加新功能价格打折 违反开闭原则的做法 就是修改之前的get代码
 */
public class Car {

    private double price;

    public double getPrice() {
        /*return price;*/
        //新加打折功能 如果不符合开闭原则 则在此处新加
        return price * 0.8;
    }

}
