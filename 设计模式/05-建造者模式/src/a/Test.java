package a;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class Test {

    public static void main(String[] args) {

        Computer computer = new Computer();
        computer.setCpu("I5 8300H");
        computer.setGpu("1050Ti");
        computer.setMemory("16G");
        computer.setHd("256固态");

        System.out.println(computer);
    }

}
