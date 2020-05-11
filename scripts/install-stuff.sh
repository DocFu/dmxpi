#!/bin/bash

apt-get update
apt-get install vim

# Installs openjdk version "11.0.7" 2020-04-14
# OpenJDK Runtime Environment (build 11.0.7+10-post-Raspbian-3deb10u1)
# OpenJDK Server VM (build 11.0.7+10-post-Raspbian-3deb10u1, mixed mode)
apt-get install default-jdk

# http://wiringpi.com/download-and-install/
apt-get install wiringpi

# https://pi4j.com/1.2/install.html
curl -sSL https://pi4j.com/install | sudo bash

