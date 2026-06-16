package it.unibo.alarm.actors


import org.apache.pekko.actor.typed.*
import org.apache.pekko.actor.typed.scaladsl.*

import scala.concurrent.duration.FiniteDuration

/**
 * A ZoneActor manages a zone of a house, groups together sensors from a zone and reports triggers.
 * It can be enabled and disabled.
 */
object ZoneActor:

  import it.unibo.alarm.actors.AlarmActor.Command.*

  enum Command:
    case Arm
    case Disarm
    case Alert
    case EntryDelayOver
    case ArmDelayOver

  export Command.*

  /**
   * Initialize the Zone actor and the sensors passed.
   * @param sensors the sensors' type managed by the actor
   * @param alarmActor the AlarmActor that this actor has to report to
   * @return
   */
  def apply(sensors: Set[SensorActor.Type], entryTimeout: FiniteDuration, exitTimeout: FiniteDuration, alarmActor: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.withTimers: timers =>
      Behaviors.setup: context  =>
        context.log.info("spawned " + context.self.path.name)
        sensors.zipWithIndex.foreach((s, i) => context.spawn(SensorActor(context.self, s), s"sensor-$i"))
        context.log.info("Zone " + context.self.path.name + " disarmed.")
        new ZoneActor(alarmActor, timers, entryTimeout, exitTimeout).disarmed()

  class ZoneActor(
    val alarmActor: ActorRef[AlarmActor.Command],
    val timers: TimerScheduler[Command],
    val entryTimeout: FiniteDuration,
    val exitTimeout: FiniteDuration,
                 ):

    def disarmed(): Behavior[Command] =
      Behaviors.receive: (context, message) =>
        message match
          case Arm => context.log.info("Zone " + context.self.path.name + " started arming.") ; exitDelay()
          case _ => Behaviors.same

    private def exitDelay(): Behavior[Command] =
      timers.startSingleTimer(ArmDelayOver, exitTimeout);
      Behaviors.receive: (context, message) =>
        message match
          case Disarm =>
            context.log.info("Zone " + context.self.path.name + " disarmed.")
            disarmed()
          case ArmDelayOver => context.log.info("Zone " + context.self.path.name + " armed.") ; armed()
          case _ => Behaviors.same

    private def armed(): Behavior[Command] =
      Behaviors.receive: (context, message) =>
        message match
          case Alert => alarmActor ! AlarmActor.Command.Alert(context.self.path.toStringWithoutAddress) ; context.log.info("Zone " + context.self.path.name + " intrusion detected, started timer.") ; entryDelay()
          case Disarm => context.log.info("Zone " + context.self.path.name + " disarmed.") ; disarmed()
          case _ => Behaviors.same

    private def alarm() : Behavior[Command] =
      Behaviors.receive: (context, message) =>
        message match
          case Disarm =>
            context.log.info("Zone " + context.self.path.name + " disarmed.")
            disarmed()
          case _ => Behaviors.same

    private def entryDelay(): Behavior[Command] =
      timers.startSingleTimer(EntryDelayOver, entryTimeout);
      Behaviors.receive: (context, message) =>
        message match
        case Disarm  => context.log.info("Zone " + context.self.path.name + " disarmed.") ; disarmed()
        case EntryDelayOver =>
          context.log.info("Entry delay over in zone " + context.self.path.name)
          alarmActor ! Trigger
          alarm()
        case _ => Behaviors.same