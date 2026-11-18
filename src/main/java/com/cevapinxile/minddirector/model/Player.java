/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.model;

import com.cevapinxile.minddirector.util.CardLabel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 *
 * @author denijal
 */
public class Player {
    public String name, avatar, flag;
    @JsonIgnore
    public CardNode cards = new CardNode();

    public Iterator<CardLabel> cardsInOrder() {
        CardNode root = cards;
        TreeSet<Integer> keys = new TreeSet<>();
        HashMap<Integer, LinkedList<CardLabel>> result = new HashMap<>();
        dfs(root, 0, keys, result);
        return keys.stream().map(result::get).flatMap(p -> p.stream()).iterator();
    }
    
    private void dfs(CardNode root, int level, TreeSet<Integer> keys, HashMap<Integer, LinkedList<CardLabel>> result){
        if(root == null) return;
        if(!root.labels.isEmpty()){
            keys.add(level);
            LinkedList<CardLabel> list = result.getOrDefault(level, new LinkedList<>());
            list.addAll(root.labels);
            result.putIfAbsent(level, list);
        }
        for(CardNode child : root.children){
            dfs(child, level+1,keys,result);
        }
    }
}
