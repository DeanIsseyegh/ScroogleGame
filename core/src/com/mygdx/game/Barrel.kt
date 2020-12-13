package com.mygdx.game

import com.badlogic.gdx.math.Rectangle

class Barrel() : Rectangle() {
    //    val width = 30f
//    val height = 50f
//    var animation= ToxicBarrelAnimation().createToxicBarrelAnimation()
//        set(value) {
//            field = value
//        }
//        get() = animation
//
//    var xPosition = xCoordinate
//        set(value) {
//            field = value
//        }
//        get() = xPosition
//
//    var yPosition = yCoordinate
//        set(value) {
//            field = value
//        }
//        get() = yPosition
    fun moveBarrel(delta: Float) {
        y -= 300f * delta
    }
}