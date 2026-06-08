package entity

import (
	"context"
	"log"
	"odds-and-even/message"
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

func CreatePlayer(id int) Player {
	ctx, cancel := context.WithCancel(context.Background())
	return &PlayerImpl{
		ch:     make(chan message.Message),
		id:     id,
		ctx:    ctx,
		cancel: cancel,
	}
}

func (p *PlayerImpl) run() {
	for {
		select {
		case <-p.ctx.Done():
			log.Printf("Player terminating")
			return
		case msg := <-p.ch:
			handleMessage(p, msg)
		}
	}
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
