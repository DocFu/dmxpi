#!/bin/bash

# Setting up output pins

echo "21" > /sys/class/gpio/export
echo "out" > /sys/class/gpio/gpio21/direction

echo "20" > /sys/class/gpio/export
echo "out" > /sys/class/gpio/gpio20/direction

echo "16" > /sys/class/gpio/export
echo "out" > /sys/class/gpio/gpio16/direction

echo "12" > /sys/class/gpio/export
echo "out" > /sys/class/gpio/gpio12/direction

echo "25" > /sys/class/gpio/export
echo "out" > /sys/class/gpio/gpio25/direction

echo "24" > /sys/class/gpio/export
echo "out" > /sys/class/gpio/gpio24/direction

echo "23" > /sys/class/gpio/export
echo "out" > /sys/class/gpio/gpio23/direction

echo "18" > /sys/class/gpio/export
echo "out" > /sys/class/gpio/gpio18/direction

echo "OUT: 21 20 16 12 25 24 23 18"

# Setting up input pins

echo "26" > /sys/class/gpio/export
echo "in" > /sys/class/gpio/gpio26/direction

echo "19" > /sys/class/gpio/export
echo "in" > /sys/class/gpio/gpio19/direction

echo "13" > /sys/class/gpio/export
echo "in" > /sys/class/gpio/gpio13/direction

echo "6" > /sys/class/gpio/export
echo "in" > /sys/class/gpio/gpio6/direction

echo "5" > /sys/class/gpio/export
echo "in" > /sys/class/gpio/gpio5/direction

echo "22" > /sys/class/gpio/export
echo "in" > /sys/class/gpio/gpio22/direction

echo "27" > /sys/class/gpio/export
echo "in" > /sys/class/gpio/gpio27/direction

echo "17" > /sys/class/gpio/export
echo "in" > /sys/class/gpio/gpio17/direction

echo "IN: 26 19 13 6 5 22 27 17"

# Lauflicht

echo "1" > /sys/class/gpio/gpio21/value
sleep 1
echo "0" > /sys/class/gpio/gpio21/value

echo "1" > /sys/class/gpio/gpio20/value
sleep 1
echo "0" > /sys/class/gpio/gpio20/value

echo "1" > /sys/class/gpio/gpio16/value
sleep 1
echo "0" > /sys/class/gpio/gpio16/value

echo "1" > /sys/class/gpio/gpio12/value
sleep 1
echo "0" > /sys/class/gpio/gpio12/value

echo "1" > /sys/class/gpio/gpio25/value
sleep 1
echo "0" > /sys/class/gpio/gpio25/value

echo "1" > /sys/class/gpio/gpio24/value
sleep 1
echo "0" > /sys/class/gpio/gpio24/value

echo "1" > /sys/class/gpio/gpio23/value
sleep 1
echo "0" > /sys/class/gpio/gpio23/value

echo "1" > /sys/class/gpio/gpio18/value
sleep 1
echo "0" > /sys/class/gpio/gpio18/value

# Alle an

echo "1" > /sys/class/gpio/gpio21/value
echo "1" > /sys/class/gpio/gpio20/value
echo "1" > /sys/class/gpio/gpio16/value
echo "1" > /sys/class/gpio/gpio12/value
echo "1" > /sys/class/gpio/gpio25/value
echo "1" > /sys/class/gpio/gpio24/value
echo "1" > /sys/class/gpio/gpio23/value
echo "1" > /sys/class/gpio/gpio18/value
sleep 3

# Alle aus

echo "0" > /sys/class/gpio/gpio21/value
echo "0" > /sys/class/gpio/gpio20/value
echo "0" > /sys/class/gpio/gpio16/value
echo "0" > /sys/class/gpio/gpio12/value
echo "0" > /sys/class/gpio/gpio25/value
echo "0" > /sys/class/gpio/gpio24/value
echo "0" > /sys/class/gpio/gpio23/value
echo "0" > /sys/class/gpio/gpio18/value


# Status input pins

cat /sys/class/gpio/gpio26/value
cat /sys/class/gpio/gpio19/value
cat /sys/class/gpio/gpio13/value
cat /sys/class/gpio/gpio6/value
cat /sys/class/gpio/gpio5/value
cat /sys/class/gpio/gpio22/value
cat /sys/class/gpio/gpio27/value
cat /sys/class/gpio/gpio17/value
sleep 1


# Releasing output pins

echo "20" > /sys/class/gpio/unexport
echo "16" > /sys/class/gpio/unexport
echo "12" > /sys/class/gpio/unexport
echo "25" > /sys/class/gpio/unexport
echo "24" > /sys/class/gpio/unexport
echo "23" > /sys/class/gpio/unexport
echo "18" > /sys/class/gpio/unexport


# Releasing input pins

echo "26" > /sys/class/gpio/unexport
echo "19" > /sys/class/gpio/unexport
echo "13" > /sys/class/gpio/unexport
echo "6" > /sys/class/gpio/unexport
echo "5" > /sys/class/gpio/unexport
echo "22" > /sys/class/gpio/unexport
echo "27" > /sys/class/gpio/unexport
echo "17" > /sys/class/gpio/unexport

echo "GPIO LED Test done!"

exit 0









