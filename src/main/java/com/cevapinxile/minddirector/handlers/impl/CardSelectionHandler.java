/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.controllers.GameController;
import com.cevapinxile.minddirector.handlers.EditStageHandler;
import com.cevapinxile.minddirector.model.CardNode;
import com.cevapinxile.minddirector.model.EditStage;
import com.cevapinxile.minddirector.model.GameState;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.util.ArrayList;

/**
 *
 * @author denijal
 */
public class CardSelectionHandler implements EditStageHandler {

    private static final int VC_PLUS = 3662;
    private static final int VC_MINUS = 3658;
    
    private int playerIndex = -1;
    private CardNode selectedCard = null;
    private boolean dialogShown = false;

    private void selectCard(CardNode nextCard) {
        if (nextCard == null) {
            return;
        }
        if(selectedCard.instances != 0){
            System.out.println("Diselecting card: "+selectedCard);
            //TODO: [UI] Remove a potential border around nextCard
        }
        selectedCard = nextCard;
        if (selectedCard.instances != 0) {
            System.out.println("Selecting card: "+nextCard);
            //TODO: [UI] Put a border over the selected card
        }
    }

    @Override
    public EditStage handleClick(int keyCode) {
        if (selectedCard == null) {
            return EditStage.CARD_SELECTION;
        }

        if (dialogShown) {
            if (keyCode == NativeKeyEvent.VC_ENTER && selectedCard.instances != 0) {
                GameController.getInstance().deleteCard(selectedCard);
            } else {
                System.out.println("Hide dialog");
                //TODO: [UI] Hide dialog
            }
            this.dialogShown = false;
            return EditStage.CARD_SELECTION;
        }

        if (NativeKeyEvent.VC_1 <= keyCode && keyCode <= NativeKeyEvent.VC_0) {
            final int digit = (keyCode - 1) % 10;
            selectCard(selectedCard.children[digit]);
        }

        if (keyCode == NativeKeyEvent.VC_BACKSPACE) {
            selectCard(selectedCard.parent);
        }

        if (keyCode == NativeKeyEvent.VC_ESCAPE) {
            EditStage.PLAYER_SELECTION.getHandler().enter(null);
            return EditStage.PLAYER_SELECTION;
        }

        if (keyCode == NativeKeyEvent.VC_ENTER && selectedCard.instances != 0) {
            EditStage.NUMBER_ENTERING.getHandler().enter(selectedCard);
            return EditStage.NUMBER_ENTERING;
        }

        if (keyCode == VC_MINUS && selectedCard.instances != 0) {
            dialogShown = true;
            System.out.println("Show dialog");
            //TODO: [UI] Show 'Are you sure?' dialog
        }

        if (keyCode == VC_PLUS) {
            EditStage.NUMBER_ENTERING.getHandler().enter(null);
            return EditStage.NUMBER_ENTERING;
        }

        return EditStage.CARD_SELECTION;
    }

    @Override
    public void enter(Object args) {
        // Args is either an index of the player (coming from PlayerSelection)
        // array of number and optionally old card (changing from number Entering)
        // or null (canceling from numberEntering)
        if (args != null && args instanceof Integer) {
            playerIndex = (Integer) args;
            selectedCard = GameState.PLAYERS.get(playerIndex).cards;
        }
        if (args != null && args instanceof ArrayList) {
            ArrayList<Object> numbers = (ArrayList<Object>) args;
            final String toAdd = (String) numbers.get(0);
            final CardNode toRemove = (CardNode) numbers.get(1);
            if (toRemove != null) {
                GameController.getInstance().deleteCard(toRemove);
            }
            GameController.getInstance().addCard(toAdd, playerIndex);
            selectedCard = GameState.PLAYERS.get(playerIndex).cards;
        }
        dialogShown = false;
    }

}
