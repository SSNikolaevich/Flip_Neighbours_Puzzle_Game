package com.github.ssnikolaevich.flipgame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.github.ssnikolaevich.flipgame.game.Game;
import com.github.ssnikolaevich.flipgame.game.Tile;

public class GameActivity extends Activity {
    public final static String EXTRA_COLUMNS_COUNT = "com.github.ssnikolaevich.flipgame.COLUMNS_COUNT";
    public final static String EXTRA_ROWS_COUNT = "com.github.ssnikolaevich.flipgame.ROWS_COUNT";

    public final int DEFAULT_COLUMNS = 4;
    public final int DEFAULT_ROWS = 4;

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        init();
    }

    protected void init() {
        initGame();
        initGameView();
    }

    protected void initGame() {
        Intent intent = getIntent();
        final int columnsCount = intent.getIntExtra(EXTRA_COLUMNS_COUNT, DEFAULT_COLUMNS);
        final int rowsCount = intent.getIntExtra(EXTRA_ROWS_COUNT, DEFAULT_ROWS);

        game = new Game(columnsCount, rowsCount);
    }

    private class TileOnClickListener implements View.OnClickListener {
        private int column;
        private int row;

        public TileOnClickListener(int column, int row) {
            this.column = column;
            this.row = row;
        }

        @Override
        public void onClick(View view) {
            game.makeMove(column, row);
        }
    }

    protected void initGameView() {
        final int columnsCount = game.getColumns();
        final int rowsCount = game.getRows();

        final GridLayout gridLayout = (GridLayout) findViewById(R.id.gameGrid);
        gridLayout.setColumnCount(columnsCount);
        gridLayout.setRowCount(rowsCount);
        gridLayout.removeAllViews();

        TileViewFactory tileViewFactory = new TileViewFactory(this);
        for (int r = 0; r < rowsCount; ++r) {
            for (int c = 0; c < columnsCount; ++c) {
                View view = tileViewFactory.create(game.getTile(c, r));
                view.setOnClickListener(new TileOnClickListener(c, r));
                gridLayout.addView(view);
            }
        }

        game.setOnTileFlipListener(new Game.OnTileFlipListener() {
            @Override
            public void onFlip(Game game, int column, int row) {
                ViewFlipper view =
                        (ViewFlipper) gridLayout.getChildAt(game.getColumns() * row + column);
                view.showNext();
            }
        });

        RelativeLayout gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
        gameLayout.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(
                            View view,
                            int left, int top, int right, int bottom,
                            int oldLeft, int oldTop, int oldRight, int oldBottom
                    ) {
                        int tileSize = (right - left) / columnsCount;

                        for (int i = 0; i < gridLayout.getChildCount(); ++i) {
                            ViewFlipper child = (ViewFlipper) gridLayout.getChildAt(i);
                            GridLayout.LayoutParams params =
                                    (GridLayout.LayoutParams) child.getLayoutParams();
                            params.width = tileSize;
                            params.height = tileSize;
                            child.setLayoutParams(params);
                        }
                        Log.d("GameActivity", "Tile size: " + tileSize);
                        if (tileSize > 0)
                            view.removeOnLayoutChangeListener(this);
                    }
                }
        );
    }

    public void resetGame(View view) {
        initGame();
        initGameView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
