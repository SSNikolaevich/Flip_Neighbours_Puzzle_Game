package com.github.ssnikolaevich.flipgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.github.ssnikolaevich.flipgame.game.Game;

public class GameActivity extends Activity {
    public final static String EXTRA_COLUMNS_COUNT = "com.github.ssnikolaevich.flipgame.COLUMNS_COUNT";
    public final static String EXTRA_ROWS_COUNT = "com.github.ssnikolaevich.flipgame.ROWS_COUNT";

    public final int DEFAULT_COLUMNS = 4;
    public final int DEFAULT_ROWS = 4;

    private Game game;

    private View mGameView;
    private View mEndGameView;
    private View mHelpView;
    private GridLayout mGameGrid;

    private TextView mSameSizeTextView;
    private TextView mNextSizeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mGameView = findViewById(R.id.gameView);

        mEndGameView = findViewById(R.id.endGameView);
        mEndGameView.setVisibility(View.GONE);

        mHelpView = findViewById(R.id.helpView);
        mHelpView.setVisibility(View.GONE);

        mGameGrid = (GridLayout) findViewById(R.id.gameGrid);

        mSameSizeTextView = (TextView) findViewById(R.id.sameSizeTextView);
        mNextSizeTextView = (TextView) findViewById(R.id.nextSizeTextView);

        init();
    }

    protected void init() {
        Intent intent = getIntent();
        final int columnsCount = intent.getIntExtra(EXTRA_COLUMNS_COUNT, DEFAULT_COLUMNS);
        final int rowsCount = intent.getIntExtra(EXTRA_ROWS_COUNT, DEFAULT_ROWS);

        initGame(columnsCount, rowsCount);
        initGameView();
    }

    protected void initGame(int columnsCount, int rowsCount) {
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
            if (game.isOver()) {
                onGameOver();
            }
        }
    }

    protected void initGameView() {
        final int columnsCount = game.getColumns();
        final int rowsCount = game.getRows();

        mGameGrid.removeAllViews();
        mGameGrid.setColumnCount(columnsCount);
        mGameGrid.setRowCount(rowsCount);

        TileViewFactory tileViewFactory = new TileViewFactory(this);
        for (int r = 0; r < rowsCount; ++r) {
            for (int c = 0; c < columnsCount; ++c) {
                View view = tileViewFactory.create(game.getTile(c, r));
                view.setOnClickListener(new TileOnClickListener(c, r));
                mGameGrid.addView(view);
            }
        }

        game.setOnTileFlipListener(new Game.OnTileFlipListener() {
            @Override
            public void onFlip(Game game, int column, int row) {
                ViewFlipper view =
                        (ViewFlipper) mGameGrid.getChildAt(game.getColumns() * row + column);
                view.showNext();
            }
        });

        mGameView.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(
                            View view,
                            int left, int top, int right, int bottom,
                            int oldLeft, int oldTop, int oldRight, int oldBottom
                    ) {
                        int tileSize = (right - left) / columnsCount;

                        for (int i = 0; i < mGameGrid.getChildCount(); ++i) {
                            View child = mGameGrid.getChildAt(i);
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

    public void startSameSizeGame(View view) {
        animateNewGame();
        resetGame(game.getColumns(), game.getRows());
    }

    public void startNextSizeGame(View view) {
        animateNewGame();
        resetGame(game.getColumns() + 1, game.getRows() + 1);
    }

    private void animateNewGame() {
        mEndGameView.animate()
                .alpha(0f)
                .scaleX(2.0f)
                .scaleY(2.0f)
                .rotation(-180.0f)
                .setDuration(250)
                .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mEndGameView.setRotation(0.0f);
                                mEndGameView.setVisibility(View.GONE);
                            }
                        }
                );
    }

    public void onResetGame(View view) {
        Intent intent = getIntent();
        final int columnsCount = intent.getIntExtra(EXTRA_COLUMNS_COUNT, DEFAULT_COLUMNS);
        final int rowsCount = intent.getIntExtra(EXTRA_ROWS_COUNT, DEFAULT_ROWS);

        resetGame(columnsCount, rowsCount);
    }

    public void resetGame(int columnsCount, int rowsCount) {
        mGameGrid.animate()
                .alpha(0.0f)
                .rotation(90.0f)
                .scaleX(0.25f)
                .scaleY(0.25f)
                .setDuration(250)
                .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mGameGrid.setRotation(-90.0f);
                                mGameGrid.animate()
                                        .alpha(1.0f)
                                        .rotation(0.0f)
                                        .scaleX(1.0f)
                                        .scaleY(1.0f)
                                        .setDuration(250)
                                        .setListener(null);
                            }
                        }
                );
        initGame(columnsCount, rowsCount);
        initGameView();
    }

    protected void onGameOver() {
        final int columnsCount = game.getColumns();
        final int rowsCount = game.getRows();
        mSameSizeTextView.setText(columnsCount + " x " + rowsCount);
        mNextSizeTextView.setText((columnsCount + 1) + " x " + (rowsCount + 1));

        mEndGameView.setAlpha(0f);
        mEndGameView.setScaleX(2.0f);
        mEndGameView.setScaleY(2.0f);
        mEndGameView.setVisibility(View.VISIBLE);

        mEndGameView.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(250)
                .setListener(null);
    }

    public void showHelp(View view) {
        mHelpView.setAlpha(0f);
        mHelpView.setScaleX(2.0f);
        mHelpView.setScaleY(2.0f);
        mHelpView.setVisibility(View.VISIBLE);

        mHelpView.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(250)
                .setListener(null);

        mGameView.animate()
                .alpha(0f)
                .scaleX(0.5f)
                .scaleY(0.5f)
                .setDuration(250)
                .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mGameView.setVisibility(View.GONE);
                            }
                        }
                );
    }

    public void closeHelp(View view) {
        mGameView.setAlpha(0f);
        mGameView.setScaleX(0.5f);
        mGameView.setScaleY(0.5f);
        mGameView.setVisibility(View.VISIBLE);

        mGameView.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(250)
                .setListener(null);

        mHelpView.animate()
                .alpha(0f)
                .scaleX(2.0f)
                .scaleY(2.0f)
                .setDuration(250)
                .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mHelpView.setVisibility(View.GONE);
                            }
                        }
                );
    }

    public void onShare(View view) {
        shareIt();
    }

    protected void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String msgSubject = getString(R.string.share_msg_subject);
        String msgText = getString(R.string.share_msg_text);
        String shareCaption = getString(R.string.share_via);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, msgSubject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, msgText);
        startActivity(Intent.createChooser(sharingIntent, shareCaption));
    }
}
