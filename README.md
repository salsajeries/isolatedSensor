# How This Works
## Obtain IP Address for your Raspberry Pi

To locate the raspberry pi via SSH, you must first obtain the IP Address of the
Raspberry Pi. In the terminal, type the following command to retrieve the IP Address:

```ping <pi's name>.local```

If the raspberry pi is connected to the same network, you will see an 
IP Address similar to `192.168.1.241`. Save this for your <em>build.gradle</em>

## Build Program
Build the logic of your program under `src/main/java/org.example/My_Sensor.java`. Once complete, 
run the program in the `Main.java` file. 

## Configure build.gradle
In the `build.gradle`, file, the only (2) components you should really have to edit are:
- dependencies 
- Raspberry Pi Variables (rpiAddress and rPiDirectory)

Make sure to put whatever dependencies you need. 

For the Rasperry Pi variables, enter the IP Address from earlier for ``rpiAddress`` and the 
location of where you want to save the application on the raspberry pi for `rPiDirectory`.

## Build Project on Raspberry Pi
In the root directory of this application, run 

```./gradlew deployToPi```

This will build the application and move the build file over to the raspberry pi.

## Run Application
Open your terminal, and connect via SSH to the Raspberry Pi:

```ssh pi@192.168.1.241```

Next, navigate to the directory you created and run 

```java -cp "lib/*" org.example.Main```