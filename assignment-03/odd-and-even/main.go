package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"odds-and-even/entity"
	"os"
	"os/signal"
	"strconv"
	"syscall"
	"time"
)

var (
	roundNum int64
	helpFlag bool
	logFlag  bool
)

const title string = "odds and even: goroutine odd-and-even tournament"
const cooldown time.Duration = time.Millisecond * 2000

func main() {
	signalChan := make(chan os.Signal, 1)
	signal.Notify(signalChan, syscall.SIGINT, syscall.SIGTERM)
	gracefulShutdownContext, gracefulShutdown := context.WithCancel(context.Background())
	if !parseArguments() {
		return
	}
	initLogging()
	log.Printf("[MAIN]: starting tournament with %d rounds...", roundNum)
	chief := entity.CreateGameChief(roundNum, gracefulShutdownContext)
	ch := chief.Run()
	for {
		select {
		case <-signalChan:
			log.Printf("[MAIN]: signal received, shutting down...")
			gracefulShutdown()
			time.Sleep(cooldown)
			log.Printf("[MAIN]: all shut down, goodbye")
			return
		case <-ch:
			log.Printf("[MAIN]: tournament ended, shutting down...")
			return
		}
	}

}

func initLogging() {
	if logFlag {
		timestamp := time.Now().Format("020106_150405")
		filename := fmt.Sprintf("match_%s", timestamp)
		file, err := os.OpenFile(filename, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0666)
		if err != nil {
			log.Fatal("Failed to open log file: ", err)
		}
		log.SetOutput(file)
	} else {
		log.SetOutput(os.Stdout)
	}
	log.SetFlags(log.Ldate | log.Ltime)
}

func parseArguments() bool {
	fmt.Println(title)
	flag.BoolVar(&helpFlag, "help", false, "display the help message")
	flag.BoolVar(&helpFlag, "h", false, "display the help message")
	flag.BoolVar(&logFlag, "l", false, "log to separate file")
	flag.Parse()
	if helpFlag {
		printUsage()
		return false
	} else if len(flag.Args()) == 0 {
		fmt.Println(fmt.Errorf("Error: number of rounds not specified"))
		printUsage()
		return false
	} else if l := len(flag.Args()); l > 1 {
		fmt.Println(fmt.Errorf("Error: can only specify one argument, found %d", l))
	}
	var err error
	if roundNum, err = strconv.ParseInt(flag.Args()[0], 10, 64); err != nil {
		fmt.Println(fmt.Errorf("Error: number passed must be integer, found %s", flag.Args()[0]))
		return false
	}
	return true
}

func printUsage() {
	fmt.Println("\nUsage:\todds-and-even [options] <n>")
	fmt.Println("  <> n : number of rounds")
	fmt.Println("Options:")
	fmt.Println("\t<> --help :\tdisplay the help message")
	fmt.Println("\t<> --l    :\tlog to separate file")
	fmt.Println()
}
