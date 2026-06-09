package message

// Message types
type MessageType string

const (
	NumberType    MessageType = "number"
	OddOrEvenType MessageType = "choice"
	TerminateType MessageType = "terminate"
)

type Message struct {
	MType   MessageType
	Payload int
}
