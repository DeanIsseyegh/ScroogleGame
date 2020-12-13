package com.mygdx.game

import com.mygdx.game.ToxicBarrelAnimation
import java.awt.Rectangle

class Lakitu() : com.badlogic.gdx.math.Rectangle() {
    var bossHealth=1000f

    var direction = "left"


    fun moveLakitu(delta: Float) {
            if(direction=="left"){
                x-=200f*delta
            }
        else if (direction=="right"){
                x+=200f*delta

            }
    }
}