package b;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class Test {

    public static void main(String[] args) {

        Computer computer = new ComputerBuilder().builder();

        System.out.println(computer);
    }

}

class ComputerBuilder{

    private Computer computer = new Computer();

    public Computer builder(){
        computer.setCpu("I7 8750HK");
        computer.setGpu("3060Ti");
        computer.setMemory("32G");
        computer.setHd("1T固态");
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
