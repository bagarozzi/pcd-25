package main

import (
	"flag"
	"fmt"
	"log"
	"os"
	"time"
)

var (
	roundNum int
	helpFlag bool
)

const title string = "odds and even: goroutine odd-and-even tournament"

func main() {
	if !parseArguments() {
		return
	}
	initLogging()
	log.Printf("Match started with %d players", roundNum)
}

func initLogging() {
	timestamp := time.Now().Format("020106_1504")
	filename := fmt.Sprintf("match_%s", timestamp)
	file, err := os.OpenFile(filename, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0666)
	if err != nil {
		log.Fatal("Failed to open log file: ", err)
	}
	log.SetOutput(file)
	log.SetFlags(log.Ldate | log.Ltime)
}

func parseArguments() bool {
	fmt.Println(title)
	flag.BoolVar(&helpFlag, "help", false, "display the help message")
	flag.BoolVar(&helpFlag, "h", false, "display the help message")
	flag.Parse()
	if helpFlag {
		printUsage()
		return false
	} else if len(flag.Args()) == 0 {
		fmt.Println(fmt.Errorf("Error: number of rounds not specified"))
		printUsage()
		return false
	}
	return true
}

func printUsage() {
	fmt.Println("\nUsage:\todds-and-even <n> [--help]")
	fmt.Println("  <> n : number of rounds")
	fmt.Println("  <> --help : display the help message")
	fmt.Println()
}
