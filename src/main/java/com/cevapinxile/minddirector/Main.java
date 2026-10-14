/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.cevapinxile.minddirector;

import com.cevapinxile.minddirector.input.KeyboardListener;
import com.cevapinxile.minddirector.obs.ObsScene;
import com.cevapinxile.minddirector.obs.ObsService;

/**
 *
 * @author denijal
 */
public class Main {

    public static void main(String[] args) {
//        System.setProperty(
//          "org.slf4j.simpleLogger.log.io.obswebsocket.community.client",
//          "warn"
//        );
        KeyboardListener.getInstance().registerListener();
    }
}//C4231