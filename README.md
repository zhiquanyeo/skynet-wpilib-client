# WPILibJ -> Skynet Bridge

Skynet is a protocol for the control of small, *non-lethal* robots. It is implemented over [MQTT](http://mqtt.org) and uses a set of topics to communicate the state of a robot and it's I/O ports.

There are 2 parts to a Skynet system: A client, and an endpoint. The endpoint is whatever device/program you wish to control. For example:
- Small physical robot
- Large pysical robot
- Robot simulator
- Simple diagnostic screen

These will all implement the **endpoint** portion of the protocol. 

The other component is the client. This is basically the "brains" of the operation and can be implemented in many different ways. For example:
- Simple program that translates joystick movements into Skynet commands
- Script that sends a bunch of Skynet commands
- Libraries that integrate with existing frameworks (like this one!)

The [WPILibJ](http://first.wpi.edu/FRC/roborio/release/docs/java/) -> Skynet Bridge is an example of integrating an existing robot framework (in this case, the one used for FIRST Robotics Competitions) with the Skynet protocol. The library converts an FRC robot program into a Skynet Client, and enables it to send and receive commands to/from an endpoint. The library also works with SmartDashboard and NetworkTables, allowing teams to use all the functionality that they are used to on the RoboRIO in a diverse number of endpoints.

## Using in FRC Robot Programs
First, gather all your required material:
- wpilib-skynet.jar from the [Releases](https://github.com/zhiquanyeo/skynet-wpilib-client/releases) section
- skynet-build.properties and skynet-build.xml files from the repo
- A MQTT broker like [Mosquitto](http://mosquitto.org/)
- Some sort of Skynet Endpoint, for example the skynet-vrep example in Releases. See below for more information
- FRC Driver Station

Copy the wpilib-skynet.jar, skynet-build.properties and skynet-build.xml files into the root folder of your target FRC project. Next, fire up the MQTT broker and endpoint. Then, in Eclipse, right click on skynet-build.xml -> Run as Ant Build. This should start up the program and show a UI that lets you connect to an arbitrary endpoint broker. If you're running this on a local machine, leave everything at the defaults, and then click "Connect". If all goes well, the status message should read "Connected".

Fire up the Driver Station at this point. The 2 status indicators for Communication and Robot Code should be green. You can then use the Driver Station to activate the different modes of your robot program (teleop, autonomous, etc), and see results on the endpoint.

#### Using the Example V-REP Endpoint
[V-REP](http://www.coppeliarobotics.com/) is a cross platform robot simulator that allows control over the simulation via remote API. The Releases section of this repo has a ZIP file (skynet-vrep) that contains a V-REP simulation scene containing one robot, and an Skynet compatible endpoint. To run this endpoint, do the following:
1. Load up the scene (frcdrive.ttt file) in V-REP and start it
2. Run the .jar file on the commandline and pass it frcdrive.json as the first argument (java -jar skynet-vrep-endpoint.jar frcdrive.json)
3. Use the WPILib->Skynet Bridge to control the on-screen robot

## Building
The repo contains a recent implementation of WPILibJ with modifications to support the Skynet protocol. Load it up in Eclipse and generate a runnable JAR, and you're all set.

To use in robot programs, generate a runnable JAR, then copy skynet-build.properties, skynet-build.xml and the JAR file into the root folder of target project
