package com.mygdx.game.player

import com.mygdx.game.FireballPosition

class PlayerState {
    val maxHealth: Long = 3
    var hitpoints: Long = maxHealth

    var weapon: PlayerWeapon = PlayerWeapon(-100f, -100f)
    var fireball: PlayerWeapon = PlayerWeapon(-100f, -100f)
    var timeHasBeenAttacking: Float = 0f
    var timeUntilNextAttackAllowed = 0f

    var enemiesKilled = 0f
}