# Assignment 03 - Odd and Even & Alarm System

The assignment 3 implements and recalls the following theory topics:
- **Message passing models**: base operations, asynchronous and ***synchronous*** versions
- **Communication schemes** for channels: one-to-one (Refree-Player), many-to-many, many-to-one (Refree-Chief)
- **Comparison with monitors/semaphores with Resource Allocation**: by using channels we can implement all the structures
seen previously. Producer/Consumer, Buffers, Resource Allocation
- **Guarded communication and loop**: we can selectively read from a channel depending on a condition
- **Synchrnous semantics**: when a communication is synchronous both reader and writer have to wait
for each other to be ready
- **Rendez-vous**: the calling process also waits for the reply (*call* primitive)
- **Unification of concurrency and OOP with Actors**: _state encapsulation_ with a _behavior_ on 
_how to react to a message_
- **Uncoupling from physical concurrency**: by encapsulating a control flow inside an Actor
- **Actors primitives**: send, create, become to change behavior
- **Purely reactive behavior**: an Actor is not always active, it's active only when a message arrives and it reacts to it
- **Location transaparency**: actors don't have to know where an actor is to communicate 
- **Eventual message delivery**: no assumptions can be made on the delay and the ***order*** of delivery
- **Future**s: an asynchrnous result by an actor. 
- **Stashing**: given that there's no order on messages, some have to be either ignored or stashed
for later computation.
- **Messaging patterns**: channels (***point-point***, ***publish-subscribe***), message routing (***broker*** like the shard coordinator)
- **Proactivity** using **message self-sending**: to enable tasks and use a plan of actions, like
an iteration, self-sending can be used (or multiple actors each with a tasks that are triggered) &rarr; we do it with timers. 