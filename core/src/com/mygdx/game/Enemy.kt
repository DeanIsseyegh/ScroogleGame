package com.mygdx.game

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