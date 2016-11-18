package com.whyse.lib.jna.test;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface Mathjna extends Library{
	public Pointer CreateMath(String pszFlowPath);
	public int Multiply(int a, int b);
}
