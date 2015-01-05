package com.github.ssnikolaevich.flipgame.game;

public class Tile {
    public static final int FRONT = 0;
    public static final int BACK = 1;

    private Value mFront;
    private Value mBack;
    private int mSide;

    Tile() {
        mFront = new Value();
        mBack = new Value();
        mSide = FRONT;
    }

    public Value getFront() {
        return mFront;
    }

    public Value getBack() {
        return mBack;
    }

    public void setFront(Value value) {
        mFront = value;
    }

    public void setBack(Value value) {
        mBack = value;
    }

    public int getVisibleSide() {
        return mSide;
    }

    public void setVisibleSide(int side) {
        mSide = side;
    }

    public Value get(int side) {
        return (side == FRONT) ? mFront : mBack;
    }

    public Value getVisibleValue() {
        return get(getVisibleSide());
    }

    public void set(int side, Value value) {
        if (side == FRONT) {
            mFront = value;
        } else {
            mBack = value;
        }
    }

    public void flip() {
        mSide = (mSide == FRONT) ? BACK : FRONT;
    }
}

