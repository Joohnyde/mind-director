/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.controllers;

import com.cevapinxile.minddirector.model.CardNode;
import com.cevapinxile.minddirector.model.EditStage;
import com.cevapinxile.minddirector.model.GameState;

/**
 *
 * @author denijal
 */


public class GameController {
    private static final GameController INSTANCE = new GameController();

    private final InputController inputController = InputController.getInstance();
    private EditStage stage = EditStage.PLAYER_SELECTION;

    private GameController() {
    }

    public static GameController getInstance() {
        return INSTANCE;
    }


    public void handleKey(int keyCode) {
        //Delegate keyCode
        final EditStage newStage = stage.getHandler().handleClick(keyCode);
        if(newStage == null){
            inputController.idle();
            return;
        }
        stage = newStage;        
    }
    
    public void deleteCard(final CardNode toRemove) {
        _d(toRemove);
        GameState.printTree();
    }
    private void _d(final CardNode toRemove){
        if (toRemove.digit == -1 || toRemove.parent == null) {
            return;
        }
        
        toRemove.instances = Math.max(0, toRemove.instances - 1);
        if (toRemove.instances != 0) {
            return;
        }
        
        CardNode parent = toRemove.parent;
        if (parent.children[toRemove.digit] != toRemove) {
            return;
        }
        
        parent.children[toRemove.digit] = null;
        parent.childrenCount = Math.max(0, parent.childrenCount - 1);
        
        if (parent.childrenCount != 0) {
            return;
        }
        _d(parent);
    }

    public void addCard(String toAdd, int playerIndex) {
        if(toAdd == null || toAdd.isBlank() || playerIndex == -1) return;
        CardNode parent = GameState.PLAYERS.get(playerIndex).cards;
        for(char digitChar : toAdd.toCharArray()){
            int digit =  digitChar - '0';
            CardNode nextNode = parent.children[digit];
            if(nextNode != null){
                parent = nextNode;
                continue;
            }
            CardNode newNode = new CardNode();
            newNode.digit = digit;
            newNode.parent = parent;
            parent.childrenCount++;
            parent.children[digit] = newNode;
            parent = newNode;            
        }
        parent.instances++;
        
        
        GameState.printTree();
    }
}
