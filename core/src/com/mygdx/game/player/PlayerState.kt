package com.mygdx.game.player

class PlayerState {
    val maxHealth: Long = 3
    var hitpoints: Long = maxHealth
    var invulnerableTime = 0f

    var weapon: PlayerWeapon = PlayerWeapon(-100f, -100f)
    var timeHasBeenAttacking: Float = 0f
    var timeUntilNextAttackAllowed = 0f
    var timeHasBeenJumping=0f
    var orbDelay = 5f
    var fireballFuel = 0f

    var enemiesKilled = 0f
}