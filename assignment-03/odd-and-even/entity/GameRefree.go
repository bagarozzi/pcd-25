package entity

type Refree interface {
	run()
}

type GameRefree struct {
	//chiefChannel chan message.Message
	players []Player
	results []int
}

func CreateGameRefree() Refree {
	return &GameRefree{}
}

// Chooses a random player that chooses odd or even, then asks both players for a number
// and claims a winners. Trasmists the winner to the Chief.
func (g *GameRefree) run() {
	panic("unimplemented")
}
