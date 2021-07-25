package positive;

import java.io.*;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 * 统计文件    使用单一职责原则 将读取文件生成字符串单独抽为一个方法
 */
public class AppTest {

    /**
     * 通过文件读取字符串
     * @return
     */
    public static String getStringByReadFile(String filePath) throws Exception {
        Reader reader = new FileReader(filePath);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = null;
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    public static void main(String[] args) {



    }

    /**
     * 统计单词个数
     * @return
     * @throws Exception
     */
    public static int countWord() throws Exception {

        String s = getStringByReadFile("something.txt");
        String[] l = s.split("[^a-zA-Z]+");
        return l.length;

    }



}
