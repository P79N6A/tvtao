#!/bin/bash

echo "(1) 构建app,生成apk和ap, 同时把ap文件f发布到本地仓库"

./gradlew clean assembleRelease publish




./dbuild.sh -c all  -r             dmode release
./dbuild.sh -c all                 dmode debug

./dbuild.sh -c xiaomi -r -p         单独打包dmode

./dbuild.sh -y                              yunos debug
./dbuild.sh -y -q -r                        yunos release


/////////////////////////快速编译////////////////////////////
./fastdbuild.sh -c all  -r             dmode release
./fastdbuild.sh -c all                 dmode debug

./fastdbuild.sh -c xiaomi -r -p         单独打包dmode

./fastdbuild.sh -y                              yunos debug
./fastdbuild.sh -y -q -r                        yunos release
/////////////////////////快速编译////////////////////////////





echo "ap 模块发布到本地仓库成功 "


echo "(3) 安装 基线apk"

echo "adb install -r app/build/outputs/apk/app-release.apk"



adb install -r app/build/outputs/apk/app-release.apk