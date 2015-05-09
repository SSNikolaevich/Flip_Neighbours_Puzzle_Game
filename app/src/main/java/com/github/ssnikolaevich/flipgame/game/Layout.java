package com.github.ssnikolaevich.flipgame.game;

import java.util.*;

public class Layout {
    private int mColumns;
    private int mRows;
    private List<Tile> mTiles;

    public Layout(int columns, int rows) {
        mColumns = columns;
        mRows = rows;
        mTiles = new ArrayList<>();
        for (int c = 0; c < mColumns; ++c) {
            for (int r = 0; r < mRows; ++r) {
                mTiles.add(new Tile());
            }
        }
    }

    public int getColumns() {
        return mColumns;
    }

    public int getRows() {
        return mRows;
    }

    public Tile getTile(int column, int row) {
        return mTiles.get(row * mColumns + column);
    }
}

