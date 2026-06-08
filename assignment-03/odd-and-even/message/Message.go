package message

// Message types
type MessageType string

const (
	NumberType    string = "number"
	OddOrEvenType string = "choice"
)

type Message struct {
	mType   MessageType
	payload int
}
