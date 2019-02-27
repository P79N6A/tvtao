#!/usr/bin/env bash

echo "(1) 被动推送 push patch to local "

adb push app/build/outputs/tpatch-debug/update-4.8.0.json /sdcard/Android/data/com.yunos.tvtaobao/cache/update.json
adb push app/build/outputs/tpatch-debug/patch-4.8.1@4.8.0.tpatch /sdcard/Android/data/com.yunos.tvtaobao/cache