package main

import (
	"flag"
	"fmt"
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
