/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EditStageHandler;
import com.cevapinxile.minddirector.model.CardNode;
import com.cevapinxile.minddirector.model.EditStage;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.util.ArrayList;

/**
 *
 * @author denijal
 */
public class NumberEnteringHandler implements EditStageHandler {

    private final StringBuilder number = new StringBuilder();
    private CardNode oldCard = null;

    @Override
    public EditStage handleClick(int keyCode) {
        if (NativeKeyEvent.VC_1 <= keyCode && keyCode <= NativeKeyEvent.VC_0) {
            final int digit = (keyCode - 1) % 10;
            number.append(digit);
        }

        if (keyCode == NativeKeyEvent.VC_BACKSPACE) {
            number.setLength(Math.max(number.length() - 1, 0));
        }
        System.out.println("INPUT: "+number.toString());
        //TODO: [UI] Fill the number in the input field
        if (keyCode == NativeKeyEvent.VC_ESCAPE) {
            if(this.oldCard == null){
                System.out.println("Removing mocked card");
                //TODO: [UI] Remove created "mock" card
            }else {
                System.out.println("Returning oldcard");
                //TODO: [UI] Turn back oldCard into a card;
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
        this.oldCard = (CardNode) args;
        if(this.oldCard == null){
            System.out.println("Adding a mock card");
            //TODO: [UI] Add "mock" card
        } else {
            System.out.println("Turning a mock card into a field");
            //TODO: [UI] Turn oldCard into input field
        }
        this.number.setLength(0);
    }

    
}
