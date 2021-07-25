package negtive;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 *
 * 字符流按照字符读取  首位是1说明是汉字 读取2个字节(GBK下) 首位是0表示是字母 读取1个字节 字符流默认查询的是与操作系统一致的码表 中文系统默认GBK码表
 *          * 字节流按照字节读取 每次读取一个字节
 *
 * 项目目录下的something.txt
 *
 */
public class AppTest {

    public static void main(String[] args) throws Exception {

        //统计一个文件中有多少个字符
        System.out.println(countChar() + "个字符");

        //统计单词
        System.out.println(countWord() + "单词");

    }

    //统计一个文件中有多少个字符
            public static int countChar() throws Exception {
                Reader reader = new FileReader("something.txt");
                int count = 0;
                for (int read = -1;(read = reader.read()) != -1;){
                    //System.out.print((char)read);
                    count++;
        }
        return count;
    }

    /**
     * 统计单词个数
     * @return
     * @throws Exception
     */
    public static int countWord() throws Exception {

        Reader reader = new FileReader("something.txt");
        BufferedReader bufferedReader = new BufferedReader(reader);

        String line = null;
        int count = 0;
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();

        for (;(line = bufferedReader.readLine()) != null;) {
            //System.out.println(line);
            stringBuilder.append(line);
            stringBuilder.append("\n");
            count++;
        }
        String string = stringBuilder.toString();
        String[] s = string.split("[^a-zA-Z]+");
        return s.length;

    }

}
