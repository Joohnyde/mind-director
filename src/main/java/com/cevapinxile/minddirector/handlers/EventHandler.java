/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cevapinxile.minddirector.handlers;

/**
 *
 * @author denijal
 */
public interface EventHandler {
    
    void handlePlayerPick(int index);
    
    boolean handleReturn();
    
    default void onEnter(){
        System.out.println("ENTER: "+this.getClass().getCanonicalName());
    }
    
    default void onExit(){
        System.out.println("EXIT: "+this.getClass().getCanonicalName());
        //TODO: Reset UI state
    }

}
