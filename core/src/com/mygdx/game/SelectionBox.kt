package com.mygdx.game

class SelectionBox() : com.badlogic.gdx.math.Rectangle() {
    val selectionBoxWidth = 150f
    val selectionBoxHeight = 50f

    fun moveSelectionboxDown() {
            y -= 50f
    }
    fun moveSelectionboxUp() {
            y += 50f}
}