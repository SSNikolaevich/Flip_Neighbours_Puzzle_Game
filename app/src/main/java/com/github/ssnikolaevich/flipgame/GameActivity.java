package com.github.ssnikolaevich.flipgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.ssnikolaevich.flipgame.game.Game;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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

    private Animation mTileClickAnimation;
    private Animation mNewSizeAccentAnimation;

    private AdView mAdView;

    private int mGameViewWidth;

    private int displaySize;

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

        mTileClickAnimation = AnimationUtils.loadAnimation(this, R.anim.tile_click);
        mNewSizeAccentAnimation = AnimationUtils.loadAnimation(this, R.anim.new_size_accent);

        mGameViewWidth = 0;

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        mAdView = (AdView) findViewById(R.id.adView);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("0818886d")
                .addTestDevice("11611862615")
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);

        // Gets the display size.
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        displaySize = Math.min(p.x, p.y);

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
            if (mGameGrid.isEnabled()) {
                view.startAnimation(mTileClickAnimation);
                game.makeMove(column, row);
                if (game.isOver()) {
                    onGameOver();
                }
            }
        }
    }

    protected void initGameView() {
        final int columnsCount = game.getColumns();
        final int rowsCount = game.getRows();

        mGameGrid.removeAllViews();
        mGameGrid.setColumnCount(columnsCount);
        mGameGrid.setRowCount(rowsCount);
        mGameGrid.setEnabled(true);

        final int maxTileSize = displaySize / Math.max(columnsCount, rowsCount);

        Animator.AnimatorListener tileAnimatorListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mGameGrid.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mGameGrid.setEnabled(true);
            }
        };

        for (int r = 0; r < rowsCount; ++r) {
            for (int c = 0; c < columnsCount; ++c) {
                TileView view = new TileView(this);
                view.setMaxTileSize(maxTileSize);
                view.setTile(game.getTile(c, r));
                view.setOnClickListener(new TileOnClickListener(c, r));
                view.setAnimatorListener(tileAnimatorListener);
                mGameGrid.addView(view);
            }
        }

        game.setOnTileFlipListener(new Game.OnTileFlipListener() {
            @Override
            public void onFlip(Game game, int column, int row) {
                TileView view =
                        (TileView) mGameGrid.getChildAt(game.getColumns() * row + column);
                view.flip();
            }
        });

        if (mGameViewWidth == 0) {
            mGameView.addOnLayoutChangeListener(
                    new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(
                                View view,
                                int left, int top, int right, int bottom,
                                int oldLeft, int oldTop, int oldRight, int oldBottom
                        ) {
                            if (mGameViewWidth == 0)
                                mGameViewWidth = right - left;
                            if (mGameViewWidth > 0) {
                                resizeTiles();
                                view.removeOnLayoutChangeListener(this);
                            }
                        }
                    }
            );
        } else {
            resizeTiles();
        }
    }

    private void resizeTiles() {
        int tileSize = mGameViewWidth / mGameGrid.getColumnCount();
        for (int i = 0; i < mGameGrid.getChildCount(); ++i) {
            View child = mGameGrid.getChildAt(i);
            GridLayout.LayoutParams params =
                    (GridLayout.LayoutParams) child.getLayoutParams();
            params.width = tileSize;
            params.height = tileSize;
            child.setLayoutParams(params);
        }
    }

    public void startSameSizeGame(View view) {
        animateNewGame();
        resetGame(game.getColumns(), game.getRows());
    }

    public void startNextSizeGame(View view) {
        animateNewGame();
        resetGame(game.getColumns() + 1, game.getRows() + 1);
    }

    public void rateThisApp(View view) {
        try
        {
            startActivity(
                new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())
                )
            );
        } catch (ActivityNotFoundException e) {
            String message = getResources().getString(R.string.no_play_store_installed);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
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

    public void resetGame(final int columnsCount, final int rowsCount) {
        mGameGrid.animate()
                .alpha(0.0f)
                .rotation(45.0f)
                .scaleX(0.0f)
                .scaleY(0.0f)
                .setDuration(250)
                .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                initGame(columnsCount, rowsCount);
                                initGameView();
                                mGameGrid.setRotation(-45.0f);
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
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mNextSizeTextView.startAnimation(mNewSizeAccentAnimation);
                    }
                });
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

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        game = new Game(savedInstanceState);
        initGameView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        game.saveState(savedInstanceState);

        super.onSaveInstanceState(savedInstanceState);
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
