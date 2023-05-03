package com.mygdx.game.screens

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
import com.mygdx.game.*
import com.mygdx.game.animations.DemonAnimation
import com.mygdx.game.animations.FireballAnimation
import com.mygdx.game.animations.KnightAnimation
import com.mygdx.game.animations.ToxicBarrelAnimation
import com.mygdx.game.player.PlayerHealth
import com.mygdx.game.player.PlayerState
import java.util.*
import kotlin.collections.ArrayList


class ScroogleScreenLevel1(private val game: Game,
                           private val viewPortWidth: Float,
                           private val viewPortHeight: Float) : Screen {

    private var lastEnemySpawnTime: Long = 0
    private val batch: SpriteBatch
    private val knightAnimation: Animation<TextureRegion>
    private val knightWeaponImg: Texture
    private val demonAnimation: Animation<TextureRegion>
    private val levelBackgroundImg: Texture
    private val music: Music
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
    private val barrelWidth = 20f
    private val barrelHeight = 35f
    //    private val barrel = Rectangle(((viewPortWidth - barrelWidth) / 2), 90f + platform1.height, barrelWidth, barrelheight)
    private var barrels: MutableList<Barrel> = mutableListOf()

    private val orbWidth = 15f
    private val orbHeight = 15f
    private val orbWeaponImg: Texture
    private var orbs: MutableList<Orb> = mutableListOf()

    private val fireballWidth = 100f
    private val fireballHeight = 100f
    private val fireballAnimation: Animation<TextureRegion>
    private var fireballs: MutableList<Fireball>

    private val knightHealthWidth = 10f
    private val knightHealthHeight = 10f
    private val knightHealth: MutableList<PlayerHealth>
    private val knightHealthImg: Texture

    private val lakitu: Lakitu


    init {
        batch = SpriteBatch()
        knightAnimation = KnightAnimation().createKnightAnimation()
        demonAnimation = DemonAnimation().createAnimiation()
        fireballAnimation = FireballAnimation().createFireballAnimation()
        barrelAnimation = ToxicBarrelAnimation().createToxicBarrelAnimation()
        levelBackgroundImg = Texture("levels/level1/background.png")
        platformImg = Texture("levels/level1/platform3.png")
        knightWeaponImg = Texture("player/weapons/weapon1.png")
        knightHealthImg = Texture("player/1-heart.png")
        orbWeaponImg = Texture("orbs_pack/sphere_light_blue.png")
        music = Gdx.audio.newMusic(Gdx.files.internal("music/Level1Music.mp3"))
        music.isLooping = true
        val camera = OrthographicCamera(viewPortWidth, viewPortHeight)
        camera.setToOrtho(false)
        viewport = FitViewport(viewPortWidth, viewPortHeight, camera)
        viewport.apply()
        playerState.x = viewPortWidth / 2 - playerState.width / 2
        enemies = mutableListOf()
        knightHealth = mutableListOf()
        orbs = mutableListOf()
        fireballs = mutableListOf()
        barrels = mutableListOf()
        spawnEnemy()
        lakitu = Lakitu()
        lakitu.x = viewPortWidth / 2 - lakitu.width / 2
        lakitu.y = viewPortHeight - lakitu.height
        populateHealthList()

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

    private fun spawnBarrel() {
        if (playerState.enemiesKilled > 0 && playerState.enemiesKilled.rem(10f) == 0f) {
            System.out.println("barrel")
            val barrel = Barrel()
            barrel.x = MathUtils.random(0f, viewPortWidth - barrelWidth)
            barrel.y = viewPortHeight
            barrel.width = barrelWidth
            barrel.height = barrelHeight
            barrels.add(barrel)
        }
    }

    private fun moveBarrel(delta: Float) {

        barrels.forEach { barrel ->
            if (barrel.y > barrel.height / 4 && !isRectangleOnPLatform(barrel)) {
                barrel.moveBarrel(delta)
            }
        }
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
        handlePlayerFireballAttackInput(delta)
        reduceInvulnerableTime(delta)

        moveOrb(delta)
        moveFireballs(delta)
        moveEnemies(delta)
        moveBarrel(delta)
        moveLakitu(delta)

        checkEnemyCollisionWithWeapon()
        checkEnemyCollisionWithProjectile()
        checkEnemyCollisionWithPlayer()
        checkEnemyCollisionWithRefuelBarrel()
        moveOuchText(delta)

    }

    private fun drawSpritesAndText() {
        val currentKnightFrame = knightAnimation.getKeyFrame(stateTime, true)
        batch.draw(currentKnightFrame, playerState.x, playerState.y, playerState.width, playerState.height)
        val currentToxicBarrelFrame = barrelAnimation.getKeyFrame(stateTime, true)
        barrels.forEach { barrel -> batch.draw(currentToxicBarrelFrame, barrel.x, barrel.y, barrel.width, barrel.height) }
        font.draw(
                batch,
                "Health: ${playerState.hitpoints}/${playerState.maxHealth}",
                viewPortWidth - 100f,
                viewPortHeight
        )
        font.draw(batch, "Enemies Killed: ${playerState.enemiesKilled}", 10f, viewPortHeight)
        font.draw(batch, "Boss Health: ${lakitu.bossHealth}", 10f, viewPortHeight - 25f)
        font.draw(batch, "flamethrower fuel: ${playerState.fireballFuel / 3 * 100}+%", 10f, viewPortHeight - 50f)
        batch.draw(knightWeaponImg, playerState.weapon.x, playerState.weapon.y)
        drawKnightHealth()
        orbs.forEach { orb -> batch.draw(orbWeaponImg, orb.x, orb.y, orb.width, orb.height) }
        val currentFireballFrame = fireballAnimation.getKeyFrame(stateTime, true)
        fireballs.forEach { fireball -> batch.draw(currentFireballFrame, fireball.x, fireball.y, fireballWidth, fireballHeight) }
        platforms.forEach { platform -> batch.draw(platformImg, platform.x, platform.y) }
        ouchTextList.forEach { ouchText -> font.draw(batch, ouchText.ouchText, ouchText.x, ouchText.y) }
        val currentEnemyFrame = demonAnimation.getKeyFrame(stateTime, true)
        enemies.forEach { enemy -> batch.draw(currentEnemyFrame, enemy.x, enemy.y) }
        val currentLakituFrame = lakitu.lakituAnimation.getKeyFrame(stateTime, true)
        batch.draw(currentLakituFrame, lakitu.x, lakitu.y, lakitu.width, lakitu.height)
    }

    private fun populateHealthList() {
        var i = 0
        while (i < playerState.hitpoints) {
            var health = PlayerHealth()
            health.x = ((i + 1f) * 10f)
            health.y = viewPortHeight - 75f
            knightHealth.add(health)
            i++
        }
    }

    private fun drawKnightHealth() {
        var i = 0
        while (i < playerState.hitpoints) {
            knightHealth.forEach { knightHealth -> batch.draw(knightHealthImg, knightHealth.x, knightHealth.y, knightHealthWidth, knightHealthHeight) }
            i++
        }
    }

    private fun reduceInvulnerableTime(delta: Float) {
        if (playerState.invulnerableTime > 0) {
            playerState.invulnerableTime -= delta
        }
    }

    private fun checkEnemyCollisionWithWeapon() {
        val enemyThatsHitWeapon = enemies.find { it.overlaps(playerState.weapon) }
        if (enemyThatsHitWeapon != null) {
            enemies.remove(enemyThatsHitWeapon)
            playerState.enemiesKilled += 1
            spawnBarrel()
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
        fireballs.forEach { fireball ->
            fireball.moveFireball(delta)
        }

    }

    private fun handlePlayerMoveInput(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerState.moveLeft(delta)
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerState.moveRight(delta,viewPortWidth)
        }
    }

    private fun handlePlayerJumpInput(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && playerState.timeHasBeenJumping < 0.4 || playerState.timeHasBeenJumping > 0 && playerState.timeHasBeenJumping < 0.4) {
            playerState.jump(delta)
        } else if (!isPlayerOnPLatform() && playerState.y >= 5f) {
            playerState.falling(delta)
            if (playerState.y <= 5) {
                playerState.timeHasBeenJumping = 0f
            }
        }
    }

    private fun isPlayerOnPLatform(): Boolean {
        platforms.forEach { platform ->
            if ((playerState.x > platform.x - playerState.width && playerState.x < platform.x + platform.width) && ((playerState.y > platform.y - platform.height / 2 && playerState.y < platform.y + platform.height / 2) || playerState.y == platform.y + platform.height)) {
                playerState.y = platform.y + platform.height
                playerState.timeHasBeenJumping = 0f
                return true;
            }
        }
        return false
    }

    private fun handlePlayerAttackInput(delta: Float) {
        playerState.timeUntilNextAttackAllowed -= delta
        if (playerState.timeHasBeenAttacking < 0.5 && playerState.timeUntilNextAttackAllowed <= 0 && Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerState.weapon.x = playerState.x + playerState.width / 2
            playerState.weapon.y = playerState.y + playerState.height / 2
            playerState.timeHasBeenAttacking += delta
        }

        if (playerState.timeHasBeenAttacking > 0 && (!Gdx.input.isKeyPressed(Input.Keys.A) || playerState.timeHasBeenAttacking >= 0.5)) {
            stopPlayerAttackAndAddEndLag()
        }

    }

    private fun handlePlayerOrbAttackInput(delta: Float) {
        playerState.orbDelay -= 1
        if (playerState.orbDelay < 0 && Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerState.orbDelay = 10f
            val leftOrb = Orb()
            leftOrb.x = playerState.x
            leftOrb.y = playerState.y + playerState.height / 2
            leftOrb.width = orbWidth
            leftOrb.height = orbHeight
            leftOrb.direction = "left"
            val rightOrb = Orb()
            rightOrb.x = playerState.x + playerState.width
            rightOrb.y = playerState.y + playerState.height / 2
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
        orbs.forEach { orb ->
            val enemyThatsHitProjectile = enemies.find { it.overlaps(orb) }
            if (enemyThatsHitProjectile != null) {
                playerState.enemiesKilled += 1
                enemies.remove(enemyThatsHitProjectile)
                spawnBarrel()
//                orbs.remove (orb)
            }
//            else if(orb.x>viewPortWidth||orb.x<viewPortWidth){
//                orbs.remove(orb)
//            }
        }
        fireballs.forEach { fireball ->
            val enemyThatsHitProjectile = enemies.find { it.overlaps(fireball) }
            if (enemyThatsHitProjectile != null) {
                playerState.enemiesKilled += 1
                enemies.remove(enemyThatsHitProjectile)
                spawnBarrel()
//                fireballs.remove (fireball)
            }
            val bossThatHitsProjectile = lakitu.overlaps(fireball)
            if (bossThatHitsProjectile && lakitu.bossHealth > 0) {
                shrinkBoss()
                fireball.y += 500f
                lakitu.bossHealth = lakitu.bossHealth - 5
                if (lakitu.bossHealth < 600) {
                    lakitu.turnAngry()
                    lakitu.movementType = "angry"
                }
            } else if (lakitu.bossHealth <= 0) {
                lakitu.y += 1000f
            }
//
        }
    }

    private fun shrinkBoss() {
        if (lakitu.bossHealth == 900f) {
            lakitu.width = (lakitu.bossHealth / 1000f * lakitu.width)
            lakitu.height = (lakitu.bossHealth / 1000f * lakitu.height)
        }
    }


    private fun handlePlayerFireballAttackInput(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.F) && playerState.fireballFuel > 0) {
            playerState.fireballFuel -= delta
            var fireball = Fireball()
            fireball.x = playerState.x - playerState.width - fireball.width
            fireball.y = playerState.y
            fireball.width = fireballWidth
            fireball.height = fireballHeight
            fireballs.add(fireball)
        }
    }

    private fun checkEnemyCollisionWithRefuelBarrel() {
        barrels.forEach { barrel ->
            if (playerState.overlaps(barrel)) {
                barrel.x = -100f
                playerState.fireballFuel = 3f
            }
        }
    }

    private fun stopPlayerAttackAndAddEndLag() {
        playerState.timeUntilNextAttackAllowed = 0.5f
        playerState.weapon.x = -100f
        playerState.weapon.y = -100f
        playerState.timeHasBeenAttacking = 0f
    }

    //need a period of time without damage
    private fun checkEnemyCollisionWithPlayer() {
        val enemyThatsHitPlayer = enemies.find { it.overlaps(playerState) }
        if (playerState.invulnerableTime <= 0f) {
            if (enemyThatsHitPlayer != null) {
                playerIsHit()
                enemies.remove(enemyThatsHitPlayer)
                ouchTextList.add(OuchText(playerState.x))
                if (playerState.hitpoints == 0L) {
                    gameOver()
                }
            } else if (lakitu.overlaps(playerState)) {
                playerIsHit()
                if (playerState.hitpoints == 0L) {
                    gameOver()
                }
            }
        }
    }

    private fun playerIsHit() {
        playerState.hitpoints = playerState.hitpoints - 1
        knightHealth.removeAt(playerState.hitpoints.toInt())
        playerState.invulnerableTime = 2f
    }

    private fun gameOver() {
        isDead = true
        game.screen = GameOverScreen(game, viewPortWidth, viewPortHeight, playerState, "level-1")
    }

    private fun moveLakitu(delta: Float) {
        if (lakitu.x < lakitu.width / 4) {
            lakitu.direction = "right"
        } else if (lakitu.x > viewPortWidth - lakitu.width * 1.25) {
            lakitu.direction = "left"
        }
        lakitu.moveLakitu(delta)
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
