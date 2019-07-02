package com.mygdx.game

class PlayerState {
    val maxHealth: Long = 3
    var hitpoints: Long = maxHealth

    var timeSinceStartedAttacking = 0f
    var isAttacking: Boolean = false

}