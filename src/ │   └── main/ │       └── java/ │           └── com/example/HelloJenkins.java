package com.example;

public class HelloJenkins {
    public static void main(String[] args) {
        System.out.println("Hello, Jenkins!");
        System.out.println("2 + 3 = " + add(2, 3));
    }

    public static int add(int a, int b) {
        return a + b;
    }
}
