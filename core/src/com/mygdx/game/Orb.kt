package com.mygdx.game

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