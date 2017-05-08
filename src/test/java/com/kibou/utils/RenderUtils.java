package com.kibou.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class RenderUtils {
	
	public static void printCollectionWithMultiline(List<?> results, OutputStream os){
		StringBuilder sb = new StringBuilder("List : [\n");
		for(Object result : results){
			sb.append("\t").append(result).append("\n");
		}
		sb.append("] size=").append(results.size());
		try {
			os.write(sb.toString().getBytes("utf-8"));
			os.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public static void printCollectionWithMultiline(List<?> results){
		printCollectionWithMultiline(results,System.out);
	}
	
	public static <T> void printMapWithMultiline(Map<?,?> results, OutputStream os){
		StringBuilder sb = new StringBuilder("Map : {\n");
		for(Entry<?, ?> result : results.entrySet()){
			sb.append("\t").append(result.getKey()).append("=").append(result.getValue()).append(",\n");
		}
		sb.append("}");
		try {
			os.write(sb.toString().getBytes("utf-8"));
			os.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static <T> void printMapWithMultiline(Map<?,?> results){
		printMapWithMultiline(results,System.out);
	}
	
}
