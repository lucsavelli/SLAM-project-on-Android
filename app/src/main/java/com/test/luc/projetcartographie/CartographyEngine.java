package com.test.luc.projetcartographie;



import com.test.luc.library.BluetoothSPP;
import java.util.ArrayList;
import java.util.List;

/**
 * MOTEUR DE CALCUL / SIMULATEUR
 */
public class CartographyEngine {

    private Robot eRobot;
    private List<Element> DiscretisationTerrain;
    private Trame TrameSimu;
    private BluetoothSPP bt;
    private Config eConfigurationSimu;

    //RandomThread myThread;
    DataProcessingThread myThread2;

    public static final int ROBOT_PROTOCOL = 0x0061; /**correspond à  */
    public static final int LIDAR_PROTOCOL = 0x0070;
    public static final int RECEPTION_WAIT = 0;
    public static final int RECEPTION_FUNCTION_MSB = 1;
    public static final int RECEPTION_FUNCTION_LSB = 2;
    public static final int RECEPTION_PAYLOAD_LENGTH_MSB = 3;
    public static final int RECEPTION_PAYLOAD_LENGTH_LSB = 4;
    public static final int RECEPTION_PAYLOAD = 5;
    public static final int RECEPTION_CHECKSUM = 6;
    public static final int RECEPTION_MAX_PAYLOAD_LENGTH = 160;

    int receptionState = RECEPTION_WAIT;
    int receivedFunction;
    int receivedPayloadLength;
    int receivedPayload[]= new int[RECEPTION_MAX_PAYLOAD_LENGTH];
    int receivedPayloadIndex = 0;
    int receivedChecksum;
    int calculatedChecksum;


    private byte[] donneesBluetooth;

    private boolean flag = true; //Flag signalant l'arrivée de data sur le DataReceivedListener
    public boolean testFlag = true;
    private int k = 0; //Compteur
    private int l=0; //Compteurs nettoyage
    private int m=0;


    float xPosf = 0;
    float yPosf = 0;
    int angleRobotdeg = 0;

    double xPos2 = 0;
    double yPos2 = 0;
    int distanceobstacle = 0;
    int xPosElement = 0;
    int yPosElement = 0;




    //*****Méthodes pour faire lien avec les autres classes******//
    public void setDiscretisationTerrain(List<Element> discretisationTerrain) {
        DiscretisationTerrain = discretisationTerrain;
    }

    public void setRobot(Robot robot){
        this.eRobot = robot;
    }


    public void setConfig(Config ConfigSimu)    {
        this.eConfigurationSimu = ConfigSimu;
    }

    public void setBluetoothSPP(BluetoothSPP bSPP) {
        this.bt = bSPP;

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                donneesBluetooth = data;
                flag = !flag;
            }
        });
    }



    //*****Constructeur de la classe*****//
    public CartographyEngine(){
        myThread2 = new DataProcessingThread(); // On crée une nouvelle instance de la classe DataProcessingThread
        myThread2.start();
        TrameSimu = new Trame();  //Fonctionne, mais Objet trame devenu inutile ! (à terme le supprimer)
        eConfigurationSimu = new Config();
    }


    private int CalculateChecksum(int msgFunction, int msgPayloadLength, int msgPayload[] ) {
        int checksum = 0;
        int i = 0;
        checksum = checksum ^ 0xFE;
        checksum = checksum ^ msgFunction >>8;
        checksum = checksum ^ msgFunction;
        checksum = checksum ^ msgPayloadLength>>8;
        checksum = checksum ^ msgPayloadLength;
        for(i = 0; i < msgPayloadLength; i++)
        {
            checksum = checksum ^ msgPayload[i];
        }
        return checksum;

    }

    private void DecodeRobotMessage(int c){
        switch (receptionState)
        {
            //char calculatedChecksum;

            case RECEPTION_WAIT:
                if (c == 0xFE)
                    receptionState = RECEPTION_FUNCTION_MSB;
                break;

            case RECEPTION_FUNCTION_MSB:
                receivedFunction = (c << 8);
                receptionState = RECEPTION_FUNCTION_LSB;
                break;

            case RECEPTION_FUNCTION_LSB:
                receivedFunction +=  c;
                receptionState = RECEPTION_PAYLOAD_LENGTH_MSB;
                break;

            case RECEPTION_PAYLOAD_LENGTH_MSB:
                receivedPayloadLength = (c << 8);
                receptionState = RECEPTION_PAYLOAD_LENGTH_LSB;
                break;

            case RECEPTION_PAYLOAD_LENGTH_LSB:
                receivedPayloadLength +=  c;
                if (receivedPayloadLength > RECEPTION_MAX_PAYLOAD_LENGTH)
                    receptionState = RECEPTION_WAIT;
                else if (receivedPayloadLength == 0)
                    receptionState = RECEPTION_CHECKSUM;
                else
                    receptionState = RECEPTION_PAYLOAD;
                break;

            case RECEPTION_PAYLOAD:
                receivedPayload[receivedPayloadIndex] = c;
                receivedPayloadIndex++;
                if (receivedPayloadIndex == receivedPayloadLength)
                {
                    receivedPayloadIndex = 0;
                    receptionState = RECEPTION_CHECKSUM;
                }
                break;

            case RECEPTION_CHECKSUM:
                calculatedChecksum = CalculateChecksum(receivedFunction, receivedPayloadLength, receivedPayload);
                receivedChecksum = c;
                if (calculatedChecksum == receivedChecksum)
                {
                    UartProcessMessage(receivedFunction, receivedPayloadLength,receivedPayload);
                }
                receptionState = RECEPTION_WAIT;
                break;

            default:
                receptionState = RECEPTION_WAIT;
                break;
        }
    }


    void UartProcessMessage(int function, int payloadLength, int payload[])
    {

        int i,j;
        int xPos = 0;
        int yPos = 0;
        int angleDegre = 0;

        switch (function)
        {

            case ROBOT_PROTOCOL:

                for(i = 0;i < 4;i++) {
                    yPos += payload[3 - i] << (8*i); // On inverse x et y pour les besoins de l'affichage (tablette en mode paysage)
                    xPos += payload[7 - i] << (8*i);
                    angleDegre += payload[11 - i] << (8*i);
                }

                xPosf =  Robot.RAYON + (xPos * Config.FACTEUR_ECHELLE); // 50 mm taille d'une case, 50mm = R/4
                yPosf = Config.POSITION_Y_INITIALE + (yPos * Config.FACTEUR_ECHELLE);
                angleRobotdeg = angleDegre;

                eRobot.setPosX(xPosf);
                eRobot.setPosY(yPosf);
                eRobot.setAngle(angleRobotdeg);

                if(eRobot.posInitialized == false){
                    eRobot.posInitialized = true;
                }

                break;


            case LIDAR_PROTOCOL:

                for(j=0;j<150;j++){
                    distanceobstacle=payload[j];

                    if(distanceobstacle != 0) {
                        xPos2 = yPosf + (100 * Math.sin((angleRobotdeg - 14) * Math.PI / 180) - 10 * distanceobstacle * Math.cos((j - (angleRobotdeg - 14)) * Math.PI / 180)) * (Config.FACTEUR_ECHELLE);
                        yPos2 = xPosf + (100 * Math.cos((angleRobotdeg - 14) * Math.PI / 180) + 10 * distanceobstacle * Math.sin((j - (angleRobotdeg - 14)) * Math.PI / 180)) * (Config.FACTEUR_ECHELLE);
                    }

                    else{
                        //On conserve les valeurs du point précédent
                    }

                    TrameSimu.setPosXElement(yPos2); // On inverse x et y => mode paysage
                    TrameSimu.setPosYElement(xPos2);
                    MiseAJourTerrain(DiscretisationTerrain); //On met à jour le terrain
                    j++;
                }
                l++;
//                m++;




            default:
                break;
        }
    }




    void EncodeAndSendMessage(int msgFunction,int msgPayloadLength, byte msgPayload[])
    {
        byte[] trame = new byte[msgPayloadLength + 6];
        int pos = 0, i;
        trame[pos++] = -2; //OxFE en unsigned
        trame[pos++] = (byte) (msgFunction >> 8);
        trame[pos++] = (byte) (msgFunction);
        trame[pos++] = (byte) (msgPayloadLength >> 8);
        trame[pos++] = (byte) (msgPayloadLength);
        for (i = 0; i < msgPayloadLength; i++)
        {
            trame[pos++] = msgPayload[i];
        }

        //Conversion msgPayload en entier pour calculer le checksum...
        int[] payloadInt = new int[msgPayloadLength];
        for (i = 0; i < msgPayloadLength; i++)
        {
            payloadInt[i] = (msgPayload[i] & 0xFF); //Application masque
        }

        trame[pos] = (byte)(CalculateChecksum(msgFunction, msgPayloadLength, payloadInt) & 0xFF); //Conversion int ==> byte   validité ???
        bt.send(trame,true);
    }



    public List<Element> InitialisationTerrain() {
        DiscretisationTerrain = new ArrayList<Element>();
        int i, j;

        //2 boucles imbriquées pour créer tous les éléments (80x80)
        for (i = 0; i < Config.NOMBRE_ELEMENTS; i++) {
            for (j = 0; j < Config.NOMBRE_ELEMENTS; j++) {
                DiscretisationTerrain.add(new Element(j, i));
            }
        }

        return DiscretisationTerrain;
    }




            public void MiseAJourTerrain(List<Element> monTerrain) {

                double x = TrameSimu.getPosXElement() / (Element.LARGEUR);
                double y = TrameSimu.getPosYElement() / (Element.LARGEUR);

                x = Math.floor(x);
                y = Math.floor(y);

                xPosElement = (int)x;
                yPosElement = (int)y;


                //Conversion unité correcte...

                int i = (yPosElement-1)*Config.NOMBRE_ELEMENTS + xPosElement; //On récupère l'identifiant de l'élément dans la liste grâce à la formule qui va bien !

                monTerrain.get(i).IncrementerCompteur(); // on incrémente le compteur de l'élément concerné

                if(l>24){
                    for (Element el:monTerrain) {
                        if(el.getCompteur()<3){
                            el.ResetCompteur(); //On élimine les points parasites
                        }
                    }
                    l=0;
                }
        }



    ////////////////////////////////////////////
    //*****GESTION DES TRAMES DU ROBOT******////
    ////////////////////////////////////////////


    public class DataProcessingThread extends Thread {

        public void run() {

            int bCorrect;

            while (true) {
                try {
                    if(testFlag != flag) {  //Si un nouveau tableau de byte est arrivé
                        for(k = 0; k<donneesBluetooth.length; k++) {
                            bCorrect = (donneesBluetooth[k] & 0xFF); //On passe le byte en un int "unsigned"
                            DecodeRobotMessage(bCorrect); //
                        }
                        testFlag = !testFlag;
                    }


                } catch (Exception e) {
                    //???
                }


            }
        }


    }



}