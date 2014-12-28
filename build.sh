#!/usr/bin/env bash

set -e

if [ ! -f build.sh ]; then
	echo 'build.sh must be run from its root path'
	exit 1
fi

./genbindings.sh

echo 'performing gradle build'
./gradlew build
