package com.mygdx.game

import com.badlogic.gdx.Game
import com.mygdx.game.screens.ScroogleScreenLevel1

class MainGame : Game() {

    private val viewPortWidth: Float = 600f
    private val viewPortHeight: Float = 360f

    override fun create() {
        val scroogleGameKotlin = ScroogleScreenLevel1(this, viewPortWidth, viewPortHeight)
        setScreen(scroogleGameKotlin)
    }

}