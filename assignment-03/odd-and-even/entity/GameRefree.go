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
	evenId       int
	oddId        int
	num1         int
	num2         int
	id           int
}

func CreateGameRefree(id int, players []Player, chiefCh chan message.Message) Refree {
	return &GameRefree{
		chiefChannel: chiefCh,
		players:      players,
		id:           id,
	}
}

// Chooses a random player that chooses odd or even, then asks both players for a number
// and claims a winners. Trasmists the winner to the Chief.
func (g *GameRefree) run() {
	choise := rand.IntN(1)
	g.players[choise].getChannel() <- message.Message{MType: message.OddOrEvenType, Payload: struct{}{}}
	other := 1 + choise%2
	g.players[1+choise%2].getChannel() <- message.Message{MType: message.NumberRequestType, Payload: struct{}{}}

	gotNum1 := false
	gotNum2 := false
	for gotNum1 == false || gotNum2 == false {
		select {
		case msg := <-g.players[choise].getChannel():
			if msg.Payload == 0 {
				g.evenId = g.players[choise].getId()
				g.oddId = g.players[1+choise%2].getId()
			}
			g.num1 = msg.Payload.(message.OddOrEvenReply).Number
			gotNum1 = true
		case msg := <-g.players[other].getChannel():
			g.num2 = msg.Payload.(message.NumberReply).Number
			gotNum2 = true
		}
	}

	sum := g.num1 + g.num2

	if sum%2 == 0 {
		for _, player := range g.players {
			if player.getId() == g.evenId {
				g.chiefChannel <- message.Message{MType: message.EndMatchType, Payload: message.EndMatchReply{WinnerId: g.evenId, LoserId: g.oddId, RefreeId: g.id}}
			}
		}
	} else {
		for _, player := range g.players {
			if player.getId() == g.oddId {
				g.chiefChannel <- message.Message{MType: message.EndMatchType, Payload: message.EndMatchReply{WinnerId: g.oddId, LoserId: g.evenId, RefreeId: g.id}}
			}
		}
	}

}
