#!/usr/bin/env bash

echo "(1) 构建修改后的模块, 同时把ap文件发布到本地仓库"
apVersion=4.8.0
newVersion=4.8.1

./gradlew clean assembleRelease -DapVersion=${apVersion} -DversionName=${newVersion}