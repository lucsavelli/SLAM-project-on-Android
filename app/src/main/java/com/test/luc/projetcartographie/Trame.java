package com.test.luc.projetcartographie;

/**
 * Trame envoyée par le Robot (simulée pour l'instant)
 */
public class Trame {

    private double posXElement;
    private double posYElement;

    public double getPosXElement(){
        return posXElement;
    }
    public double getPosYElement(){
        return posYElement;
    }

    public void setPosXElement(double newPosX){
        this.posXElement = newPosX;
    }
    public void setPosYElement(double newPosY){
        this.posYElement = newPosY;
    }

}
