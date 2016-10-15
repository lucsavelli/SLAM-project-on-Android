package com.test.luc.projetcartographie;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.test.luc.library.BluetoothSPP;
import com.test.luc.library.BluetoothState;

import java.io.Serializable;


public class CartographyActivity extends AppCompatActivity {



    //Moteur graphique
    CartographyView aView = null;
    //Moteur de calcul
    CartographyEngine aEngine = null;
    //Robot
    Robot aRobot = null;
    //BluetoothSPP
    BluetoothSPP bt;

    BluetoothAdapter mBtAdapter;

    Config ConfigurationSimu = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigurationSimu = new Config();

        aView = new CartographyView(this);

        setContentView(aView);

        aEngine = new CartographyEngine();

        //Envoi de trames de consignes de position au Robot sur click  (NON FONCTIONNEL, TRAMES PARTENT MAIS...)
        aView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int i = 0;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float xTouch = event.getX();
                    float yTouch = event.getY();

                    float xTouchmm = xTouch / ConfigurationSimu.FACTEUR_ECHELLE;
                    float yTouchmm = yTouch / ConfigurationSimu.FACTEUR_ECHELLE;

                    int xTouchint = (int)xTouchmm;
                    int yTouchint = (int)yTouchmm;

                    byte[] sentData = new byte[8];
                    int dataLenght = 8;

                    for (i = 0; i < 4; i++)
                    {
                        sentData[3-i] = (byte) ((xTouchint)>>(8 * i));
                        sentData[7-i] = (byte) ((yTouchint)>>(8 * i));
                    }

                    aEngine.EncodeAndSendMessage(0xA0,dataLenght,sentData); //On envoie la position du toucher écran

                }
                return true;
            }
        });


        aView.setEngine(aEngine); // On envoie le moteur physique au moteur graphique pour le tracé du terrain lors du SurfaceCreated

        aRobot = new Robot();
        aView.setRobot(aRobot);
        aEngine.setRobot(aRobot);

        mBtAdapter  = BluetoothAdapter.getDefaultAdapter();
    }


    public void onStart() {
        super.onStart();

        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // On récupère les objets envoyés par le main
        Intent i = getIntent();
        BluetoothDevice device = i.getExtras().getParcelable("DetectedDevice");

        bt = new BluetoothSPP(this);  // "this" => "getApplicationContext" ???
        //On initialise le BluetoothSPP
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_OTHER);
        aEngine.setBluetoothSPP(bt);

        bt.connect(device.getAddress()); //On lance la connexion au device
    }

}
