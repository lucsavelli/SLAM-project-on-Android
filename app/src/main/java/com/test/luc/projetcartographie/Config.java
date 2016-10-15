package com.test.luc.projetcartographie;

/**
 * Classe contenant le variables de configuration
 */
public class Config {

    public static final int RAYON_REEL_ROBOT = 150; // mm
    public static int HAUTEUR_ECRAN = 1358; // pixels

    public static int DIMENSION_TERRAIN = 4000; // mm   -   (Terrain carré)
    public static int DIMENSION_REEL_ELEMENT = 200;//mm   -   (Elements carrés)

    public static float FACTEUR_ECHELLE = 0; // pixels/mm
    public static float RAYON_APPLI_ROBOT = 0; //pixels
    public static float DIMENSION_APPLI_ELEMENT = 0; //pixels
    public static int NOMBRE_ELEMENTS = 0;

    public static float POSITION_Y_INITIALE = 0;

    public static int CHOIX_STYLE = 0;




    //*****Mise à jour des variables de configuration*****//

    public void MAJConfig(int dimTerrain, int tailleElement, int hauteurEcran, int choixPosY, int choixStyle){
        DIMENSION_TERRAIN = dimTerrain + (int)(((float)(dimTerrain))*0.1);
        DIMENSION_REEL_ELEMENT = tailleElement;
        HAUTEUR_ECRAN = hauteurEcran - 60;

        FACTEUR_ECHELLE = ((float)HAUTEUR_ECRAN) / ((float)DIMENSION_TERRAIN);  ;
        RAYON_APPLI_ROBOT =   ((float)RAYON_REEL_ROBOT) * FACTEUR_ECHELLE;
        DIMENSION_APPLI_ELEMENT = DIMENSION_REEL_ELEMENT * FACTEUR_ECHELLE;
        NOMBRE_ELEMENTS = (int)(((float)DIMENSION_TERRAIN) / ((float)DIMENSION_REEL_ELEMENT));

        CHOIX_STYLE = choixStyle;

        switch(choixPosY){
            case 0:
                POSITION_Y_INITIALE = RAYON_APPLI_ROBOT;
            break;

            case 1:
                POSITION_Y_INITIALE = ((float)DIMENSION_TERRAIN)* FACTEUR_ECHELLE / 2;
            break;

            case 2:
                POSITION_Y_INITIALE = ((float)DIMENSION_TERRAIN)* FACTEUR_ECHELLE - RAYON_APPLI_ROBOT;
            break;
        }



    }



}
