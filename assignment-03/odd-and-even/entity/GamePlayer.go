package entity

import (
	"context"
	"log"
	"math/rand/v2"
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
			case msg := <-p.ch:
				switch msg.MType {
				case message.OddOrEvenRequestType:
					ooe := rand.IntN(2)
					num := rand.IntN(5)
					p.send(message.Message{MType: message.OddOrEvenReplyType, Payload: message.OddOrEvenReply{Choice: ooe,
						Number: num,
						Id:     p.getId(),
					}})
					continue
				case message.NumberRequestType:
					num := rand.IntN(5)
					p.send(message.Message{MType: message.NumberReplyType, Payload: message.NumberReply{
						Number: num,
						Id:     p.getId(),
					}})
					continue
				case message.TerminateType:
					log.Printf("[PLAYER-%d]: lost, terminating", p.getId())
					return
				}

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
