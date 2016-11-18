package com.whyse.main.util;

import java.lang.reflect.Field;
import java.util.Map;



public class BeanUtil {

	/**
	 * xumin  2015-7-15 下午4:00:17
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.err.println(System.getProperty("user.dir"));
	}
	public static void printOb(Object ob) {
		if(ob==null)
			return;
		System.err.println(ob.getClass().getName());
		Field[] fieldlist = ob.getClass().getDeclaredFields();
        for (int i = 0; i < fieldlist.length; i++) {
            Field field = fieldlist[i];
            field.setAccessible(true);//获取私有属性
            try {
				Object value = field.get(ob);
				System.err.println(field.getName()+" : "+value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
//            if(isNullIn || value!=null )
//            	map.put(field.getName(), value);
         }
	}
	/**
	 * 将ob中属性添加到map
	 * xumin  2015-7-15 下午4:13:36
	 * @param map
	 * @param ob
	 * @param isNullIn true:把空的字段也插入进去，反正就不是
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void addToMap(Map<String, Object> map, Object ob, boolean isNullIn) throws IllegalArgumentException, IllegalAccessException {
		Field[] fieldlist = ob.getClass().getDeclaredFields();
        for (int i = 0; i < fieldlist.length; i++) {
            Field field = fieldlist[i];
            field.setAccessible(true);//获取私有属性
            Object value = field.get(ob);
            if(isNullIn || value!=null )
            	System.err.println(field.getName()+": "+value);
            	map.put(field.getName(), value);
         }
	}

}
