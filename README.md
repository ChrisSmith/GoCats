GoCats
======

Example Android App using Go as the primary language.
Grabs images from /r/aww and displays a new one every couple seconds on your phone. 
Go is used for the networking, json parsing, caching and timer


## Quick Setup for Go/Android compilation  
Update Android Tools

Install NDK (https://developer.android.com/tools/sdk/ndk/index.html)

Setup Android toolchain 
```
./build/tools/make-standalone-toolchain.sh  --arch=arm  --platform=android-19 --system=darwin-x86_64 --toolchain=arm-linux-androideabi-4.8 --install-dir=$NDK_ROOT
```

Update Go to 1.4 (clone from master if you use Android 5.0+. https://github.com/golang/go)

```
go get golang.org/x/mobile/app
```

Cross compile gcc for android 
```
cd /usr/local/src // Go is typically installed here on OSX
CC=clang ./make.bash
CC_FOR_TARGET=$NDK_ROOT/bin/arm-linux-androideabi-gcc CGO_ENABLED=1 GOOS=android GOARCH=arm GOARM=7 ./make.bash

optionally override gcc in your profile 
```

See https://github.com/ChrisSmith/GoCats/blob/master/genbindings.sh on how to compile and generate the bindings between the languages
