package com.mygdx.game.player

class PlayerState {
    val maxHealth: Long = 3
    var hitpoints: Long = maxHealth

    var weapon: PlayerWeapon = PlayerWeapon("sword", -100f, -100f, 10f, 25f)
    var fireball: PlayerWeapon = PlayerWeapon("fireball", -100f, -100f, 50f, 50f)

    var timeHasBeenAttacking: Float = 0f
    var timeUntilNextAttackAllowed = 0f

    var enemiesKilled = 0f
}