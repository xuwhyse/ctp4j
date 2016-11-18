package com.whyse.main;

import org.bridj.Pointer;

import com.sun.jna.Native;
import com.whyse.lib.jna.test.Mathjna;
import com.whyse.lib.test.Math;
import com.whyse.lib.test.TestLibrary;
import com.whyse.main.trader.BridjUtils;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sysTemPath = System.getProperty("user.dir");
		String path = sysTemPath+"/src/main/resources/lib/win64/bridjTest.dll";
		bridjTest(path);
//		jnaTest(path);
//		jniTest(path);
	}

	private static void bridjTest(String path) {
		MainTest.loadLibrary(path, "test");
		
		//Java_com_whyse_lib_test_Math_CreateMath
		Pointer<Byte> aa = BridjUtils.stringToBytePointer("f:/");
		Pointer<Math > pmath = Math.CreateMath(aa);
		Math math = pmath.get();
		
//		Math math = new Math(BridjUtils.stringToBytePointer("f:/"));
		
		int c = TestLibrary.Add(3, 4);
		System.err.println(c);
		c = math.Multiply(3, 4);
		System.err.println(c);
	}

}
