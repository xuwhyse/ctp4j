package com.whyse.jni.hello;

/**
 * 2.生成.h文件到E:\javaAbout\workspace\ctp4j\target\classes  下面
 * E:\javaAbout\workspace\ctp4j\target\classes>javah com.whyse.jni.hello.MathJni
 * 
 * 3.jni.h在JAVA_HOME/include里面
 * author:xumin 
 * 2016-5-4 下午4:05:49
 */
public class MathJni {
	public native int Multiply(int a, int b);
	public native static MathJni CreateMath(String pszFlowPath);
	public static native int Add(int a, int b);
}
