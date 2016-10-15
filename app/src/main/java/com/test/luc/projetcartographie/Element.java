package com.test.luc.projetcartographie;


import android.graphics.RectF;

/**
 * Classe Objet : Element (élément de discrétisation du terrain)
 */
public class Element {

    public static float LARGEUR = Config.DIMENSION_APPLI_ELEMENT;

    //*****DEFINITION FORME*****//
    private RectF eRectangle = null;
    public RectF getRectangle(){
        return eRectangle;
    }

    //*****COMPTEUR*****/////
    private int compteur = 0;
    public void IncrementerCompteur(){
        this.compteur ++; // On incrémente à chaque fois qu'un nouveau point est détecté dans la zone délimitée par l'élément
    }
    public int getCompteur(){
        return compteur;
}

    public void ResetCompteur(){this.compteur = 0;}


    //*****Constructeur de la classe*****//
    public Element(int posX, int posY){
        this.eRectangle = new RectF(posX*LARGEUR, posY*LARGEUR, (posX+1)*LARGEUR, (posY+1)*LARGEUR);
    }

}
