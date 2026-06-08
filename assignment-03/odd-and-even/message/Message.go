package message

// Message types
type MessageType string

const (
	NumberType    MessageType = "number"
	OddOrEvenType MessageType = "choice"
)

type Message struct {
	MType   MessageType
	Payload int
}
