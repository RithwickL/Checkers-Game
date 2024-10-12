/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.checkers;

/**
 *
 * @author Rithu
 */
public class Piece {
    private String color;
    private boolean isKing;

    public Piece(String color) {
        this.color = color;
        this.isKing = false;
    }

    public String getColor() {
        return color;
    }

    public void makeKing() {
        this.isKing = true;
    }

    public boolean isKing() {
        return isKing;
    }
}

