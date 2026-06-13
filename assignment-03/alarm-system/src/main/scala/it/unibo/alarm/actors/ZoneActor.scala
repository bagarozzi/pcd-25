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
    case Disable
    case Enable
    case Alert

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
      active(alarm)

  private def active(alarm: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case Disable => Behaviors.same
        case Enable => Behaviors.same
        case Alert => Behaviors.same

  // private def exitDelay()

  // private def alarm()

  // private def entryDelay()

  //private def armed()

  //private def disarmed()