package com.mygdx.game

import com.badlogic.gdx.Game

class MainGame : Game() {

    private val viewPortWidth: Float = 600f
    private val viewPortHeight: Float = 360f

    override fun create() {
        val scroogleGameKotlin = ScroogleScreen(this, viewPortHeight, viewPortWidth)
        setScreen(scroogleGameKotlin)
    }

}