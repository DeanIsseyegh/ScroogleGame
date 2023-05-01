package com.mygdx.game

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.mygdx.game.animations.LakituAnimation

class Lakitu() : com.badlogic.gdx.math.Rectangle() {
    var bossHealth = 1000f
    var direction = "left"
    var lakituAnimation: Animation<TextureRegion>
    var movementType = "normal"
    var depth = ""


    init {
        lakituAnimation = LakituAnimation().createLakituAnimation();
        this.width = 80f
        this.height = 95f
    }

    fun turnAngry() {
        lakituAnimation = LakituAnimation().createAngryLakituAnimation()
    }


    fun moveLakitu(delta: Float) {
        if (movementType == "normal") {
            normalMove(delta)
        } else if (movementType == "angry") {
            angryMove(delta)
        }
    }

    fun normalMove(delta: Float) {
        if (direction == "left") {
            x -= 200f * delta
        } else if (direction == "right") {
            x += 200f * delta

        }
    }

    fun angryMove(delta: Float) {
        if (direction == "left") {
            x -= 100f * delta
            bounceMoveY(delta)
        } else if (direction == "right") {
            x += 100f * delta
            bounceMoveY(delta)
        }
    }

    private fun bounceMoveY(delta: Float) {
        System.out.println(depth + " " + y)
        if (y >= 265f) {
            depth = "top"
        } else if (y <= 0f) {
            depth = "bottom"
        }


        if (depth == "top") {
            y -= 300 * delta
        } else if (depth == "bottom") {
            y += 300 * delta
        }
    }
}