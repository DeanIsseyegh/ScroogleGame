package com.mygdx.game.animations

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class ToxicBarrelAnimation {

    private val toxicBarrelFrames: Array<TextureRegion>

    init {
        // Load the sprite sheet as a Texture
        val toxicBarrelSheet = Texture(Gdx.files.internal("environmentItems/ToxicBarrelAnimation.png"))

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        val tmp = TextureRegion.split(toxicBarrelSheet,
                toxicBarrelSheet.getWidth() / 2,
                toxicBarrelSheet.getHeight() / 2)

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        val idleFramesTmp: Array<TextureRegion?> = arrayOfNulls(2 * 2)
        var index = 0
        for (i in 0 until 2) {
            for (j in 0 until 1) {
                idleFramesTmp[index++] = tmp[i][j]
            }
        }
        toxicBarrelFrames = idleFramesTmp.filterNotNull().toTypedArray()
    }

    fun createToxicBarrelAnimation(): Animation<TextureRegion> {
        return Animation(0.1f, *toxicBarrelFrames)
    }

}