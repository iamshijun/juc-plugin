package com.kibou.utils.function;

public interface Predicate<T> {
	boolean apply(T input);

	boolean equals(Object object);
}
