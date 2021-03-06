package com.whyse.lib.trader;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Array;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * \ufffd\ufffd\ufffd\u063d\ufffd\ufffd<br>
 * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcUserApiStruct.h:3477</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("Trader") 
public class CThostFtdcReturnResultField extends StructObject {
	static {
		BridJ.register();
	}
	/** C type : TThostFtdcReturnCodeType */
	@Array({7}) 
	@Field(0) 
	public Pointer<Byte > ReturnCode() {
		return this.io.getPointerField(this, 0);
	}
	/** C type : TThostFtdcDescrInfoForReturnCodeType */
	@Array({129}) 
	@Field(1) 
	public Pointer<Byte > DescrInfoForReturnCode() {
		return this.io.getPointerField(this, 1);
	}
	public CThostFtdcReturnResultField() {
		super();
	}
	public CThostFtdcReturnResultField(Pointer pointer) {
		super(pointer);
	}
}
