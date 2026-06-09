package entity

import (
	"context"
	"log"
	"math"
	"odds-and-even/message"
	"time"
)

type Chief interface {
	Run() chan int64
	send(message.Message)
}

type GameChief struct {
	rounds  int64
	players []Player
	ch      chan message.Message
}

func CreateGameChief(rounds int64, ctx context.Context) Chief {
	var g GameChief
	g.rounds = rounds
	var players []Player
	playerNum := int(math.Floor(math.Pow(2, float64(rounds))))
	for i := range playerNum {
		players = append(players, CreatePlayer(i))
	}
	g.players = players
	return &g
}

func (g *GameChief) Run() chan int64 {
	log.Printf("[CHIEF]: starting tournament with %d rounds and %d players", g.rounds, len(g.players))
	ch := make(chan int64)
	go func() {
		for {
			if g.rounds == 0 {
				ch <- 2
				return
			}
			// matchmaking
			// crea refree
			// aspetta refree
			time.Sleep(2000 * time.Millisecond)
			// termina chi ha perso
			g.rounds--
		}
	}()
	return ch
}

func (g *GameChief) send(m message.Message) {
	g.ch <- m
}
