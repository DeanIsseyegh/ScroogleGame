package com.mygdx.game;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

class ScroogleScreenTest {

    public static void main(String args[]) {
        FireballPosition fireballPosition = new FireballPosition();

        float playStartPosition=15;
        float playerWidth=2;
        float fireballWidth = 10;

        float fireballStartX= fireballPosition.calculateFireballPositionX(fireballWidth,playerWidth,playStartPosition);

        System.out.println(11 == fireballStartX);
    }

}