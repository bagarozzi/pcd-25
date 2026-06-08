package entity

import (
	"math/rand/v2"
	"odds-and-even/message"
)

type Refree interface {
	run()
}

type GameRefree struct {
	chiefChannel chan message.Message
	players      []Player
	results      []int
	evenId       int
	oddId        int
}

func CreateGameRefree() Refree {
	return &GameRefree{}
}

// Chooses a random player that chooses odd or even, then asks both players for a number
// and claims a winners. Trasmists the winner to the Chief.
func (g *GameRefree) run() {
	choise := rand.IntN(2)
	g.players[choise].getChannel() <- message.Message{MType: message.OddOrEvenType, Payload: 2}
	m := <-g.players[choise].getChannel()
	if m.Payload == 0 {
		g.evenId = g.players[choise].getId()
		g.oddId = g.players[1+choise%2].getId()
	}
	g.evenId = g.players[choise].getId()
	g.players[1-choise].getChannel() <- message.Message{MType: message.OddOrEvenType, Payload: 2}

	for i, player := range g.players {
		channel := player.getChannel()
		mes := <-channel
		g.results[i] = mes.Payload
	}

	sum := g.results[0] + g.results[1]

	if sum%2 == 0 {
		for _, player := range g.players {
			if player.getId() == g.evenId {
				g.chiefChannel <- message.Message{MType: "winner", Payload: player.getId()}
			}
		}
	} else {
		g.chiefChannel <- message.Message{MType: "winner", Payload: g.players[1].getId()}
	}

}
