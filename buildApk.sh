#!/bin/bash

echo "(1) 构建app,生成apk和ap, 同时把ap文件f发布到本地仓库"

./gradlew clean assembleDebug publish

echo "ap 模块发布到本地仓库成功 "

echo "(3) 安装 基线apk"

echo "adb install -r app/build/outputs/apk/app-debug.apk"

adb install -r app/build/outputs/apk/app-debug.apk