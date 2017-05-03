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
PROG=Verification

host=csgrads1.utdallas.edu
ssh $netid@$host java -cp $BINDIR $PROG
             

