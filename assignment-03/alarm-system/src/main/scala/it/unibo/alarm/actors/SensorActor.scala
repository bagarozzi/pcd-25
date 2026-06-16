package it.unibo.alarm.actors

import it.unibo.alarm.actors.ZoneActor.Command.Alert
import org.apache.pekko.actor.typed.*
import org.apache.pekko.actor.typed.scaladsl.*

object SensorActor:

  enum Type:
    case Motion
    case Door

  enum Command:
    case Trigger

  export Command.*
  export Type.*

  def apply(father: ActorRef[ZoneActor.Command], sensorType: Type): Behavior[Command] =
    Behaviors.setup: context =>
      context.log.info("Spawned " + context.self.path.toStringWithoutAddress + " of type " + sensorType.toString)
      active(father)

  private def active(father: ActorRef[ZoneActor.Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case Trigger =>
        father ! Alert
        Behaviors.same