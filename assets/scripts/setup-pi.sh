#!/bin/bash


echo "Imortant: Make sure SD Card partition space is extended before upgrading or installing packages. See raspi-config / Advanced!"
echo "Also don't forget to enable hdmi_force_hotplug=1 in /boot/config.txt to boot and start QLC+ without HDMI connected."

sleep 2

raspi-config

apt-get update
apt-get upgrade
apt-get install vim
apt-get install git

# Installs openjdk version "11.0.7" 2020-04-14
# OpenJDK Runtime Environment (build 11.0.7+10-post-Raspbian-3deb10u1)
# OpenJDK Server VM (build 11.0.7+10-post-Raspbian-3deb10u1, mixed mode)
apt-get install default-jdk

# http://wiringpi.com/download-and-install/
apt-get install wiringpi

# https://pi4j.com/1.2/install.html
curl -sSL https://pi4j.com/install | sudo bash



# Optional for building on pi
apt-get install maven
