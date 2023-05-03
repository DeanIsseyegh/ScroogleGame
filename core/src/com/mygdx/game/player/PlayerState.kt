package com.mygdx.game.player

import com.badlogic.gdx.math.Rectangle

class PlayerState : Rectangle() {
    val maxHealth: Long = 3
    var hitpoints: Long = maxHealth
    var invulnerableTime = 0f

    var weapon: PlayerWeapon = PlayerWeapon(-100f, -100f)
    var timeHasBeenAttacking: Float = 0f
    var timeUntilNextAttackAllowed = 0f
    var timeHasBeenJumping = 0f
    var orbDelay = 5f
    var fireballFuel = 0f

    var enemiesKilled = 0f

    init {
        width = 30f
        height = 45f
        y = 45f
    }

    fun moveLeft(delta: Float) {
        x -= 200 * delta
        if (x < 0) {
            x = 0f
        }
    }

    fun moveRight(delta: Float, viewPortWidth: Float) {
        x += 200 * delta
        if (x > (viewPortWidth - width)) {
            x = viewPortWidth - width
        }
    }
    fun jump(delta:Float){
        y += 300 * delta
        timeHasBeenJumping += delta
    }
    fun falling(delta:Float){
        y -= 300 * delta
    }

}