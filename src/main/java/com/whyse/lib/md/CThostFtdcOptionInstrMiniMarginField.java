package com.whyse.lib.md;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Array;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * \ufffd\ufffd\u01f0\ufffd\ufffd\u0228\ufffd\ufffd\u053c\ufffd\ufffd\u0421\ufffd\ufffd\u05a4\ufffd\ufffd<br>
 * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcUserApiStruct.h:1327</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("Md") 
public class CThostFtdcOptionInstrMiniMarginField extends StructObject {
	static {
		BridJ.register();
	}
	/** C type : TThostFtdcInstrumentIDType */
	@Array({31}) 
	@Field(0) 
	public Pointer<Byte > InstrumentID() {
		return this.io.getPointerField(this, 0);
	}
	/** C type : TThostFtdcInvestorRangeType */
	@Field(1) 
	public byte InvestorRange() {
		return this.io.getByteField(this, 1);
	}
	/** C type : TThostFtdcInvestorRangeType */
	@Field(1) 
	public CThostFtdcOptionInstrMiniMarginField InvestorRange(byte InvestorRange) {
		this.io.setByteField(this, 1, InvestorRange);
		return this;
	}
	/** C type : TThostFtdcBrokerIDType */
	@Array({11}) 
	@Field(2) 
	public Pointer<Byte > BrokerID() {
		return this.io.getPointerField(this, 2);
	}
	/** C type : TThostFtdcInvestorIDType */
	@Array({13}) 
	@Field(3) 
	public Pointer<Byte > InvestorID() {
		return this.io.getPointerField(this, 3);
	}
	/** C type : TThostFtdcMoneyType */
	@Field(4) 
	public double MinMargin() {
		return this.io.getDoubleField(this, 4);
	}
	/** C type : TThostFtdcMoneyType */
	@Field(4) 
	public CThostFtdcOptionInstrMiniMarginField MinMargin(double MinMargin) {
		this.io.setDoubleField(this, 4, MinMargin);
		return this;
	}
	/** C type : TThostFtdcValueMethodType */
	@Field(5) 
	public byte ValueMethod() {
		return this.io.getByteField(this, 5);
	}
	/** C type : TThostFtdcValueMethodType */
	@Field(5) 
	public CThostFtdcOptionInstrMiniMarginField ValueMethod(byte ValueMethod) {
		this.io.setByteField(this, 5, ValueMethod);
		return this;
	}
	/** C type : TThostFtdcBoolType */
	@Field(6) 
	public int IsRelative() {
		return this.io.getIntField(this, 6);
	}
	/** C type : TThostFtdcBoolType */
	@Field(6) 
	public CThostFtdcOptionInstrMiniMarginField IsRelative(int IsRelative) {
		this.io.setIntField(this, 6, IsRelative);
		return this;
	}
	public CThostFtdcOptionInstrMiniMarginField() {
		super();
	}
	public CThostFtdcOptionInstrMiniMarginField(Pointer pointer) {
		super(pointer);
	}
}
