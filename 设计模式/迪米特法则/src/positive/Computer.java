package positive;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class Computer {

    private void saveData(){
        System.out.println("保存数据");
    }

    private void  killProcess(){
        System.out.println("关闭程序");
    }

    private void closeScreen(){
        System.out.println("关闭屏幕");
    }

    private void powerOff(){
        System.out.println("断电");
    }

    /**
     * 对外保留一个关机方法  细节不对外开放
     */
    public void shutdown(){
        saveData();
        killProcess();
        closeScreen();
        powerOff();
    }

}
