# SLAM-project-on-Android
2D SLAM (Simultaneous Localisation and Mapping) on an Android device.

### The mapping robot
The robot is driven by a **dsPic33 microcontroller**, and gets distance and angle environnement data from its **360° - 6hz LIDAR sensor**. The robot communicates with the android device through a **bluetooh transmitter**.


![robot](https://cloud.githubusercontent.com/assets/21124351/19410187/d3b4f364-92e5-11e6-8962-830e403a69e2.jpg)

### The Android app - our work

![galaxytab2](https://cloud.githubusercontent.com/assets/21124351/19410188/d76771ee-92e5-11e6-88a7-37ae7ebc0958.jpeg)

This android app gets data from the robot (bluetooth communication). The robot send two different types of frames : LIDAR data frames and robot location frames.

The app opens a Bluetooth socket to receive robot data. Once connected, it decodes the received frames, and real-time display the robot location, its orientation, and the robot environnement discovered by the LIDAR.


![terrain](https://cloud.githubusercontent.com/assets/21124351/19410190/dbcafb7a-92e5-11e6-8725-482abe0237fb.jpg)

The app is able to detect LIDAR spurious data : every 5 seconds, all measured points with an occurence lower than a fixed level are removed. 


Below the map displayed as seen by the robot : red points have a too weak occurence.

![carto1](https://cloud.githubusercontent.com/assets/21124351/19410192/de341586-92e5-11e6-83aa-03a499828aff.png)


A few moments later, the red points have been removed.

![carto2](https://cloud.githubusercontent.com/assets/21124351/19410194/e19973f6-92e5-11e6-82e7-cabddc2f72bd.png)




*You will find the sourcecode of the project in the **app/src/main/java/com/test/luc/projetcartographie/** directory.* 




