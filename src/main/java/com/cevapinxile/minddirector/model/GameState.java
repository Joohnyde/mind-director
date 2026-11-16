/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.model;

import java.util.ArrayList;

/**
 *
 * @author denijal
 */
public class GameState {
    
    public static final Integer NUMBER_OF_PLAYERS = 4;
    public static final ArrayList<Player> PLAYERS = new ArrayList<>();
    public static Integer lives = 4, shurikens = 2, level = 1;    
    
    public static void printTree(){
        for(Player p : PLAYERS){
            System.out.println(p.name+":");
            printCard(p.cards, 0);
        }
    }
    private static String getDigit(CardNode card){
        return card.digit + (card.instances <= 1 ? "" : " ("+card.instances+")");
    }
    private static void printCard(CardNode card, int level){
        for(int i = 0; i<level; i++)
            System.out.print("\t");
        System.out.print(((card.digit == -1) ? "x" : getDigit(card))+"\n");
        
        for(int i = 0; i<10;i++){
            if(card.children[i] != null){
                printCard(card.children[i], level+1);
            }
        }

        
    }
}
