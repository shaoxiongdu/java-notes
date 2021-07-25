package negtive;

import java.util.Date;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class WeekReport {

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
}
