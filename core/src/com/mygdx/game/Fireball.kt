package com.mygdx.game

class Fireball() : com.badlogic.gdx.math.Rectangle() {
    var direction = "up"
    fun moveFireball(delta: Float) {
        y += 400f * delta
    }
}