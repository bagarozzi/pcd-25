package message

// Message types
type MessageType string

const (
	NumberRequestType    MessageType = "number_request"
	NumberReplyType      MessageType = "number"
	OddOrEvenRequestType MessageType = "choice"
	OddOrEvenReplyType   MessageType = "choice_reply"
	TerminateType        MessageType = "terminate"
	EndMatchType         MessageType = "end_match"
)

type Message struct {
	MType   MessageType
	Payload any
}

type NumberReply struct {
	Number int
	Id     int
}

type OddOrEvenReply struct {
	Choice int
	Number int
	Id     int
}

type EndMatchReply struct {
	WinnerId int
	LoserId  int
	RefreeId int
}
