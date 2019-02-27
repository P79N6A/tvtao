package com.yunos.tv.app.widget;

public class Position {
	int x;
	int y;

	public Position() {

	}

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position(Position p) {
		this.x = p.x();
		this.y = p.y();
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void set(Position p) {
		this.x = p.x();
		this.y = p.y();
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public void offset(int offsetX, int offsetY) {
		x += offsetX;
		y += offsetY;
	}
}

