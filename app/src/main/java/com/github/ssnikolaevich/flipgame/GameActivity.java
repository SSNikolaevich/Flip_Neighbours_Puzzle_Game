package com.github.ssnikolaevich.flipgame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import android.graphics.Color;


public class GameActivity extends Activity {
    public final static String EXTRA_COLUMNS_COUNT = "com.github.ssnikolaevich.flipgame.COLUMNS_COUNT";
    public final static String EXTRA_ROWS_COUNT = "com.github.ssnikolaevich.flipgame.ROWS_COUNT";

    public final int DEFAULT_COLUMNS = 5;
    public final int DEFAULT_ROWS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        init();
    }

    protected void init() {
        Intent intent = getIntent();
        final int columnsCount = intent.getIntExtra(EXTRA_COLUMNS_COUNT, DEFAULT_COLUMNS);
        final int rowsCount = intent.getIntExtra(EXTRA_ROWS_COUNT, DEFAULT_ROWS);

        final GridLayout gridLayout = (GridLayout) findViewById(R.id.gameGrid);
        gridLayout.setColumnCount(columnsCount);
        gridLayout.setRowCount(rowsCount);

        TileViewFactory tileViewFactory = new TileViewFactory(this);
        for (int c = 0; c < columnsCount; ++c) {
            for (int r = 0; r < rowsCount; ++r) {
                View view = tileViewFactory.create();
                gridLayout.addView(view);
            }
        }

        RelativeLayout gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
        gameLayout.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View view, int l, int t, int r, int b, int ol, int ot, int or, int ob) {
                        int tileSize = (r - l) / columnsCount;

                        for (int i = 0; i < gridLayout.getChildCount(); ++i) {
                            View child = gridLayout.getChildAt(i);
                            GridLayout.LayoutParams params = (GridLayout.LayoutParams) child.getLayoutParams();
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
