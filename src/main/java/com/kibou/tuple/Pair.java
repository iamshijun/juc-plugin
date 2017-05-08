package com.kibou.tuple;

public class Pair<T1, T2> {
	public final T1 first;
	public final T2 second;

	public Pair(T1 t1, T2 t2) {
		first = t1;
		second = t2;
	}

	public T1 getFirst() {
		return first;
	}

	public T2 getSecond() {
		return second;
	}

	public static <T1, T2> Pair<T1, T2> of(T1 t1, T2 t2) {
		return new Pair<T1, T2>(t1, t2);
	}

	private static boolean equals(Object x, Object y) {
		return (x == null && y == null) || (x != null && x.equals(y));
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		return other instanceof Pair && equals(first, ((Pair<T1, T2>) other).first)
				&& equals(second, ((Pair<T1, T2>) other).second);
	}

	@Override
	public int hashCode() {
		int hashFirst = (first != null ? first.hashCode() : 0);
		int hashSecond = (second != null ? second.hashCode() : 0);

		return (hashFirst >> 1) ^ (hashSecond << 1);
	}

	@Override
	public String toString() {
		return "{" + getFirst() + "," + getSecond() + "}";
	}
}