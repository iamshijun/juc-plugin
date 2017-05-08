package com.kibou.utils.collection;

import java.util.Collection;

public final class CollectionUtils {

	private CollectionUtils(){}
	
	public static boolean isEmpty(Collection<?> collection){
		return collection == null || collection.size() == 0;
	}
	
	public static boolean isNotEmpty(Collection<?> collection){
		return !isEmpty(collection);
	}
}
