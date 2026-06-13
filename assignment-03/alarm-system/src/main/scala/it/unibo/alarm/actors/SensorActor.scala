package it.unibo.alarm.actors

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

  def apply(father: ActorRef[Command], sensorType: Type): Behavior[Command] =
    Behaviors.setup: context =>
      active(father)

  private def active(father: ActorRef[Command]): Behavior[Command] =
    Behaviors.receiveMessage:
      case Trigger =>
        father ! Trigger
        Behaviors.same
      case null => Behaviors.same