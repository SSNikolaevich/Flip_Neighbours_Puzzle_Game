package com.github.ssnikolaevich.flipgame.game;

import java.util.*;
import android.os.Bundle;
import android.util.Size;

public class Game {
    static final String STATE_DATA = "gameData";

    private Layout mLayout;
    private OnTileFlipListener onTileFlipListener;

    public Game(int columns, int rows) {
        mLayout = new Layout(columns, rows);
        onTileFlipListener = null;
        initLayout();
    }

    public Game(Bundle savedState) {
        ArrayList<Integer> data = savedState.getIntegerArrayList(STATE_DATA);
        Iterator<Integer> iter = data.iterator();
        int columns = iter.next().intValue();
        int rows = iter.next().intValue();
        mLayout = new Layout(columns, rows);
        onTileFlipListener = null;
        loadLayout(iter);
    }

    private void initLayout() {
        int columns = mLayout.getColumns();
        int rows = mLayout.getRows();
        Random random = new Random();
        for (int c = 0; c < columns; ++c) {
            for (int r = 0; r < rows; ++r) {
                Tile tile = mLayout.getTile(c, r);
                tile.setFront(getRandomTileValue(random));
                tile.setBack(getRandomTileValue(random));
            }
        }
        int flipsCount = columns * rows;
        for (int i = 0; (i < flipsCount) || isOver(); ++i) {
            int column = random.nextInt(columns);
            int row = random.nextInt(rows);
            makeMove(column, row);
        }
    }

    private void loadLayout(Iterator<Integer> iter) {
        int columns = mLayout.getColumns();
        int rows = mLayout.getRows();
        for (int c = 0; c < columns; ++c) {
            for (int r = 0; r < rows; ++r) {
                Tile tile = mLayout.getTile(c, r);
                tile.setFront(new Value(iter.next().intValue()));
                tile.setBack(new Value(iter.next().intValue()));
                tile.setVisibleSide(iter.next().intValue());
            }
        }
    }

    private static Value getRandomTileValue(Random random) {
        Value value = new Value();
        if (random.nextFloat() < 0.2f) {
            value.setOther(true);
        } else {
            while (value.empty()) {
                value.setLeft(random.nextFloat() < 0.25f);
                value.setRight(random.nextFloat() < 0.25f);
                value.setTop(random.nextFloat() < 0.25f);
                value.setBottom(random.nextFloat() < 0.25f);
            }
        }
        return value;
    }

    public boolean isOver() {
        int columns = mLayout.getColumns();
        int rows = mLayout.getRows();
        for (int c = 0; c < columns; ++c) {
            for (int r = 0; r < rows; ++r) {
                if (mLayout.getTile(c, r).getVisibleSide() != Tile.FRONT)
                    return false;
            }
        }
        return true;
    }

    public int getColumns() {
        return mLayout.getColumns();
    }

    public int getRows() {
        return mLayout.getRows();
    }

    public Tile getTile(int column, int row) {
        return mLayout.getTile(column, row);
    }

    private void flipTile(int column, int row) {
        Tile tile = mLayout.getTile(column, row);
        tile.flip();
        if (onTileFlipListener != null)
            onTileFlipListener.onFlip(this, column, row);
    }

    public void makeMove(int column, int row) {
        Tile tile = mLayout.getTile(column, row);
        Value value = tile.getVisibleValue();
        int columns = mLayout.getColumns();
        int rows = mLayout.getRows();
        if (value.isOther()) {
            for (int c = 0; c < columns; ++c) {
                for (int r = 0; r < rows; ++r) {
                    if ((c != column) && (r != row)) {
                        flipTile(c, r);
                    }
                }
            }
        } else {
            if (value.isLeft()) {
                int c = ((column > 0)? column : columns) - 1;
                flipTile(c, row);
            }
            if (value.isRight()) {
                int c = ((column + 1) < columns)? (column + 1) : 0;
                flipTile(c, row);
            }
            if (value.isTop()) {
                int r = ((row > 0)? row : rows) - 1;
                flipTile(column, r);
            }
            if (value.isBottom()) {
                int r = ((row + 1) < rows)? (row + 1) : 0;
                flipTile(column, r);
            }
        }
    }

    public void setOnTileFlipListener(OnTileFlipListener listener) {
        onTileFlipListener = listener;
    }

    public interface OnTileFlipListener {
        public void onFlip(Game game, int column, int row);
    }

    public void saveState(Bundle savedState) {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(Integer.valueOf(getColumns()));
        data.add(Integer.valueOf(getRows()));
        saveLayoutData(data);
        savedState.putIntegerArrayList(STATE_DATA, data);
    }

    private void saveLayoutData(ArrayList<Integer> data) {
        int columns = mLayout.getColumns();
        int rows = mLayout.getRows();
        for (int c = 0; c < columns; ++c) {
            for (int r = 0; r < rows; ++r) {
                Tile tile = mLayout.getTile(c, r);
                data.add(Integer.valueOf(tile.getFront().asInt()));
                data.add(Integer.valueOf(tile.getBack().asInt()));
                data.add(Integer.valueOf(tile.getVisibleSide()));
            }
        }
    }
}
