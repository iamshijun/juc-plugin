package com.kibou.tuple;

import java.io.Serializable;

public class Quad<A, B, C, D> implements Serializable {
	private static final long serialVersionUID = 1L;

	private A first;
	private B second;
	private C third;
	private D fourth;

	public Quad(A first, B second, C third, D fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
	}
	
	public static <A,B,C,D> Quad<A,B,C,D> of(A first, B second, C third, D fourth) {
		return new Quad<A, B, C, D>(first, second, third, fourth);
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

	public D getFourth() {
		return fourth;
	}

	public void setFourth(D fourth) {
		this.fourth = fourth;
	}

	@Override
	public String toString() {
		return "{" + first + "," + second + "," + third + "," + fourth + "}";
	}
}
