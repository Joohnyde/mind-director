/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cevapinxile.minddirector.handlers;

import com.cevapinxile.minddirector.obs.ObsService;

/**
 *
 * @author denijal
 */
public interface EventHandler {

  void handlePlayerPick(int index);

  default boolean handleReturn() {
    ObsService.getInstance().commit();
    return true;
  }

  default void onEnter() {
    System.out.println("ENTER: " + this.getClass().getCanonicalName());
  }

  default void onExit() {
    System.out.println("EXIT: " + this.getClass().getCanonicalName());
    //TODO: Reset UI state
  }

}
