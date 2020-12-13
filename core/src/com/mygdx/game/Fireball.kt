package com.mygdx.game

import com.mygdx.game.ToxicBarrelAnimation
import java.awt.Rectangle

class Fireball() : com.badlogic.gdx.math.Rectangle() {
    var direction = "up"
    fun moveFireball(delta: Float) {
            y += 400f * delta
    }
}