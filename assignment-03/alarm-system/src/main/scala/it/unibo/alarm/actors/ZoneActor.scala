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
        context.log.info("spawned " + context.self.path.toStringWithoutAddress)
        sensors.zipWithIndex.foreach((s, i) => context.spawn(SensorActor(context.self, s), s"sensor-$i"))
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
          case Arm => exitDelay()
          case _ => Behaviors.same

    private def exitDelay(): Behavior[Command] =
      timers.startSingleTimer(ArmDelayOver, exitTimeout);
      Behaviors.receiveMessagePartial:
          case Disarm => disarmed()
          case ArmDelayOver => armed()

    private def armed(): Behavior[Command] =
      Behaviors.receive: (context, message) =>
        message match
          case Alert => alarmActor ! AlarmActor.Command.Alert(context.self.path.toStringWithoutAddress) ; entryDelay()
          case Disarm => disarmed()
          case _ => Behaviors.same

    private def alarm() : Behavior[Command] =
      alarmActor ! Trigger
      Behaviors.receiveMessagePartial:
        case Disarm => disarmed()

    private def entryDelay(): Behavior[Command] =
      timers.startSingleTimer(EntryDelayOver, entryTimeout);
      Behaviors.receiveMessagePartial:
        case Disarm  => disarmed()
        case EntryDelayOver => alarm()