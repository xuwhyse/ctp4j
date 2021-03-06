package com.whyse.lib.md;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Array;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * \ufffd\ufffd\ufffd\ufffd\u036c\ufffd\ufffd\ufffd\u0435\u013a\ufffd\u053c\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
 * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcUserApiStruct.h:1103</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("Md") 
public class CThostFtdcSyncingInstrumentCommissionRateField extends StructObject {
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
	public CThostFtdcSyncingInstrumentCommissionRateField InvestorRange(byte InvestorRange) {
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
	/** C type : TThostFtdcRatioType */
	@Field(4) 
	public double OpenRatioByMoney() {
		return this.io.getDoubleField(this, 4);
	}
	/** C type : TThostFtdcRatioType */
	@Field(4) 
	public CThostFtdcSyncingInstrumentCommissionRateField OpenRatioByMoney(double OpenRatioByMoney) {
		this.io.setDoubleField(this, 4, OpenRatioByMoney);
		return this;
	}
	/** C type : TThostFtdcRatioType */
	@Field(5) 
	public double OpenRatioByVolume() {
		return this.io.getDoubleField(this, 5);
	}
	/** C type : TThostFtdcRatioType */
	@Field(5) 
	public CThostFtdcSyncingInstrumentCommissionRateField OpenRatioByVolume(double OpenRatioByVolume) {
		this.io.setDoubleField(this, 5, OpenRatioByVolume);
		return this;
	}
	/** C type : TThostFtdcRatioType */
	@Field(6) 
	public double CloseRatioByMoney() {
		return this.io.getDoubleField(this, 6);
	}
	/** C type : TThostFtdcRatioType */
	@Field(6) 
	public CThostFtdcSyncingInstrumentCommissionRateField CloseRatioByMoney(double CloseRatioByMoney) {
		this.io.setDoubleField(this, 6, CloseRatioByMoney);
		return this;
	}
	/** C type : TThostFtdcRatioType */
	@Field(7) 
	public double CloseRatioByVolume() {
		return this.io.getDoubleField(this, 7);
	}
	/** C type : TThostFtdcRatioType */
	@Field(7) 
	public CThostFtdcSyncingInstrumentCommissionRateField CloseRatioByVolume(double CloseRatioByVolume) {
		this.io.setDoubleField(this, 7, CloseRatioByVolume);
		return this;
	}
	/** C type : TThostFtdcRatioType */
	@Field(8) 
	public double CloseTodayRatioByMoney() {
		return this.io.getDoubleField(this, 8);
	}
	/** C type : TThostFtdcRatioType */
	@Field(8) 
	public CThostFtdcSyncingInstrumentCommissionRateField CloseTodayRatioByMoney(double CloseTodayRatioByMoney) {
		this.io.setDoubleField(this, 8, CloseTodayRatioByMoney);
		return this;
	}
	/** C type : TThostFtdcRatioType */
	@Field(9) 
	public double CloseTodayRatioByVolume() {
		return this.io.getDoubleField(this, 9);
	}
	/** C type : TThostFtdcRatioType */
	@Field(9) 
	public CThostFtdcSyncingInstrumentCommissionRateField CloseTodayRatioByVolume(double CloseTodayRatioByVolume) {
		this.io.setDoubleField(this, 9, CloseTodayRatioByVolume);
		return this;
	}
	public CThostFtdcSyncingInstrumentCommissionRateField() {
		super();
	}
	public CThostFtdcSyncingInstrumentCommissionRateField(Pointer pointer) {
		super(pointer);
	}
}
