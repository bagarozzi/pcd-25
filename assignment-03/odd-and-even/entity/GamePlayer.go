package entity

// A Player is a member of the tournament.
// You can send messages and receive messages from the player.
type Player interface {
	//sendMatch()
	//receiveResults()
	//terminate()
	run()
}

type PlayerImpl struct {
}

func CreatePlayer(id int) Player {
	return nil
}

func (p *PlayerImpl) run() {
	for {

	}
}
