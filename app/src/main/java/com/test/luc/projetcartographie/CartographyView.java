package com.test.luc.projetcartographie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * MOTEUR GRAPHIQUE
 */
public class CartographyView extends SurfaceView implements SurfaceHolder.Callback {


    SurfaceHolder gSurfaceHolder;
    //Le thread qui gère le dessin
    DrawingThread gThread;
    //Le pinceau/palette
    Paint gPaint = null;
    //Le robot
    Robot gRobot = null;
    //Le terrain discrétisé
    List<Element> gTerrain = null;
    //Element utilisé pour fixer la taille
    Element gElement;
    //Moteur de calcul
    CartographyEngine gEngine;



    //*****Méthodes pour faire le lien avec l'activité*****//

    public void setRobot(Robot aRobot){
        this.gRobot = aRobot;
    }
    public void setEngine(CartographyEngine aEngine){
        this.gEngine = aEngine;
    }




    //*****Constructeur de la classe*****//

    public CartographyView(Context context){
        super(context); // On utilise le context comme la classe SurfaceView
        gSurfaceHolder = getHolder();
        gSurfaceHolder.addCallback(this); //On associe un holder à la surface view et on permet les callbacks

        gThread = new DrawingThread();

        gPaint =  new Paint();
        if(Config.CHOIX_STYLE == 0) {
            gPaint.setStyle(Paint.Style.STROKE);
        }
        else{
            gPaint.setStyle(Paint.Style.FILL);
        }
        gRobot = new Robot();
    }


    //*****DESSIN*****//
    @Override
    protected void onDraw(Canvas gCanvas) {
        // Dessiner le fond de l'écran en premier
        gCanvas.drawColor(Color.LTGRAY);

        if(gTerrain != null){
            //Dessiner tous les éléments
            for(Element e : gTerrain) {
                if(e.getCompteur() == 0){
                    gPaint.setColor(Color.GRAY);
                }

                else if(e.getCompteur() < 3){
                    gPaint.setColor(Color.RED);
                }

                else if(e.getCompteur() < 10){
                    gPaint.setColor(Color.BLUE);
                }

                else {
                    gPaint.setColor(Color.GREEN);
                }

                gCanvas.drawRect(e.getRectangle(), gPaint); //On dessine le carré avec la bonne couleur

            }
        }

        if(gRobot != null){
            gPaint.setColor(gRobot.getCouleurRobot());
            if(gRobot.posInitialized == true) {
                //Dessin du cercle qui représente le contour du Robot
                gCanvas.drawCircle(gRobot.getPosX(), gRobot.getPosY(), Robot.RAYON, gPaint);
                //Dessin du triangle qui indiqe l'orientation du robot
                gPaint.setStrokeWidth(2);
                gPaint.setColor(Color.YELLOW);
                Path path = new Path();
                path.moveTo((float) (gRobot.getPosX() + Robot.RAYON * Math.cos(gRobot.getAngle() * Math.PI / 180)), (float) (gRobot.getPosY() - Robot.RAYON * Math.sin(gRobot.getAngle() * Math.PI / 180)));
                path.lineTo((float) (gRobot.getPosX() + (Robot.RAYON/4) * Math.cos((gRobot.getAngle() + 90) * Math.PI / 180)), (float) (gRobot.getPosY() - (Robot.RAYON/4) * Math.sin((gRobot.getAngle()+90) * Math.PI / 180)));
                path.lineTo((float) (gRobot.getPosX() + (Robot.RAYON/4) * Math.cos((gRobot.getAngle()-90) * Math.PI / 180)), (float) (gRobot.getPosY() - (Robot.RAYON/4) * Math.sin((gRobot.getAngle() - 90) * Math.PI / 180)));
                path.close();
                gCanvas.drawPath(path, gPaint);

            }
        }
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gThread.keepDrawing = true;
        gThread.start();

        if(gRobot != null) {
            gEngine.setDiscretisationTerrain(gTerrain); //On met en commun le terrain discretisé (au moteurs graphique et physique)
            gTerrain = this.gEngine.InitialisationTerrain();

        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gThread.keepDrawing = false;

        boolean joined = false;
        while (!joined) {
            try {
                gThread.join();
                joined = true;
            } catch (InterruptedException e) {
            }
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Que faire quand le surface change ? (L'utilisateur tourne son téléphone par exemple), non utile ici...
    }


    //*****Thread d'affichage graphique*****//
    public class DrawingThread extends Thread {
        //Variable utilisée pour arrêter le dessin
        public boolean keepDrawing = true;

        @Override
        public void run() {
            while (keepDrawing) {
                Canvas canvas = null;

                try {
                    // On récupère le canvas pour dessiner dessus
                    canvas = gSurfaceHolder.lockCanvas();
                    // On s'assure qu'aucun autre thread n'accède au holder
                    synchronized (gSurfaceHolder) {
                        // Et on dessine
                        onDraw(canvas);
                    }
                } finally {
                    // Notre dessin fini, on relâche le Canvas pour que le dessin s'affiche
                    if (canvas != null)
                        gSurfaceHolder.unlockCanvasAndPost(canvas);
                }


                // Pour dessiner à 50 fps et ne pas surcharger le processeur
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }

            }
        }
    }




}
