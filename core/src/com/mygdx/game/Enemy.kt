package com.mygdx.game

import com.mygdx.game.ToxicBarrelAnimation
import java.awt.Rectangle

class Enemy() : com.badlogic.gdx.math.Rectangle() {
    var direction = "down"

    fun moveEnemy(delta: Float) {
        if (direction == "right") {
            x += 100f*delta
        } else if (direction == "left") {
            x -= 100f*delta
        }
    }
}