package com.test.luc.projetcartographie;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.test.luc.library.BluetoothState;
import android.bluetooth.BluetoothAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * Activitée affiché au démarrage
 */
public class Main extends AppCompatActivity {

    Button scanBouton = null;
    EditText dimTerrain = null;
    EditText ppDetect = null;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    Config ConfigurationSimu = null;
    private Spinner liste = null;
    private Spinner liste2 = null;

    //BluetoothAdapter
    BluetoothAdapter mBtAdapter;

    int hauteurVue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        liste = (Spinner) findViewById(R.id.spinner1);

        List<String> choix = new ArrayList<String>();
        choix.add("GAUCHE");
        choix.add("CENTRE");
        choix.add("DROITE");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, choix);
        //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        liste.setAdapter(adapter);


        liste2 = (Spinner) findViewById(R.id.spinner2);

        List<String> choixStyle = new ArrayList<String>();
        choixStyle.add("Epuré");
        choixStyle.add("Plein");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, choixStyle);
        //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        liste2.setAdapter(adapter2);


        ConfigurationSimu = new Config();

        dimTerrain = (EditText) findViewById(R.id.sizeTerrain);
        ppDetect = (EditText) findViewById(R.id.ppdDetectable);
        scanBouton = (Button) findViewById(R.id.scanBtn);

        scanBouton.setOnClickListener(ScanButtonListener);


        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);


        mBtAdapter = BluetoothAdapter.getDefaultAdapter(); // Plus simple de le "redéclarer"

    }



    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        // the height will be set at this point
        final View myView = this.findViewById(android.R.id.content).getRootView();
        hauteurVue = myView.getMeasuredHeight(); // On récupère la hauteur de l'écran pour pouvoir adapter l'aafichage graphique à tout type d'appareil
    }



    private View.OnClickListener ScanButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(dimTerrain.getText().toString().matches("") || ppDetect.getText().toString().matches("")) {
                Toast.makeText(getApplicationContext()
                        , "Remplir tous les champs !"
                        , Toast.LENGTH_SHORT).show();
            }

            else if(Integer.parseInt(dimTerrain.getText().toString()) < 1000 || Integer.parseInt(dimTerrain.getText().toString()) > 10000) {
                Toast.makeText(getApplicationContext()
                        , "Taille terrain non valide !"
                        , Toast.LENGTH_SHORT).show();
            }

            else if(Integer.parseInt(ppDetect.getText().toString()) < 10 || Integer.parseInt(ppDetect.getText().toString()) > 500) {
                Toast.makeText(getApplicationContext()
                        , "Plus petite distance détectable non valide !"
                        , Toast.LENGTH_SHORT).show();
            }

            else{

                if (mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }
                // Request discover from BluetoothAdapter
                mBtAdapter.startDiscovery();

                Toast.makeText(getApplicationContext()
                        , "Scan en cours... peut prendre quelques secondes"
                        , Toast.LENGTH_LONG).show();

            }

        }
    };

    public void onStart() {
        super.onStart();
        //Si le bluetooth n'est pas en marche sur l'appareil, on demande l'autorisation de le lancer
        if (!mBtAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        }

    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {

                Toast.makeText(getApplicationContext()
                        , "Bluetooth activé !"
                        , Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        String test = new String();

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getName().contains("move")){  //Nom du Robot : Free2move

                    //On récupère les valeurs des EditText
                    int valDimTerrain = Integer.parseInt(dimTerrain.getText().toString());
                    int valPpDetect = Integer.parseInt(ppDetect.getText().toString());
                    //On récupère les valeurs des Spinner
                    int choixPosIni = (int)liste.getSelectedItemId();
                    int choixSyle = (int)liste2.getSelectedItemId();

                    //On met à jour la configuratiob
                    ConfigurationSimu.MAJConfig(valDimTerrain,valPpDetect,hauteurVue,choixPosIni, choixSyle);

                    //On lance CartographyActivity et on lui envoie le Device détecté
                    Intent intent3 = new Intent(getApplicationContext(),CartographyActivity.class);
                    intent3.putExtra("DetectedDevice", device);
                    startActivity(intent3);

                }





                }


            }



    };


}



