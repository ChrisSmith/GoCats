#!/usr/bin/env bash
set -e

./build.sh

echo 'installing apk'

adb install -r app/build/outputs/apk/app-debug.apk

echo 'starting app'

adb shell am start -a android.intent.action.MAIN \
	-n org.collegelabs.gocats.app/org.collegelabs.gocats.app.MainActivity
