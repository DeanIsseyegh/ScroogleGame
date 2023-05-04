package com.mygdx.game.screens.common.levelMechanics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.mygdx.game.player.PlayerState

class PlayerMechanics() {

    fun handlePlayerMoveInput(delta: Float, playerState: PlayerState, viewPortWidth: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerState.moveLeft(delta)
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerState.moveRight(delta, viewPortWidth)
        }

    }
    fun handlePlayerJumpInput(delta: Float, playerState: PlayerState, platforms: ArrayList<com.badlogic.gdx.math.Rectangle>) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && playerState.timeHasBeenJumping < 0.4 || playerState.timeHasBeenJumping > 0 && playerState.timeHasBeenJumping < 0.4) {
            playerState.jump(delta)
        } else if (!isPlayerOnPLatform(playerState,platforms) && playerState.y >= 5f) {
            playerState.falling(delta)
            if (playerState.y <= 5) {
                playerState.timeHasBeenJumping = 0f
            }
        }
    }

    fun isPlayerOnPLatform(playerState: PlayerState, platforms: ArrayList<com.badlogic.gdx.math.Rectangle>): Boolean {
        platforms.forEach { platform ->
            if ((playerState.x > platform.x - playerState.width && playerState.x < platform.x + platform.width) && ((playerState.y > platform.y - platform.height / 2 && playerState.y < platform.y + platform.height / 2) || playerState.y.equals( platform.y + platform.height))) {
                playerState.y = (platform.y + platform.height).toFloat()
                playerState.timeHasBeenJumping = 0f
                return true;
            }
        }
        return false
    }
}