package it.unibo.alarm.actors

import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior}

object ZoneActor:

  import it.unibo.alarm.actors.AlarmActor.Command.*

  enum Command:
    case Disable
    case Enable
    case Alert

  export Command.*

  def apply(sensors: Set[SensorActor.Type], alarm: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.setup: context =>
      sensors.zipWithIndex.foreach((s, i) => context.spawn(SensorActor(context.self, s), s"sensor-$i"))
      active()

  private def active(): Behavior[Command] =
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