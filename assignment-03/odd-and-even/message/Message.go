package message

// Message types
type MessageType string

const (
	NumberType    MessageType = "number"
	OddOrEvenType MessageType = "choice"
	TerminateType MessageType = "terminate"
	EndMatchType  MessageType = "end_match"
)

type Message struct {
	MType   MessageType
	Payload int
}
