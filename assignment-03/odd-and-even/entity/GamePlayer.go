package entity

// A Player is a member of the tournament.
// You can send messages and receive messages from the player.
type Player interface {
	// Command the player to start a match againt an Opponent.
	sendMatch(op Opponent)
	receiveResults()
}

// An Opponent is a player to which a number is sent and from which a number is received.
type Opponent interface {
	// Send your number to the opponent.
	sendNumber(number int)
	// Receive a number from your opponent.
	receiveNumber()
}
