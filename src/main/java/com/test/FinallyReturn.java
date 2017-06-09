package com.test;

/**
 * Created by https://github.com/kuangcp on 17-6-8  下午4:07
 */
public class FinallyReturn {
    private Apple name=new Apple();

    public static void main(String []s){
        Apple name = new FinallyReturn().test();
        System.out.println("result = "+name);
        System.out.println(name);
        System.out.println(name);
    }
    public Apple test(){
        name=new Apple("test");
        try{
            System.out.println("try");
            if(name==null) {
                throw new Exception();
            }
        }catch (Exception e){
            System.out.println("catch");
        }finally {
            setNull(name);
            System.out.println("finally = "+name);
        }

        return name;
    }
    public void setNull(Apple str){

        str = null;
        System.out.println("function = "+str);
    }
}
class Apple{
    String name;
    public Apple(){}
    public Apple(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

