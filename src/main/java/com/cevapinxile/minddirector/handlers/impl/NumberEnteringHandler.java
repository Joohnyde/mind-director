/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.gui.MainController;
import com.cevapinxile.minddirector.handlers.EditStageHandler;
import com.cevapinxile.minddirector.model.CardNode;
import com.cevapinxile.minddirector.model.EditStage;
import com.cevapinxile.minddirector.util.CardLabel;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.util.ArrayList;

/**
 *
 * @author denijal
 */
public class NumberEnteringHandler implements EditStageHandler {

    private final StringBuilder number = new StringBuilder();
    private CardNode oldCard = null;
    private CardLabel selectedCard = null;
    private int playerIndex = -1;

    @Override
    public EditStage handleClick(int keyCode) {
        if (NativeKeyEvent.VC_1 <= keyCode && keyCode <= NativeKeyEvent.VC_0) {
            final int digit = (keyCode - 1) % 10;
            number.append(digit);
        }

        if (keyCode == NativeKeyEvent.VC_BACKSPACE) {
            number.setLength(Math.max(number.length() - 1, 0));
        }
        selectedCard.showText(number.toString());
        if (keyCode == NativeKeyEvent.VC_ESCAPE) {
            if(this.oldCard == null){
                MainController.removeMock(playerIndex);
            }else {
                selectedCard.showText(selectedCard.card + "");
            }
            EditStage.CARD_SELECTION.getHandler().enter(null);
            return EditStage.CARD_SELECTION;
        }

        if (keyCode == NativeKeyEvent.VC_ENTER) {
            ArrayList<Object> args = new ArrayList();
            args.add(number.toString());
            args.add(this.oldCard);
            EditStage.CARD_SELECTION.getHandler().enter(args);
            return EditStage.CARD_SELECTION;
        }
        
        return EditStage.NUMBER_ENTERING;
    }

    @Override
    public void enter(Object args) {
        Object[] arguments = (Object[]) args;
        playerIndex = (int) arguments[1];
        this.oldCard = (CardNode) arguments[0];
        if(this.oldCard == null){
            selectedCard = MainController.mockCard(playerIndex);
        } else {
            selectedCard = this.oldCard.labels.getFirst();
        }
        selectedCard.showText("");
        this.number.setLength(0);
    }

    
}
