#!/bin/sh
channels=$(awk '{print $1 }' channels.lst)
#load channels from file

#for channel in ${channels[@]}; #print debug info
#do
#    echo $channel
#done

#################help msg######################
if [ $# -lt 1 ] ; then
    echo 'usage: dbuild.sh [-c <channel>] [-r] [-m] [-p] [-y]'
    echo 'use -c channel to choose channel, -c all to batch build all channels'
    echo '-p to add publish step, -r to add release flag'
    echo '-y to build yunos apk(dont append -c option)'
    echo '-m to merge bundle resources and package into main apk'
    exit 0
fi
###############################################

##############init configs#####################
debug=1
channel=""
publish=""
yunos=1
mode="debug"
merge=0
while getopts ":c:rpym" opt
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
    elif [ $opt = "m" ]; then
        merge=1
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
            echo "./gradlew -q clean assembleDebug $publish -Dmode=$mode -Dchannel=$c"
            if [ $merge -eq 1 ]; then
               echo "./gradlew -q clean assembleDebug $publish -Dmerge=true -Dmode=$mode -Dchannel=$c"
               ./gradlew -q clean assembleDebug $publish -Dmerge=true -Dmode=$mode -Dchannel=$c
            else
               echo "./gradlew -q clean assembleDebug $publish -Dmode=$mode -Dchannel=$c"
               ./gradlew -q clean assembleDebug $publish -Dmode=$mode -Dchannel=$c
            fi
        else
            if [ $merge -eq 1 ]; then
                echo "./gradlew -q clean assembleRelease -Dmerge=true -Dmode=$mode $publish -Dchannel=$c"
                ./gradlew -q clean assembleRelease $publish -Dmerge=true -Dmode=$mode -Dchannel=$c
            else
                echo "./gradlew -q clean assembleRelease $publish -Dmode=$mode -Dchannel=$c"
                ./gradlew -q clean assembleRelease $publish -Dmode=$mode -Dchannel=$c
            fi
        fi
   done
else
    if [ $debug -eq 1 ]; then
        if [ $merge -eq 1 ]; then
            echo "./gradlew -q clean assembleDebug -Dmode=$mode -Dmerge=true $publish $channelConfig"
            ./gradlew -q clean assembleDebug -Dmode=$mode -Dmerge=true $publish $channelConfig
        else
            echo "./gradlew -q clean assembleDebug -Dmode=$mode $publish $channelConfig"
            ./gradlew -q clean assembleDebug -Dmode=$mode $publish $channelConfig
        fi
    else
        if [ $merge -eq 1 ]; then
            echo "./gradlew -q clean assembleRelease -Dmode=$mode -Dmerge=true $publish $channelConfig"
            ./gradlew -q clean assembleRelease -Dmode=$mode -Dmerge=true $publish $channelConfig
        else
            echo "./gradlew -q clean assembleRelease -Dmode=$mode $publish $channelConfig"
            ./gradlew -q clean assembleRelease -Dmode=$mode $publish $channelConfig
        fi
    fi
fi

