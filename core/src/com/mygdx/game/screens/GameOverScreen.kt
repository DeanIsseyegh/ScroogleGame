package com.mygdx.game.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.player.PlayerState

class GameOverScreen(private val game: Game,
                     private val viewPortWidth: Float,
                     private val viewPortHeight: Float,
                     private val playerState: PlayerState,
                     private val level: String) : Screen {

    var batch: SpriteBatch
    var viewport: FitViewport
    private val font: BitmapFont = BitmapFont()

    init {
        batch = SpriteBatch()
        val camera = OrthographicCamera(viewPortWidth, viewPortHeight)
        camera.setToOrtho(false)
        viewport = FitViewport(viewPortWidth, viewPortHeight, camera)
        viewport.apply()
    }

    override fun hide() {

    }

    override fun show() {

    }

    override fun render(delta: Float) {
        viewport.apply()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.projectionMatrix = viewport.camera.combined
        batch.begin()
        font.draw(batch, "You died.", viewPortWidth / 2 - 100f, viewPortHeight / 2)
        font.draw(batch, "Press enter to try again", viewPortWidth / 2 - 100f, viewPortHeight / 2 - 50f)
        font.draw(batch, "You killed ${playerState.enemiesKilled} enemies", viewPortWidth / 2 - 100f, viewPortHeight / 2 - 100f)
        batch.end()

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) startGame()
    }

    private fun startGame() {
        if (level == "level-1") {
            game.screen = ScroogleScreenLevel1(game, viewPortWidth, viewPortHeight)
        }
        if (level == "level-2") {
            game.screen = ScroogleScreenLevel2(game, viewPortWidth, viewPortHeight)
        }
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {

    }
}