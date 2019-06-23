package com.mygdx.game

import com.badlogic.gdx.Game

class MainGame : Game() {

    private val viewPortWidth: Float = 300f
    private val viewPortHeight: Float = 480f

    override fun create() {
        val scroogleGameKotlin = ScroogleGameKotlin(this, viewPortHeight, viewPortHeight)
        setScreen(scroogleGameKotlin)
    }

}