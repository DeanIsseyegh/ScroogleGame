package com.mygdx.game.player

class PlayerState {
    val maxHealth: Long = 3
    var hitpoints: Long = maxHealth

    var weapon: PlayerWeapon = PlayerWeapon(-100f, -100f)
    var timeHasBeenAttacking: Float = 0f
    var timeUntilNextAttackAllowed = 0f
    var timeHasBeenJumping=0f
    var orbDelay = 5

    var enemiesKilled = 0f
}