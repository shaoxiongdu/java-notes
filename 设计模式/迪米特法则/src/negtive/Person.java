package negtive;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class Person {

    private Computer computer = new Computer();

    //关机操作
    public void shutdown(){
        computer.saveData();
        computer.killProcess();
        computer.closeScreen();
        computer.powerOff();
    }
}
