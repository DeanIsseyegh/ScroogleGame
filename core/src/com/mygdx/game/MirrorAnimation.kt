package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class MirrorAnimation {

    private val idleFrames: Array<TextureRegion>

    init {
        // Load the sprite sheet as a Texture
        val idleSheet = Texture(Gdx.files.internal("player/knight/knight_idle_sheet.png"))

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        val tmp = TextureRegion.split(idleSheet,
                idleSheet.getWidth() / 1,
                idleSheet.getHeight() / 4)

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        val idleFramesTmp: Array<TextureRegion?> = arrayOfNulls(1 * 4)
        var index = 0
        for (i in 0 until 4) {
            for (j in 0 until 1) {
                idleFramesTmp[index++] = tmp[i][j]
            }
        }
        idleFrames = idleFramesTmp.filterNotNull().toTypedArray()
    }

    fun createMirrorAnimation(): Animation<TextureRegion> {
        return Animation(0.1f, *idleFrames)
    }

}