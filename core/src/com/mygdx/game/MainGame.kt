package com.mygdx.game

import com.badlogic.gdx.Game
import com.mygdx.game.screens.ScroogleScreen

class MainGame : Game() {

    private val viewPortWidth: Float = 600f
    private val viewPortHeight: Float = 360f

    override fun create() {
        val scroogleGameKotlin = ScroogleScreen(this, viewPortWidth, viewPortHeight)
        setScreen(scroogleGameKotlin)
    }

}