package main

import (
	"golang.org/x/mobile/app"
    "fmt"
	_ "golang.org/x/mobile/bind/java"
	_ "github.com/ChrisSmith/go_libcats"
	"github.com/ChrisSmith/libcats"
)

func main() {
	libcats.SetThreadLogger()
	fmt.Printf("main tid %d\n", libcats.GetThreadId())
	app.Run(app.Callbacks{})
}
