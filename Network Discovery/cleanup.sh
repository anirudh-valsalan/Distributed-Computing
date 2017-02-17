#!/bin/bash


# Change this to your netid
netid=axk153230

#
# Root directory of your project
PROJDIR=$HOME/AOS

#
# Directory where the config file is located on your local system
CONFIGLOCAL=$HOME/AOS/config.txt

n=0

cat $CONFIGLOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
    echo "Host:$i"
    while [ $n -lt $i ]
    do
    	read line
        host=$( echo $line | awk '{ print $2 }' )

        echo "Host:$host"
        ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $netid@$host killall -u $netid &
        sleep 1

        n=$(( n + 1 ))
    done
   
)


echo "Cleanup complete"
