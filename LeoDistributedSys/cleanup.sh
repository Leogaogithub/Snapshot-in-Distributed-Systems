#!/bin/bash


# Change this to your netid
netid=lxg151530

#
# Root directory of your project
PROJDIR=$HOME/CS6378/Project1

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
PROG=Project1

n=0

cat $CONFIG | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
    echo $i
    totalNodes=$( echo $i | awk '{ print $1 }' )
    #echo $netId    
    for ((a=1; a <= $totalNodes ; a++))
    do
	read line
	echo $line
        host=$( echo $line | awk '{ print $2 }' ) 
	suffixHost='.utdallas.edu'
	host=$host$suffixHost
	ssh $netid@$host killall -u $netid &		
	#echo $n
        sleep 1
        n=$(( n + 1 ))
    done
)


echo "Cleanup complete"
