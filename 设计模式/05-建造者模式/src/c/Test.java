package c;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 * 针对不同的需求，需要不同的建造者
 */
public class Test {

    public static void main(String[] args) {

        //打游戏
        System.out.println(new AdvancedComputerBuilder().builder());

        //开发
        System.out.println(new MiddleComputerBuilder().builder());

        //娱乐
        System.out.println(new LowComputerBuilder().builder());
    }

}

/**
 * 高配
 */
class AdvancedComputerBuilder{

    private Computer computer = new Computer();

    public Computer builder(){
        computer.setCpu("I7 8750HK");
        computer.setGpu("3060Ti");
        computer.setMemory("32G");
        computer.setHd("1T固态");
        return computer;

    }

}

/**
 * 中配
 */
class MiddleComputerBuilder{

    private Computer computer = new Computer();

    public Computer builder(){
        computer.setCpu("I7 7700HQ");
        computer.setGpu("1060Ti");
        computer.setMemory("16G");
        computer.setHd("256固态");
        return computer;

    }

}

/**
 * 低配
 */
class LowComputerBuilder{

    private Computer computer = new Computer();

    public Computer builder(){
        computer.setCpu("I5 7500U");
        computer.setGpu("GTX960");
        computer.setMemory("8G");
        computer.setHd("128T固态");
        return computer;

    }

}

class Computer {

    private String cpu;

    private String gpu;

    private String memory;

    //硬盘
    private String hd;

    @Override
    public String toString() {
        return "Computer{" +
                "cpu='" + cpu + '\'' +
                ", gpu='" + gpu + '\'' +
                ", memory='" + memory + '\'' +
                ", hd='" + hd + '\'' +
                '}';
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getGpu() {
        return gpu;
    }

    public void setGpu(String gpu) {
        this.gpu = gpu;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getHd() {
        return hd;
    }

    public void setHd(String hd) {
        this.hd = hd;
    }
}
