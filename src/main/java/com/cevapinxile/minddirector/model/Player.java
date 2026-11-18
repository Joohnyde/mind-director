/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author denijal
 */
public class Player {
    public String name, avatar, flag;
    @JsonIgnore
    public CardNode cards = new CardNode();
}
