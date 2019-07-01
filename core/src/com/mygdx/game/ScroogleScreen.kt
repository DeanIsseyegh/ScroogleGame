package com.mygdx.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.FitViewport


class ScroogleScreen(private val game: Game,
                     private val viewPortWidth: Float,
                     private val viewPortHeight: Float) : Screen {

    private var lastEnemySpawnTime: Long = 0
    private val batch: SpriteBatch
    private val knightImg: Texture
    private val enemyImg: Texture
    private val levelBackgroundImg: Texture
    private val music: Music
    private val player: Rectangle
    private val playerHealth: PlayerHealth = PlayerHealth()
    private var enemies: MutableList<Rectangle>
    private val font: BitmapFont = BitmapFont()
    private val ouchTextList: MutableList<OuchText> = mutableListOf()

    var viewport: FitViewport
    private var isDead = false

    private val enemyWidth = 32f
    private val enemyHeight = 36f

    init {
        batch = SpriteBatch()
        knightImg = Texture("player/knight/knight_f_idle_anim_f0.png")
        enemyImg = Texture("enemy/bigdemon/big_demon_idle_anim_f0.png")
        levelBackgroundImg = Texture("levels/level1/background.png")
        music = Gdx.audio.newMusic(Gdx.files.internal("music/Level1Music.mp3"))
        music.isLooping = true
        val camera = OrthographicCamera(viewPortWidth, viewPortHeight)
        camera.setToOrtho(false)
        viewport = FitViewport(viewPortWidth, viewPortHeight, camera)
        viewport.apply()
        player = Rectangle()
        player.width = 16f * 1.5f
        player.height = 28f * 1.5f
        player.x = viewPortWidth / 2 - player.width / 2
        player.y = 20f
        enemies = mutableListOf()
        spawnEnemy()
    }

    private fun spawnEnemy() {
        val rectangle = Rectangle()
        rectangle.x = MathUtils.random(0f, viewPortWidth - enemyWidth)
        rectangle.y = viewPortHeight / 2
        rectangle.width = enemyWidth
        rectangle.height = enemyHeight
        enemies.add(rectangle)
        lastEnemySpawnTime = TimeUtils.millis()
    }

    override fun render(delta: Float) {
        viewport.apply()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (TimeUtils.millis() - lastEnemySpawnTime > 100) spawnEnemy()

        batch.projectionMatrix = viewport.camera.combined
        batch.begin()
        ouchTextList.forEach { ouchText -> font.draw(batch, ouchText.ouchText, ouchText.x, ouchText.y) }
        batch.draw(levelBackgroundImg, 0f, 0f, viewPortWidth, viewPortHeight)
        batch.draw(knightImg, player.x, player.y, player.width, player.height)
        font.draw(batch, "Health: ${playerHealth.hitpoints}/${playerHealth.maxHealth}", viewPortWidth - 100f, viewPortHeight)
        enemies.forEach { enemy -> batch.draw(enemyImg, enemy.x, enemy.y) }
        batch.end()

        handlePlayerInput(delta)
        moveEnemies(delta)
        moveOuchText(delta)
        checkEnemyCollision()
    }

    private fun moveOuchText(delta: Float) {
        ouchTextList.forEach { ouchText ->
            ouchText.y += 200 * delta
        }
    }

    private fun moveEnemies(delta: Float) {
        enemies.forEach { enemy ->
            enemy.y -= 200 * delta
        }

        enemies = enemies.filter { enemy -> (enemy.y + enemy.height / 2 > 0) }.toMutableList()
    }

    private fun handlePlayerInput(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.x -= 200 * delta
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.x += 200 * delta
        }

        if (player.x < 0) player.x = 0f
        if (player.x > (viewPortWidth - player.width)) player.x = viewPortWidth - player.width
    }

    private fun checkEnemyCollision() {
        val enemyThatsHitPlayer = enemies.find { it.overlaps(player) }
        if (enemyThatsHitPlayer != null) {
            playerHealth.hitpoints = playerHealth.hitpoints - 1
            enemies.remove(enemyThatsHitPlayer)
            ouchTextList.add(OuchText(player.x))
            if (playerHealth.hitpoints == 0L) {
                isDead = true
                game.screen = GameOverScreen(game, viewPortWidth, viewPortHeight)
            }
        }
    }


    override fun dispose() {
        batch.dispose()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun hide() {
        //No op
    }

    override fun show() {
        //No op
    }

    override fun pause() {
        //No op
    }

    override fun resume() {
        //No op
    }

}
