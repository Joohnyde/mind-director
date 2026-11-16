/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cevapinxile.minddirector.handlers;

import com.cevapinxile.minddirector.model.EditStage;

/**
 *
 * @author denijal
 */
public interface EditStageHandler {

   EditStage handleClick(int keyCode);
   
   void enter(Object args);
}
