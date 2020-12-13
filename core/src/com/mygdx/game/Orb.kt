package com.mygdx.game

import com.mygdx.game.ToxicBarrelAnimation
import java.awt.Rectangle

class Orb() : com.badlogic.gdx.math.Rectangle() {
    var direction = "down"
    fun moveOrb(delta: Float) {
        if (direction == "left") {
            x -= 400f * delta
        }
        else if (direction == "right") {
            x += 400f * delta
        }
    }
}