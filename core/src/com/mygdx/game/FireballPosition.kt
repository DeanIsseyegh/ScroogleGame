package com.mygdx.game

class FireballPosition {

    fun calculateFireballPositionX(fireballWidth: Float, playerWidth: Float, playerStartingPositionX: Float): Float {
        val midPointPlayerWidth = playerStartingPositionX + (playerWidth/2)
        val fireballHalfWidth = (fireballWidth/2)

        return midPointPlayerWidth - fireballHalfWidth
    }
}