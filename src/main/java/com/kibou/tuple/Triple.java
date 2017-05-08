package com.kibou.tuple;

public class Triple<A, B, C> {
	private A first;
	private B second;
	private C third;

	public Triple(A first, B second, C third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public static <A, B, C> Triple<A, B, C> of(A first, B second, C third) {
		return new Triple<A, B, C>(first, second, third);
	}

	public int hashCode() {
		int hashFirst = (first != null ? first.hashCode() : 0);
		int hashSecond = (second != null ? second.hashCode() : 0);
		int hashThird = (third != null ? third.hashCode() : 0);

		return (hashFirst >> 1) ^ hashSecond ^ (hashThird << 1);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Triple)) {
			return false;
		}

		Triple<?, ?, ?> otherTriple = (Triple<?, ?, ?>) obj;

		if (first != otherTriple.first && (first != null && !(first.equals(otherTriple.first))))
			return false;
		if (second != otherTriple.second && (second != null && !(second.equals(otherTriple.second))))
			return false;
		if (third != otherTriple.third && (third != null && !(third.equals(otherTriple.third))))
			return false;

		return true;
	}

	public String toString() {
		return "{" + first + ", " + second + "," + third + " }";
	}

	public A getFirst() {
		return first;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public B getSecond() {
		return second;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	public C getThird() {
		return third;
	}

	public void setThird(C third) {
		this.third = third;
	}
}