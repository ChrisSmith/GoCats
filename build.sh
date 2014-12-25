#!/usr/bin/env bash

set -e

if [ ! -f build.sh ]; then
	echo 'build.sh must be run from its root path'
	exit 1
fi

ANDROID_APP=$PWD/app
JAVA_DIR=$ANDROID_APP/src/main/java/go/

GO_CATS=$GOPATH/src/github.com/ChrisSmith/go_libcats
mkdir -p $ANDROID_APP/src/main/jniLibs/armeabi $ANDROID_APP/src/main/java/go/libcats $GO_CATS

echo 'generating go bindings'
gobind -lang=go github.com/ChrisSmith/libcats > $GO_CATS/go_libcats.go
echo 'generating java binding'
gobind -lang=java github.com/ChrisSmith/libcats > $JAVA_DIR/libcats/Libcats.java


echo 'copying java files'
cp $GOPATH/src/golang.org/x/mobile/bind/java/Seq.java $JAVA_DIR
cp $GOPATH/src/golang.org/x/mobile/app/Go.java $JAVA_DIR

echo 'building .so'
CGO_ENABLED=1 GOOS=android GOARCH=arm GOARM=7 go build -ldflags="-shared" .
mv -f GoCats $ANDROID_APP/src/main/jniLibs/armeabi/libgojni.so

echo 'calling gradle'
./gradlew build
