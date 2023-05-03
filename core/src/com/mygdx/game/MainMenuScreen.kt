package drop

import com.badlogic.drop.Drop
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.mygdx.game.Fireball
import com.mygdx.game.SelectionBox
import com.mygdx.game.screens.ScroogleScreen
import com.mygdx.game.screens.ScroogleScreenLevel2

class MainMenuScreen(private val game: Drop) : Screen {

    private var camera: OrthographicCamera = OrthographicCamera()

    init {
        camera.setToOrtho(false, 800f, 480f)
    }

    private val batch: SpriteBatch

    private val selectionBoximg: Texture
    private val selectionBoxes: MutableList<SelectionBox>
    private var selectionbox1 = SelectionBox()


    init {
        batch = SpriteBatch()
        selectionBoximg = Texture("menu/selectBox1.png")
        selectionBoxes = mutableListOf()
        selectionbox1.x = 325f
        selectionbox1.y = 225f
        selectionbox1.width = selectionbox1.selectionBoxWidth
        selectionbox1.height = selectionbox1.selectionBoxHeight
        selectionBoxes.add(selectionbox1)
    }


    override fun render(delta: Float) {
//        ScreenUtils.clear(0f, 0f, 0.2f, 1f);

        camera.update();
        game.batch?.projectionMatrix = camera.combined;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin();
        selectionBoxes.forEach { selectionBox ->
            batch.draw(selectionBoximg,
                    selectionBox.x, selectionBox.y,
                    selectionBox.width, selectionBox.height)
        }
        print(selectionBoxes.toString() + "--------------")
        batch.end();


        game.batch?.begin();
        game.font?.draw(game.batch, "Press enter to select a level ", 315f, 300f);
        game.font?.draw(game.batch, "Level 1", 375f, 250f);
        game.font?.draw(game.batch, "Level 2", 375f, 200f);
        game.batch?.end();

        checkSelectBoxMoved()

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectionBoxes.get(0).y > 200) {
                game.screen = ScroogleScreen(game, 600f, 360f)
            }
            if (selectionBoxes.get(0).y < 200) {
                game.screen = ScroogleScreenLevel2(game, 600f, 360f)
            }
        }
    }

    private fun checkSelectBoxMoved() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            selectionBoxes.forEach { selectionbox ->
                if (selectionbox.y < 200f) {
                    selectionbox.moveSelectionboxUp()
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            selectionBoxes.forEach { selectionbox ->
                if (selectionbox.y > 200f) {
                    selectionbox.moveSelectionboxDown()
                }
            }
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