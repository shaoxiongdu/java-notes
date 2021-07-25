package negtive;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */

//长方形
class Rectangle{
    private double length;
    private double width;

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "Rectangle{" + "length=" + length + ", width=" + width + '}';
    }
}

// 正方形
class Square extends Rectangle{
    // 边长
    private double sidLength;

    @Override
    public double getLength() {
        return sidLength;
    }

    @Override
    public void setLength(double length) {
        this.sidLength = length;
    }

    @Override
    public double getWidth() {
        return sidLength;
    }

    @Override
    public void setWidth(double width) {
        this.sidLength = width;
    }

    @Override
    public String toString() {
        return "sidLength = " + this.sidLength;
    }
}

class Utils{
    //变形 让宽等与长
    public static void transform(Rectangle rectangle){
        while (rectangle.getWidth() <= rectangle.getLength()){
            rectangle.setWidth(rectangle.getWidth()+1);
            System.out.println("rectangle.width = " + rectangle.getWidth());
        }
    }
}

public class AppTest {

    public static void main(String[] args) {

        //创造长方形
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(10);
        rectangle.setLength(20);

        System.out.println(rectangle);

        //变形
        Utils.transform(rectangle);

        System.out.println(rectangle);

        //创造正方形
        Square square = new Square();
        square.setWidth(10);
        square.setLength(20);

        System.out.println(square);

        //变形
        Utils.transform(square); //死循环

        System.out.println(square);

    }

}
