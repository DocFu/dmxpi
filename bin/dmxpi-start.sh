#!/bin/bash
sudo java -Dpi4j.linking=dynamic -classpath .:classes:/opt/pi4j/lib/'*' -jar dmxpi-app.jar
 