# Assignment 01 - Poool

The assignment 1 implements and recalls the following theory topics:
- **Interaction patterns**: coordination (synchronization), competition (mutex, critical section),
interference (race condition, deadlock, starvation)
- **Semaphores**: event counting and coordination
- **Monitors**: coordination and OOP
- **Concurrent programs design**: analysis, division in task
    - Partition between Active and Passive components
    - Conceptual classes of parallelism (result, **agenda** and specialist)
    - choice of a concurrent architecture (Filter-Pipeline, Announcer-Listener, **Master-Worker**, Blackboard)
- **Task-oriented frameworks**: higher level, abstracting from the execution environment, async but allowing blocking mechanisms
- **Visual formalisms**: how Petri Nets model concurrent systems and their abstractions
- **Verification** of concurrent programs: automatically checking safety and liveness properties
through state-space search (reducing size of state space) *in all possible scenarios/traces*. LTL for expressing properties as logical
formulas. (remember ***testing vs. verification vs. validation***)
