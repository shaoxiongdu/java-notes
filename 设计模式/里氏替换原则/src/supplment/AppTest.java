package supplment;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 *
 *  重写
 *      1. 重写的方法不能比被重写的方法抛出更宽泛的异常
 *      2. 重写的方法不能比被重写的方法有更严格的修饰符
 *
 *      为什么要有这两个限制：
 *          保证子类对象替换父类对象之后，语法不会报错。 符合里氏替换原则。
 */

class father{
    public void eat(){

    }
}
class son extends father{
    /*重写父类方法*/
    @Override
    public void eat(){

    }
}

public class AppTest {

    public static void main(String[] args) {



    }

}
