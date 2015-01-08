package com.github.ssnikolaevich.flipgame.game;

public class Value {
    private boolean mLeft;
    private boolean mRight;
    private boolean mTop;
    private boolean mBottom;
    private boolean mOther;

    public Value() {
        mLeft = false;
        mRight = false;
        mTop = false;
        mBottom = false;
        mOther = false;
    }

    public Value(int v) {
        mOther = (v & 1) != 0;
        mLeft = (v & 2) != 0;
        mRight = (v & 4) != 0;
        mTop = (v & 8) != 0;
        mBottom = (v & 16) != 0;
    }

    public int asInt() {
        int v = 0;
        if (mOther)
            v |= 1;
        if (mLeft)
            v |= 2;
        if (mRight)
            v |= 4;
        if (mTop)
            v |= 8;
        if (mBottom)
            v |= 16;
        return v;
    }

    public boolean isLeft() {
        return mLeft;
    }

    public boolean isRight() {
        return mRight;
    }

    public boolean isTop() {
        return mTop;
    }

    public boolean isBottom() {
        return mBottom;
    }

    public boolean isOther() {
        return mOther;
    }

    public boolean empty() { return !(mOther || mTop || mBottom || mLeft || mRight); }

    public void setLeft(boolean left) {
        mLeft = left;
        if (left) {
            mOther = false;
        }
    }

    public void setRight(boolean right) {
        mRight = right;
        if (right) {
            mOther = false;
        }
    }

    public void setTop(boolean top) {
        mTop = top;
        if (top) {
            mOther = false;
        }
    }

    public void setBottom(boolean bottom) {
        mBottom = bottom;
        if (bottom) {
            mOther = false;
        }
    }

    public void setOther(boolean other) {
        mOther = other;
        if (other) {
            mLeft = false;
            mRight = false;
            mTop = false;
            mBottom = false;
        }
    }
}
