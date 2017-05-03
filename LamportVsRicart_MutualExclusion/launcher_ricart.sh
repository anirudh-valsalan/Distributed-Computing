#!/bin/bash


# Change this to your netid
netid=axk153230

#
# Root directory of your project
PROJDIR=$HOME/AOS3_Check

#
# This assumes your config file is named "config.txt"
# and is located in your project directory
#
CONFIG=$PROJDIR/config.txt

#
# Directory your java classes are in
#
BINDIR=$PROJDIR/bin

#
# Your main project class
#
PROG=Driver

n=0

cat $CONFIG | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read firstLine
    echo $firstLine

    numberOfServers=$( echo $firstLine | awk '{ print $1}' )

    while  [ $n -lt $numberOfServers ]
    do
        read line
        host=$( echo $line | awk '{ print $2 }' )
        ssh $netid@$host java -cp $BINDIR $PROG $n $CONFIG 1 > $PROJDIR/${n}.log &

        n=$(( n + 1 ))
    done
   
)
