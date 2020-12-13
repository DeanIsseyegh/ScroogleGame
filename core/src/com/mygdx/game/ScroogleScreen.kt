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
import java.util.*
import kotlin.collections.ArrayList


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
    private var enemies: MutableList<Enemy>
    private val font: BitmapFont = BitmapFont()
    private val ouchTextList: MutableList<OuchText> = mutableListOf()
    var viewport: FitViewport
    private var isDead = false
    private val enemyWidth = 32f
    private val enemyHeight = 36f

    private val platformWidth = 163f
    private val platformHeight = 13f
    private val platformImg: Texture
    private val platform1 = Rectangle(((viewPortWidth - platformWidth) / 2), 90f, platformWidth, platformHeight)
    private val platform2 = Rectangle(0f, 180f, platformWidth, platformHeight)
    private val platform3 = Rectangle(viewPortWidth - platformWidth, 180f, platformWidth, platformHeight)
    val platforms = ArrayList<Rectangle>(Arrays.asList(platform1, platform2, platform3))

    private val barrelAnimation: Animation<TextureRegion>
    private val barrelWidth = 30f
    private val barrelheight = 50f
    private val barrel = Rectangle(((viewPortWidth - barrelWidth) / 2), 90f + platform1.height, barrelWidth, barrelheight)


    private val orbWidth = 15f
    private val orbHeight = 15f
    private val orbWeaponImg: Texture
    private var orbs: MutableList<Orb> = mutableListOf()


    init {
        batch = SpriteBatch()
        knightAnimation = KnightAnimation().createKnightAnimation()
        demonAnimation = DemonAnimation().createAnimiation()
        fireballAnimation = FireballAnimation().createFireballAnimation()
//        barrelAnimation= ExplosionAnimation().createExplosionAnimation()
        barrelAnimation = ToxicBarrelAnimation().createToxicBarrelAnimation()
        levelBackgroundImg = Texture("levels/level1/background.png")
        platformImg = Texture("levels/level1/platform3.png")
        knightWeaponImg = Texture("player/weapons/weapon1.png")
        orbWeaponImg = Texture("orbs_pack/sphere_light_blue.png")
        music = Gdx.audio.newMusic(Gdx.files.internal("music/Level1Music.mp3"))
        music.isLooping = true
        val camera = OrthographicCamera(viewPortWidth, viewPortHeight)
        camera.setToOrtho(false)
        viewport = FitViewport(viewPortWidth, viewPortHeight, camera)
        viewport.apply()
        player = Rectangle()
//        player.width = 16f * 1.5f
        player.width = 30f
//        player.height = 28f * 1.5f
        player.height = 45f
        player.x = viewPortWidth / 2 - player.width / 2
        player.y = 20f
        enemies = mutableListOf()
        orbs = mutableListOf()
        spawnEnemy()
    }

    private fun spawnEnemy() {
        val enemy = Enemy()
        enemy.x = MathUtils.random(0f, viewPortWidth - enemyWidth)
        enemy.y = viewPortHeight
        enemy.width = enemyWidth
        enemy.height = enemyHeight
        enemies.add(enemy)
        if (enemy.x > viewPortWidth / 2) {
            enemy.direction = "left"
        } else {
            enemy.direction = "right"
        }
        lastEnemySpawnTime = TimeUtils.millis()
    }

    var stateTime = 0f
    override fun render(delta: Float) {
        viewport.apply()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (TimeUtils.millis() - lastEnemySpawnTime > 1000) spawnEnemy()

        batch.projectionMatrix = viewport.camera.combined

        batch.begin()
        batch.draw(levelBackgroundImg, 0f, 0f, viewPortWidth, viewPortHeight)
        stateTime += delta
        drawSpritesAndText()
        batch.end()

        handlePlayerMoveInput(delta)
        handlePlayerJumpInput(delta)
        handlePlayerAttackInput(delta)
        handlePlayerOrbAttackInput(delta)
        handlePlayerFireballAttackInput()
        moveOrb(delta)
        moveEnemies(delta)
        checkEnemyCollisionWithWeapon()
        checkEnemyCollisionWithProjectile()
        checkEnemyCollisionWithPlayer()
        moveOuchText(delta)
    }

    private fun drawSpritesAndText() {
        val currentKnightFrame = knightAnimation.getKeyFrame(stateTime, true)
        batch.draw(currentKnightFrame, player.x, player.y, player.width, player.height)
        val currentToxicBarrelFrame = barrelAnimation.getKeyFrame(stateTime, true)
        batch.draw(currentToxicBarrelFrame, barrel.x, barrel.y, barrelWidth, barrelheight)
        font.draw(
                batch,
                "Health: ${playerState.hitpoints}/${playerState.maxHealth}",
                viewPortWidth - 100f,
                viewPortHeight
        )
        font.draw(batch, "Enemies Killed: ${playerState.enemiesKilled}", 50f, viewPortHeight)
        batch.draw(knightWeaponImg, playerState.weapon.x, playerState.weapon.y)
        orbs.forEach { orb -> batch.draw(orbWeaponImg, orb.x, orb.y, orb.width, orb.height) }
        platforms.forEach { platform -> batch.draw(platformImg, platform.x, platform.y) }
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

    private fun moveEnemies(delta: Float) {
        enemies.forEach { enemy ->
            if (!isRectangleOnPLatform(enemy) && enemy.y > enemy.height / 4) {
                enemy.y -= 200 * delta
            } else {
                enemy.moveEnemy(delta)
            }
        }

        enemies = enemies.filter { enemy -> (enemy.y + enemy.height / 2) > 0 }.toMutableList()
    }

    private fun isRectangleOnPLatform(rectangle: Rectangle): Boolean {
        platforms.forEach { platform ->
            if ((rectangle.x > platform.x - rectangle.width && rectangle.x < platform.x + platform.width) && ((rectangle.y > platform.y - platform.height / 2 && rectangle.y < platform.y + platform.height / 2) || rectangle.y == platform.y + platform.height)) {
                rectangle.y = platform.y + platform.height
                return true;
            }
        }
        return false
    }

    private fun moveFireballs(delta: Float) {
        // Move fireballs
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

    private fun handlePlayerJumpInput(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && playerState.timeHasBeenJumping < 0.4 || playerState.timeHasBeenJumping > 0 && playerState.timeHasBeenJumping < 0.4) {
            player.y += 300 * delta
            playerState.timeHasBeenJumping += delta
        } else if (!isPlayerOnPLatform() && player.y >= 5f) {
            player.y -= 300 * delta
            if (player.y <= 5) {
                playerState.timeHasBeenJumping = 0f
            }
        }
    }

    private fun isPlayerOnPLatform(): Boolean {
        platforms.forEach { platform ->
            if ((player.x > platform.x - player.width && player.x < platform.x + platform.width) && ((player.y > platform.y - platform.height / 2 && player.y < platform.y + platform.height / 2) || player.y == platform.y + platform.height)) {
                player.y = platform.y + platform.height
                playerState.timeHasBeenJumping = 0f
                return true;
            }
        }
        return false
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

    private fun handlePlayerOrbAttackInput(delta: Float) {
        playerState.orbDelay -=1
        if ( playerState.orbDelay<0 &&Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerState.orbDelay=10
            val leftOrb = Orb()
            leftOrb.x = player.x
            leftOrb.y = player.y + player.height / 2
            leftOrb.width = orbWidth
            leftOrb.height = orbHeight
            leftOrb.direction = "left"
            val rightOrb = Orb()
            rightOrb.x = player.x + player.width
            rightOrb.y = player.y + player.height / 2
            rightOrb.width = orbWidth
            rightOrb.height = orbHeight
            rightOrb.direction = "right"
            orbs.add(leftOrb)
            orbs.add(rightOrb)
        }
    }

    private fun moveOrb(delta: Float) {
        orbs.forEach { orb ->
            orb.moveOrb(delta)
        }
    }

    private fun checkEnemyCollisionWithProjectile() {
        orbs.forEach {orb->
            val enemyThatsHitProjectile = enemies.find { it.overlaps(orb) }
            if (enemyThatsHitProjectile != null) {
                playerState.enemiesKilled += 1
                enemies.remove(enemyThatsHitProjectile)
//                orbs.remove (orb)
            }
//            else if(orb.x>viewPortWidth||orb.x<viewPortWidth){
//                orbs.remove(orb)
//            }
        }
    }

    private fun handlePlayerFireballAttackInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            // create fireball
        }
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
