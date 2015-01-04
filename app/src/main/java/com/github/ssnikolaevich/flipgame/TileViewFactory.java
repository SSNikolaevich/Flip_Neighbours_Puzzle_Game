package com.github.ssnikolaevich.flipgame;

import android.view.View;
import android.content.Context;
import android.widget.ViewFlipper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.github.ssnikolaevich.flipgame.game.Tile;

public class TileViewFactory {
    Context context;
    Animation animationFlipIn;
    Animation animationFlipOut;

    public TileViewFactory(Context context) {
        this.context = context;
        animationFlipIn = AnimationUtils.loadAnimation(context, R.anim.flipin);
        animationFlipOut = AnimationUtils.loadAnimation(context, R.anim.flipout);
    }

    public View create(Tile tile) {
        final ViewFlipper viewFlipper = new ViewFlipper(context);
        viewFlipper.setInAnimation(animationFlipIn);
        viewFlipper.setOutAnimation(animationFlipOut);
        TileSideView front = new TileSideView(context);
        TileSideView back = new TileSideView(context);

        front.setValue(tile.get(Tile.FRONT));
        front.setFront(true);

        back.setValue(tile.get(Tile.BACK));
        back.setFront(false);

        viewFlipper.addView(front);
        viewFlipper.addView(back);

        if (tile.getVisibleSide() == Tile.BACK)
            viewFlipper.showNext();

        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.showNext();
            }
        });
        return viewFlipper;
    }
}
