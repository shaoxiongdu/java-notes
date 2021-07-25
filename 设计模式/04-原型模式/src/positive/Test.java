package positive;

import java.util.Date;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class Test {

    public static void main(String[] args) throws CloneNotSupportedException {

        WeekReport weekReport1 = new WeekReport("杜少雄", "学习了程序的七大设计原则", "学习完设计模式", "无", new Date());

        System.out.println("第一周周报" + weekReport1);
        System.out.println("--------------------------------------");

        WeekReport weekReport2 =  weekReport1.clone();

        System.out.println("第二周周报" + weekReport2);

    }

}
