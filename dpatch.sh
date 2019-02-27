#!/bin/sh
channels=$(awk '{print $1 }' channels.lst) #load channels from file

#for channel in ${channels[@]}; #print debug info
#do
#    echo $channel
#done

#################help msg######################
if [ $# -lt 1 ] ; then
    echo 'usage: dpatch.sh [-c <channel>] [-r] [-y] -b <baseVersion> -n <newVersion>'
    echo 'use -c channel to choose channel, -c all to batch build all channels'
    echo '-r to add release flag'
    echo '-y to build yunos apk(dont append -c option)'
    exit 0
fi
###############################################

##############init configs#####################
debug=1
channel=""
yunos=1
baseVersion=""
newVersion=""
mode="debug"
while getopts ":c:rpb:n:y" opt
do
    if [ $opt = "c" ] ; then
        channel=$OPTARG
        yunos=0
        continue
    elif [ $opt = "r" ]; then
        debug=0
    elif [ $opt = "y" ]; then
        yunos=1
    elif [ $opt = "b" ]; then
        baseVersion=$OPTARG
    elif [ $opt = "n" ]; then
        newVersion=$OPTARG
    fi
done

echo "channel: $channel"
if [ $debug -eq 1 ]; then
    echo "debugMode"
    mode="debug"
else
    echo "releaseMode"
    mode="release"
fi

channelConfig=""
echo $yunos
if [ $yunos -eq 0 ]; then
    channelConfig="-Dchannel=$channel"
fi
echo $channelConfig

####################################################################

if [ "all" = "$channel" ]; then
   echo "build all channels"
   for c in ${channels[@]};
   do
        if [ $debug -eq 1 ]; then
            echo "./gradlew -q clean assembleDebug -Dmode=$mode -Dchannel=$c -DapVersion=$baseVersion -DversionName=$newVersion"
            ./gradlew -q clean assembleDebug -Dmode=$mode -Dchannel=$c -DapVersion=$baseVersion -DversionName=$newVersion
        else
            echo "./gradlew -q clean assembleRelease -Dmode=$mode -Dchannel=$c -DapVersion=$baseVersion -DversionName=$newVersion"
            ./gradlew -q clean assembleRelease -Dmode=$mode -Dchannel=$c -DapVersion=$baseVersion -DversionName=$newVersion
        fi
   done
else
    if [ $debug -eq 1 ]; then
        echo "./gradlew -q clean assembleDebug -Dmode=$mode -Dchannel=$channel -DapVersion=$baseVersion -DversionName=$newVersion"
        ./gradlew -q clean assembleDebug -Dmode=$mode -Dchannel=$channel -DapVersion=$baseVersion -DversionName=$newVersion
    else
    echo "./gradlew -q clean assembleRelease -Dmode=$mode -Dchannel=$channel -DapVersion=$baseVersion -DversionName=$newVersion"
        ./gradlew -q clean assembleRelease -Dmode=$mode -Dchannel=$channel -DapVersion=$baseVersion -DversionName=$newVersion
    fi
fi

