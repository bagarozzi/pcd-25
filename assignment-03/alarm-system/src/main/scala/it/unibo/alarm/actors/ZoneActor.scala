package it.unibo.alarm.actors

import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior}

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
   * @param alarm the AlarmActor that this actor has to report to
   * @return
   */
  def apply(sensors: Set[SensorActor.Type], alarm: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.setup: context =>
      sensors.zipWithIndex.foreach((s, i) => context.spawn(SensorActor(context.self, s), s"sensor-$i"))
      disarmed(alarm)

  private def disarmed(alarm: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
        case Arm => exitDelay(alarm)

  private def exitDelay(alarm: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
        case Disarm => disarmed(alarm)
        case ArmDelayOver => armed(alarm)

  private def armed(alarm: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case Alert => entryDelay(alarm)
      case Disarm => disarmed(alarm)

  private def alarm(alarm: ActorRef[AlarmActor.Command]) : Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case Disarm => disarmed(alarm)

  private def entryDelay(alarm: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case Disarm | DisarmDelayOver => disarmed(alarm)