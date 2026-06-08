package entity

import (
	"math"
	"odds-and-even/message"
)

type Chief interface {
	run()
	send(message.Message)
}

type GameChief struct {
	rounds  int
	players []Player
	ch      chan message.Message
}

func CreateGameChief(rounds int) Chief {
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

func (g *GameChief) run() {
	for {
		if g.rounds == 0 {
			//termina e segnala vincitore
		}
		// matchmaking
		// crea refree
		// aspetta refree
		// termina chi ha perso
		g.rounds--
	}
}

func (g *GameChief) send(m message.Message) {
	g.ch <- m
}
