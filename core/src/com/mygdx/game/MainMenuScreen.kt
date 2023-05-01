package drop

import com.badlogic.drop.Drop
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils
import com.mygdx.game.screens.ScroogleScreen
import com.mygdx.game.screens.ScroogleScreenLevel2

class MainMenuScreen(private val game: Drop):Screen {

    private var camera: OrthographicCamera = OrthographicCamera()

    init {
        camera.setToOrtho(false, 800f, 480f)
    }

    override fun render(delta: Float) {
//        ScreenUtils.clear(0f, 0f, 0.2f, 1f);

        camera.update();
        game.batch?.projectionMatrix = camera.combined;

        game.batch?.begin();
        game.font?.draw(game.batch, "Welcome to Drop!!! ", 100f, 150f);
        game.font?.draw(game.batch, "Tap anywhere to begin!", 100f, 100f);
        game.font?.draw(game.batch, "press 1 to start a different level!", 100f, 50f);
        game.batch?.end();

        if (Gdx.input.isTouched) {
            game.screen = ScroogleScreen(game, 600f, 360f);
            dispose();
        }
        if (Gdx.input.isKeyJustPressed(8)){ //8=the number 1
            game.screen = ScroogleScreenLevel2(game, 600f, 360f)
        }
    }

    override fun show() {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {

    }
}