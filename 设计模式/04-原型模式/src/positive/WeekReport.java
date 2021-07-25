package positive;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.Date;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class WeekReport implements Cloneable{

    //自增主键记录
    private static int incrementId = 0;

    //唯一标识
    private int id;
    //部门
    private String emp;
    //总结
    private String summary;
    //下周计划
    private String plain;
    //意见
    private String suggestion;
    //提交时间
    private Date time;

    /**
     * 底层直接复制二进制文件   不会调用构造方法
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public WeekReport clone() throws CloneNotSupportedException {

        try {
            //将对象写入内存
            ByteOutputStream byteOutputStream = new ByteOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(this);

            //从内存读取数据到字节数组
            byte[] bytes = byteOutputStream.toByteArray();
            //将字节数组作为input
            InputStream inputStream = new ByteArrayInputStream(bytes);
            //读取数据
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (WeekReport)objectInputStream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public WeekReport() {
    }

    public WeekReport(String emp, String summary, String plain, String suggestion, Date time) {

        this.id = ++incrementId;
        this.emp = emp;
        this.summary = summary;
        this.plain = plain;
        this.suggestion = suggestion;
        this.time = time;
    }

    @Override
    public String toString() {
        return "WeekReport{" +
                "id=" + id +
                ", 部门='" + emp + '\'' +
                ", 周报='" + summary + '\'' +
                ", 下周计划='" + plain + '\'' +
                ", 建议='" + suggestion + '\'' +
                ", 时间=" + time +
                '}';
    }

    public static void setIncrementId(int incrementId) {
        WeekReport.incrementId = incrementId;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setPlain(String plain) {
        this.plain = plain;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public static int getIncrementId() {
        return incrementId;
    }

    public int getId() {
        return id;
    }

    public String getEmp() {
        return emp;
    }

    public String getSummary() {
        return summary;
    }

    public String getPlain() {
        return plain;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public Date getTime() {
        return time;
    }
}
