package com.mygdx.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.player.PlayerState


class ScroogleScreen(private val game: Game,
                     private val viewPortWidth: Float,
                     private val viewPortHeight: Float) : Screen {

    private var lastEnemySpawnTime: Long = 0
    private val batch: SpriteBatch
    private val knightAnimation: Animation<TextureRegion>
    private val knightWeaponImg: Texture
    private val demonAnimation: Animation<TextureRegion>
    private val fireballAnimation: Animation<TextureRegion>
    private val levelBackgroundImg: Texture
    private val music: Music
    private val player: Rectangle
    private val playerState: PlayerState = PlayerState()
    private var enemies: MutableList<Rectangle>
    private val font: BitmapFont = BitmapFont()
    private val ouchTextList: MutableList<OuchText> = mutableListOf()

    var viewport: FitViewport
    private var isDead = false

    private val enemyWidth = 32f
    private val enemyHeight = 36f

    init {
        batch = SpriteBatch()
        knightAnimation = KnightAnimation().createKnightAnimation()
        demonAnimation = DemonAnimation().createAnimiation()
        fireballAnimation = FireballAnimation().createFireballAnimation()
        levelBackgroundImg = Texture("levels/level1/background.png")
        knightWeaponImg = Texture("player/weapons/weapon1.png")
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

    var stateTime = 0f
    override fun render(delta: Float) {
        viewport.apply()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (TimeUtils.millis() - lastEnemySpawnTime > 100) spawnEnemy()

        batch.projectionMatrix = viewport.camera.combined

        batch.begin()
        batch.draw(levelBackgroundImg, 0f, 0f, viewPortWidth, viewPortHeight)
        stateTime += delta
        drawSpritesAndText()
        batch.end()

        handlePlayerMoveInput(delta)
        handlePlayerAttackInput(delta)
        handlePlayerFireballAttackInput(delta)
        moveEnemies(delta)
        checkEnemyCollisionWithWeapon()
        checkEnemyCollisionWithFireball()
        checkEnemyCollisionWithPlayer()
        moveOuchText(delta)
    }

    private fun drawSpritesAndText() {
        val currentKnightFrame = knightAnimation.getKeyFrame(stateTime, true)
        batch.draw(currentKnightFrame, player.x, player.y, player.width, player.height)
        font.draw(
            batch,
            "Health: ${playerState.hitpoints}/${playerState.maxHealth}",
            viewPortWidth - 100f,
            viewPortHeight
        )
        font.draw(batch, "Enemies Killed: ${playerState.enemiesKilled}", 50f, viewPortHeight)
        batch.draw(knightWeaponImg, playerState.weapon.x, playerState.weapon.y)
        val currentFireballFrame = fireballAnimation.getKeyFrame(stateTime, true)
        batch.draw(currentFireballFrame, playerState.fireball.x - 35, playerState.fireball.y)
        ouchTextList.forEach { ouchText -> font.draw(batch, ouchText.ouchText, ouchText.x, ouchText.y) }
        val currentEnemyFrame = demonAnimation.getKeyFrame(stateTime, true)
        enemies.forEach { enemy -> batch.draw(currentEnemyFrame, enemy.x, enemy.y) }
    }


    private fun checkEnemyCollisionWithWeapon() {
        val enemyThatsHitWeapon = enemies.find { it.overlaps(playerState.weapon) }
        if (enemyThatsHitWeapon != null) {
            enemies.remove(enemyThatsHitWeapon)
            playerState.enemiesKilled += 1
        }
    }

    private fun checkEnemyCollisionWithFireball() {
        val enemyThatsHitFireball = enemies.find { it.overlaps(playerState.fireball) }
        if (enemyThatsHitFireball != null) {
            enemies.remove(enemyThatsHitFireball)
            playerState.enemiesKilled += 1
        }
    }

    private fun moveEnemies(delta: Float) {
        enemies.forEach { enemy ->
            enemy.y -= 200 * delta
        }

        enemies = enemies.filter { enemy -> (enemy.y + enemy.height / 2) > 0 }.toMutableList()
    }

    private fun moveFireballs(delta: Float) {
        playerState.fireball.y += 200 * delta
    }


    private fun handlePlayerMoveInput(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.x -= 200 * delta
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.x += 200 * delta
        }

        if (player.x < 0) player.x = 0f
        if (player.x > (viewPortWidth - player.width)) player.x = viewPortWidth - player.width
    }

    private fun handlePlayerAttackInput(delta: Float) {
        playerState.timeUntilNextAttackAllowed -= delta
        if (playerState.timeHasBeenAttacking < 0.5 && playerState.timeUntilNextAttackAllowed <= 0 && Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerState.weapon.x = player.x + player.width / 2
            playerState.weapon.y = player.y + player.height / 2
            playerState.timeHasBeenAttacking += delta
        }

        if (playerState.timeHasBeenAttacking > 0 && (!Gdx.input.isKeyPressed(Input.Keys.A) || playerState.timeHasBeenAttacking >= 0.5)) {
            stopPlayerAttackAndAddEndLag()
        }

    }

    private fun handlePlayerFireballAttackInput(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            playerState.fireball.x = player.x
            playerState.fireball.y = player.y + player.height / 2
        }
        moveFireballs(delta);
    }

    private fun stopPlayerAttackAndAddEndLag() {
        playerState.timeUntilNextAttackAllowed = 0.5f
        playerState.weapon.x = -100f
        playerState.weapon.y = -100f
        playerState.timeHasBeenAttacking = 0f
    }

    private fun checkEnemyCollisionWithPlayer() {
        val enemyThatsHitPlayer = enemies.find { it.overlaps(player) }
        if (enemyThatsHitPlayer != null) {
            playerState.hitpoints = playerState.hitpoints - 1
            enemies.remove(enemyThatsHitPlayer)
            ouchTextList.add(OuchText(player.x))
            if (playerState.hitpoints == 0L) {
                isDead = true
                game.screen = GameOverScreen(game, viewPortWidth, viewPortHeight, playerState)
            }
        }
    }

    override fun dispose() {
        batch.dispose()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    private fun moveOuchText(delta: Float) {
        ouchTextList.forEach { ouchText ->
            ouchText.y += 200 * delta
        }
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
