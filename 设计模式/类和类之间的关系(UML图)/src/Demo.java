/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class Demo {

    class Animal{}
    class Dog extends Animal{}

    class Engine {}
    class Car{
        Engine engine = new Engine();
    }

    class A {}
    class B{
        void fun(){
            A a = new A();
        }
    }



}
