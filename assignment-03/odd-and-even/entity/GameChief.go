package entity

import (
	"context"
	"log"
	"math"
	"odds-and-even/message"
)

type Chief interface {
	Run() chan interface{}
	send(message.Message)
}

type GameChief struct {
	rounds      int64
	players     map[int]Player
	ch          chan message.Message
	ctx         context.Context
	currentRefs map[int]Refree
}

type ChiefState int

const (
	StateMatchmaking ChiefState = iota
	StateCreateReferee
	StateWaitReferee
	StateEndRound
	StateDone
)

func CreateGameChief(rounds int64, ctx context.Context) Chief {
	var g GameChief
	g.rounds = rounds
	g.ctx = ctx
	var players map[int]Player = make(map[int]Player)
	playerNum := int(math.Floor(math.Pow(2, float64(rounds))))
	for i := range playerNum {
		players[i] = CreatePlayer(i, ctx)
	}
	g.players = players
	return &g
}

// Starts the tournament and returns a channel, the channel closes when the tournament ends.
func (g *GameChief) Run() chan interface{} {
	log.Printf("[CHIEF]: starting tournament with %d rounds and %d players", g.rounds, len(g.players))
	ch := make(chan interface{})
	for _, player := range g.players {
		player.run()
	}

	go func() {
		currentState := StateMatchmaking
		for currentState != StateDone {
			select {
			case <-g.ctx.Done():
				log.Printf("[CHIEF]: signal received, shutting down...")
				return
			default:
				switch currentState {
				case StateMatchmaking:
					g.currentRefs = createCouples(g)
					for _, ref := range g.currentRefs {
						ref.run()
					}
					currentState = StateWaitReferee
				case StateWaitReferee:
					select {
					case msg := <-g.ch:
						if msg.MType == message.EndMatchType {
							panic("unmanaged state \"StateWaitRefree\" ")
							// remove refree, remove loser player
						}
					}
					if len(g.currentRefs) == 0 {
						currentState = StateEndRound
					}
				case StateEndRound:
					g.rounds--
					if g.rounds == 0 {
						currentState = StateDone
					} else {
						currentState = StateMatchmaking
					}
				}
			}
		}
		ch <- struct{}{}
	}()
	return ch
}

func (g *GameChief) send(m message.Message) {
	g.ch <- m
}

func createCouples(g *GameChief) map[int]Refree {
	var matches map[int]Refree = make(map[int]Refree)
	for i := range len(g.players) - 2 {
		id := i + int(g.rounds)*100 // if we are in 2 round, the 3rd refree will have id 203
		matches[id] = CreateGameRefree(id, []Player{g.players[i], g.players[i+1]}, g.ch)
	}
	return matches
}
