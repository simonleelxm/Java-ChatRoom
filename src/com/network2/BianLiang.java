package com.network2;

import java.util.Scanner;

public class BianLiang {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * int a = 10; int b = 20; int temp; System.out.println(a + " " + b); temp = a;
		 * a = b; b = temp; System.out.println(a + " " + b);
		 */
//		System.out.println(add(10, 20));
		/*
		 * int a = 100, b = 130, c = 670; int max = (a > b ? a : b) > c ? (a > b ? a :
		 * b) : c; System.out.println(max);
		 */
		Scanner sc = new Scanner(System.in);
		int x1 = sc.nextInt();
		int x2 = sc.nextInt();
		int x3 = sc.nextInt();
		System.out.println(x1 + x2);
		int min = (x1 < x2 ? x1 : x2) < x3 ? (x1 < x2 ? x1 : x2) : x3;
		System.out.println(min);
		if (x1 % 2 == 0 && x1 != 0) {
			System.out.println("第一个数是偶数");
		} else if (x1 % 2 != 0 && x1 != 0) {
			System.out.println("第一个数是奇数");
		} else {
			System.out.println("第一个数为0");
		}

	}

	public static int add(int a, int b) {
		return a + b;
	}

}
