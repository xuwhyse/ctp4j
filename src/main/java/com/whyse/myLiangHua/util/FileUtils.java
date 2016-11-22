package com.whyse.myLiangHua.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {

	/**
	 * @param args
	 *            author:xumin 2016-11-22 下午8:39:56
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void writeToFileAll(String str, String path) {
		try {
			File file = new File(path);
			if (!file.exists()){
//				file.mkdirs();
				file.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(file, false); // 如果追加方式用true
			out.write(str.getBytes("utf-8"));// 注意需要转换对应的字符集
			out.close();
		} catch (IOException ex) {
			System.out.println(ex.getStackTrace());
		}
	}

	public static String readFileAll(String path) {
		StringBuffer sb = new StringBuffer();
		String tempstr = null;
		try {
			File file = new File(path);
			if (!file.exists())
				throw new FileNotFoundException();
			// BufferedReader br=new BufferedReader(new FileReader(file));
			// while((tempstr=br.readLine())!=null)
			// sb.append(tempstr);
			// 另一种读取方式
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			while ((tempstr = br.readLine()) != null)
				sb.append(tempstr);
		} catch (IOException ex) {
			System.out.println(ex.getStackTrace());
		}
		return sb.toString();
	}

}
