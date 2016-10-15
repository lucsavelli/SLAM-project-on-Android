package com.test.luc.projetcartographie;

import android.graphics.Color;

/**
 * Classe Objet : Robot (représenté par un cercle)
 */
public class Robot {


    public static float RAYON = Config.RAYON_APPLI_ROBOT;

    private static int HauteurEcran = 0;
    private static int LargeurEcran = 0;

    public boolean posInitialized = false;

    //*****COULEUR*****//
    private int couleurRobot = Color.BLACK;

    public int getCouleurRobot() {
        return couleurRobot;
    }


    //*****COORDONNEES*****//
    private float posX = 0;
    private float posY = 0;
    private float angle = 0;

    //Récupération des coordonnées
    public float getPosX() {
        return posX;
    }
    public float getPosY() {
        return posY;
    }
    public float getAngle(){return angle;}

    //MAJ des coordonnées
    public void setPosX(float newPosX) {
        this.posX = newPosX;
    }
    public void setPosY(float newPosY) {
        this.posY = newPosY;
    }
    public void setAngle(float newAngle){this.angle = newAngle;}


}