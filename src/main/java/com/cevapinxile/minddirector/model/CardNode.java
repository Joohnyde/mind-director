/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.model;

/**
 *
 * @author denijal
 */
public class CardNode {
    public int instances = 0;
    public int digit = -1;
    public CardNode[] children = new CardNode[10];
    public CardNode parent = null;
    public int childrenCount = 0;
    
    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        CardNode ptr = this;
        while(ptr != null && ptr.digit != -1 && ptr.parent != null){
            sb.append(ptr.digit);
            ptr = ptr.parent;
        }
        return sb.reverse().toString();
    }
}
