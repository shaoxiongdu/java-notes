package d;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 * 创建一个创造者接口 将步骤固定
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
//构造者接口
interface ComputerBuilder{
    public void setCpu();
    public void setGpu();
    public void setMemory();
    public void setHd();

    public Computer builder();
}

/**
 * 高配
 */
class AdvancedComputerBuilder implements ComputerBuilder{

    private Computer computer = new Computer();

    @Override
    public void setCpu() {
        computer.setCpu("I7 8750HK");

    }

    @Override
    public void setGpu() {
        computer.setGpu("3060Ti");

    }

    @Override
    public void setMemory() {
        computer.setMemory("32G");

    }

    @Override
    public void setHd() {
        computer.setHd("1T固态");

    }

    @Override
    public Computer builder() {
        return computer;
    }
}

/**
 * 中配
 */
class MiddleComputerBuilder implements ComputerBuilder{

    private Computer computer = new Computer();

    @Override
    public void setCpu() {
        computer.setCpu("I7 7700HQ");

    }

    @Override
    public void setGpu() {
        computer.setGpu("1060Ti");

    }

    @Override
    public void setMemory() {
        computer.setMemory("16G");

    }

    @Override
    public void setHd() {
        computer.setHd("256固态");

    }

    @Override
    public Computer builder() {
        return computer;
    }
}

/**
 * 低配
 */
class LowComputerBuilder implements ComputerBuilder{

    private Computer computer = new Computer();

    @Override
    public Computer builder(){
        return computer;

    }

    @Override
    public void setCpu() {
        computer.setCpu("I5 7500U");

    }

    @Override
    public void setGpu() {
        computer.setGpu("GTX960");

    }

    @Override
    public void setMemory() {
        computer.setMemory("8G");

    }

    @Override
    public void setHd() {
        computer.setHd("128T固态");

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
