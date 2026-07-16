# Assignment 02 - FSStat

The assignment 2 implements and recalls the following theory topics:
- **Event-loop based control flow architecture**:
	- the event-loop
	- never-blocking rule
	- use of asynchrnous tasks exhttps://meet.google.com/ucy-fqih-cfoecuted by other threads
- **Continuation Passing Style** (CPS): callbacks, how they relate with the Event queue (*who executes the callbacks?*), the callback of hell and desperation 
- **Advantages of async programming**: single thread for multiple tasks, no races, no deadlocks (always same thread)
- **Promises**: flattening the pyramid of hell, elegant way of representing a future, async result.
- **Async/Await language extension**: writing asynchronous programs in a sequential way. Doesn't block
the control flow (*co-routine based*) and can pass arguments.
- **Reactive programming**: declarative development of event-drive problems. Solving the problems
of the *inversion of control*, abstracting from time-varying events, doing what classic async can't do
- **Type of abstractions**: **event streams** (discrete, intermittent values), signals (dense, continuous)
- Propagation of change: useful in a declarative way
- **Propagation models**: push (source pushes new data), **pull** (consumer requests it)
- Lifting: a variable is part of a reactive chain so it's updated
- **Hot vs. Cold**: every subscriber either gets every element of the stream or start receiving from
when it has subscribed 
- **Lightweigth threads - _Fibers_**: preemptive, cooperative, lightweight thread useful for multitasking. Like coroutines but at different level of abstraction (*coroutines are language*)
- **Memory footprint**: solving practical limits on how many threads one can spawn.
- **Thread-per-task applications**: useful when serving many requests, higher throughput (***project is based on this, in a fork-join architecture***)
- **Mounting on carrier threads and _pinning_**:  vthreads are easily assigned to a platform thread
and unmount when a blocking operation happens, although they can be pinned by a synchronized call.
- **Little's law**: throughput only depends from execution speed and concurrent operations.
- 