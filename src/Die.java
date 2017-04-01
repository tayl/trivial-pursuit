/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daniel
 */
public class Die {

    //roll's the die
    public static int rollThatSucker()
    {
        int roll = (int)(Math.random() * 6 + 1);
        return roll;
    }
}

