package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class KnightAnimationJava extends Animation<TextureRegion> {

    public KnightAnimationJava(float frameDuration, Array<? extends TextureRegion> keyFrames) {
        super(frameDuration, keyFrames);
    }

    public KnightAnimationJava(float frameDuration, Array<? extends TextureRegion> keyFrames, PlayMode playMode) {
        super(frameDuration, keyFrames, playMode);
    }

    public KnightAnimationJava(float frameDuration, TextureRegion... keyFrames) {
        super(frameDuration, keyFrames);
    }
}
