package com.whyse.main.trader;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bridj.Pointer;


public class BridjUtils {
	public static Map<String, Object> mapTar;
	public static List<Map<String, Object>> listMap;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.err.println("");
	}
	/**
	 * 打印返回的值
	 * @param aa
	 * author:xumin 
	 * 2016-4-29 下午3:27:58
	 */
	public static Map<String, Object> printObFields(Pointer ob) {
		
		if(ob==null){
			System.err.println("null");
			return null;
		}
		if(ob!=null)
			return null;
		mapTar = new HashMap<String, Object>(20);
		Object aa = ob.get();
		System.err.println("======="+aa.getClass().getName()+"======");
		Method[] fieldlist = aa.getClass().getDeclaredMethods();
		for (int i = 0; i < fieldlist.length; i++) {
			Method field = fieldlist[i];
			String name = field.getName();
			System.err.print(name + " : ");
			Class<?> returnType = field.getReturnType();
			try {
				if (returnType.equals(Pointer.class)) {
					Pointer value = (Pointer) field.invoke(aa, null);
					String str = value.getCString();//
					mapTar.put(name, str);
					if (str != null && str.length() > 0)
						System.err.print(str);
					else
						System.err.print("point null");
				} else if (returnType.equals(int.class)) {
					Object value = field.invoke(aa, null);
					mapTar.put(name, value);
					System.err.print(value);
				} else if (returnType.equals(double.class)) {
					Object value = field.invoke(aa, null);
					mapTar.put(name, value);
					System.err.print(value);
				} else if (returnType.equals(long.class)) {
					Object value = field.invoke(aa, null);
					mapTar.put(name, value);
					System.err.print(value);
				} else if (returnType.equals(Float.class)) {
					Object value = field.invoke(aa, null);
					mapTar.put(name, value);
					System.err.print(value);
				}
				else{
					if(field.getParameterTypes().length>0)
						continue;
					Object value = field.invoke(aa, null);
					if(value!=null)
						mapTar.put(name, value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.err.println("");
		}
		System.err.println("==============================================");
		return mapTar;
	}
	public static Pointer<Byte> stringToBytePointer(String str) {
		Pointer<Byte> bytePointer = Pointer.allocateBytes(str.length()+1);//str.length() + 1
		bytePointer.setCString(str);
		return bytePointer;
	}
	/**
	 * 将指针里的数据map返回
	 * @param ob
	 * @return
	 * author:xumin 
	 * 2016-6-2 下午5:27:01
	 */
	public static Map<String, Object> getMapByPoint(Pointer ob) {
		if(ob==null){
			return null;
		}
		Map<String, Object> mapTar = new HashMap<String, Object>(20);
		Object aa = ob.get();
		Method[] fieldlist = aa.getClass().getDeclaredMethods();
		for (int i = 0; i < fieldlist.length; i++) {
			Method field = fieldlist[i];
			String name = field.getName();
			Class<?> returnType = field.getReturnType();
			try {
				if (returnType.equals(Pointer.class)) {
					Pointer value = (Pointer) field.invoke(aa, null);
					String str = value.getCString();//
					if(str!=null)
						mapTar.put(name, str);
				} else if (returnType.equals(int.class)) {
					Object value = field.invoke(aa, null);
					mapTar.put(name, value);
				} else if (returnType.equals(double.class)) {
					Object value = field.invoke(aa, null);
					mapTar.put(name, value);
				} else if (returnType.equals(long.class)) {
					Object value = field.invoke(aa, null);
					mapTar.put(name, value);
				} else if (returnType.equals(Float.class)) {
					Object value = field.invoke(aa, null);
					mapTar.put(name, value);
				}else{
					if(field.getParameterTypes().length>0)
						continue;
					Object value = field.invoke(aa, null);
					if(value!=null)
						mapTar.put(name, value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mapTar;
	}
	/**
	 * 把字符窜数组转化成指针
	 * @param listInstrumentID
	 * @return
	 * author:xumin 
	 * 2016-9-29 下午2:26:31
	 */
	public static Pointer<Pointer<Byte>> getPPBylist(
			List<String> listInstrumentID) {
		 Pointer<Pointer<Byte>> ppInstrumentID = Pointer.allocatePointers(Byte.class,listInstrumentID.size());//创建一个有长度的指针数组
		 for(int i=0;i<listInstrumentID.size();i++){
			 String str = listInstrumentID.get(i);
			 Pointer<Byte> bytePointer = Pointer.allocateBytes(str.length()+1);//str.length() + 1
			 bytePointer.setCString(str);
			 ppInstrumentID.setPointerAtIndex(i, bytePointer);
			 
//			 ppInstrumentID.asList().set(i, bytePointer);
		 }
		return ppInstrumentID;
	}
	
}
