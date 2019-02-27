#!/bin/sh
channels=$(awk '{print $1 }' channels.lst)
#load channels from file

#for channel in ${channels[@]}; #print debug info
#do
#    echo $channel
#done

#################help msg######################
if [ $# -lt 1 ] ; then
    echo 'usage: dbuild.sh [-c <channel>] [-r] [-p] [-y]'
    echo 'use -c channel to choose channel, -c all to batch build all channels'
    echo '-p to add publish step, -r to add release flag'
    echo '-y to build yunos apk(dont append -c option)'
    exit 0
fi
###############################################

##############init configs#####################
debug=1
channel=""
publish=""
yunos=1
mode="debug"
while getopts ":c:rpy" opt
do
    if [ $opt = "c" ] ; then
        channel=$OPTARG
        yunos=0
        continue
    elif [ $opt = "r" ]; then
        debug=0
    elif [ $opt = "p" ]; then
        publish="publish"
    elif [ $opt = "y" ]; then
        yunos=1
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

if [ -z "$publish" ]; then
    echo "dont publish"
else
    echo "do publish"
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
            echo "./gradlew -q clean assembleDebug -PFastBuild=true $publish -Dmode=$mode -Dchannel=$c"
            ./gradlew -q clean assembleDebug -PFastBuild=true  $publish -Dmode=$mode -Dchannel=$c
        else
            echo "./gradlew -q clean assembleRelease -PFastBuild=true  -Dmode=$mode $publish -Dchannel=$c"
            ./gradlew -q clean assembleRelease -PFastBuild=true  -Dmode=$mode $publish -Dchannel=$c
        fi
   done
else
    if [ $debug -eq 1 ]; then
        echo "./gradlew -q clean assembleDebug -PFastBuild=true  -Dmode=$mode $publish $channelConfig"
        ./gradlew -q clean assembleDebug -PFastBuild=true  -Dmode=$mode $publish $channelConfig
    else
    echo "./gradlew -q clean assembleRelease -PFastBuild=true  -Dmode=$mode $publish $channelConfig"
        ./gradlew -q clean assembleRelease -PFastBuild=true  -Dmode=$mode $publish $channelConfig
    fi
fi

