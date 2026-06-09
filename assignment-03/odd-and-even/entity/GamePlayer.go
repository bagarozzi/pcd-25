package entity

import (
	"context"
	"log"
	"odds-and-even/message"
	"time"
)

// A Player is a member of the tournament.
// You can send messages and receive messages from the player.
type Player interface {
	send(message.Message)
	getChannel() chan message.Message
	getId() int
	run()
	terminate()
}

type PlayerImpl struct {
	id     int
	ch     chan message.Message
	ctx    context.Context
	cancel context.CancelFunc
}

func CreatePlayer(id int, ctx context.Context) Player {
	return &PlayerImpl{
		ch:  make(chan message.Message),
		id:  id,
		ctx: ctx,
	}
}

func (p *PlayerImpl) run() {
	log.Printf("[PLAYER-%d]: spawned", p.getId())
	go func() {
		for {
			select {
			case <-p.ctx.Done():
				log.Printf("[PLAYER-%d]: terminating", p.getId())
				return
			// case msg, ok := <-p.ch
			// player logic here
			default:
				time.Sleep(50 * time.Millisecond)
			}
		}
	}()
}

func (p *PlayerImpl) send(m message.Message) {
	p.ch <- m
}

func (p *PlayerImpl) getChannel() chan message.Message {
	return p.ch
}

func (p *PlayerImpl) getId() int {
	return p.id
}

func (p *PlayerImpl) terminate() {
	p.cancel()
}

func handleMessage(p *PlayerImpl, msg message.Message) {
	panic("unimplemented")
}
