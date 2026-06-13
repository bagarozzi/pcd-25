package it.unibo.alarm.actors

import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior}

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
    case DisarmDelayOver
    case ArmDelayOver

  export Command.*

  /**
   * Initialize the Zone actor and the sensors passed.
   * @param sensors the sensors' type managed by the actor
   * @param alarmActor the AlarmActor that this actor has to report to
   * @return
   */
  def apply(sensors: Set[SensorActor.Type], entryTimeout: FiniteDuration, exitTimeout: FiniteDuration, alarmActor: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.setup: context  =>
      sensors.zipWithIndex.foreach((s, i) => context.spawn(SensorActor(context.self, s), s"sensor-$i"))
      disarmed(alarmActor)

  private def disarmed(alarmActor: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
        case Arm => exitDelay(alarmActor)

  private def exitDelay(alarmActor: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
        case Disarm => disarmed(alarmActor)
        case ArmDelayOver => armed(alarmActor)

  private def armed(alarmActor: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case Alert => entryDelay(alarmActor)
      case Disarm => disarmed(alarmActor)

  private def alarm(alarmActor: ActorRef[AlarmActor.Command]) : Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case Disarm => disarmed(alarmActor)

  private def entryDelay(alarmActor: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case Disarm | DisarmDelayOver => disarmed(alarmActor)