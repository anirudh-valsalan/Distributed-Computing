#!/bin/bash

# Change this to your netid
netid=axk153230

# Root directory of your project
PROJDIR=$HOME/AOS

# Directory where the config file is located on your local system
CONFIGLOCAL=$PROJDIR/config.txt

# Directory your java classes are in
BINDIR=$PROJDIR/bin

# Your main project class
PROG=Driver

n=0

cat $CONFIGLOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
    echo $i
    while [ $n -lt $i ] 
    do
    	read line
    	n=$( echo $line | awk '{ print $1 }' )
        host=$( echo $line | awk '{ print $2 }' )
	
		ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $netid@$host java -cp $BINDIR $PROG $n $CONFIGLOCAL&

        n=$(( n + 1 ))
    done
)
