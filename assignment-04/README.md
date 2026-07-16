# Assignment 04 - Alarm System Extended, Tic-Tac-Toe & Distributed CSs

The assignment 4 implements and recalls the following theory topics:
- For **Alarm System Extended** the same principle of The [Assignment 03](../assignment-03/README.md) are applied.
- **Actors as Paragidm for distributed systems**: varying from the limitations of the traditional paradigms (_procedura, OOP, RPC_)
- **Distributed System's Constraints**:
    - **Absensce of a shared clock**
    - **Abscence of shared memory/state**
    - **No failure detection**: there's no upper bound on message delay
- Closely linked to the **CAP Theorem**
- **Message-Oriented Middlewares**: any infrastructure that can provide messaging capabilities
between processes in a DS &rarr; Both **RMI** and **RabbitMQ** are MOMs in the Assignments
and can support various types of communications.
    - It essentialy abstracts the communication layer from a program.
    - **Transient and Persistent** models: depending if the processes are executed at the same time or not &rarr; RMI is transient, RabbitMQ is persistent.
- **RPC vs. MOMs**: RPC is synchronous, blocking and requires propagation of interfaces; MOM is persistent and decouples the presence of the processes from their messaging &rarr; far more robust, highly available and scalable.
- **Messaging models**: point-to-point (for RMI), Pub-Sub (for RabbitMQ) 
- **Pull/Push delivery models** 
- **Quality-of-Service**: about _message delivery guarantees_, at least once, at most once, exactly once. 
- **Event brokers vs. message brokers**: message brokers deal messages that are consumed and deleted shortly after. Events br. are about providing a log of ***facts***.

## Other theory
Other theory that is not mentioned in the assignment is:
- **Tracking state in a Distributed System**: no notion of global state, shared memory or clocks so designing distributed algorithm is difficult:
    - Happened-before
    - Causal
    - Clock
- **Logical clocks**
- **Vector clocks**
- **Chandy-Lamport** Snapshotting Algorithm
- **Agrawala** Critical Section Algorithm
- **Consensus algorithms** based on one uniform decision function: FLP Result, constraints and Byzantine faults