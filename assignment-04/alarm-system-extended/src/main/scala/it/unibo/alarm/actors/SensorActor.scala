package it.unibo.alarm.actors

import it.unibo.alarm.actors.ZoneActor.Command.Alert
import it.unibo.alarm.cluster.CborSerializable
import org.apache.pekko.actor.typed.*
import org.apache.pekko.actor.typed.receptionist.{Receptionist, ServiceKey}
import org.apache.pekko.actor.typed.scaladsl.*

import scala.reflect.ClassTag

object SensorActor:

  sealed trait Command extends CborSerializable

  enum Type:
    case Motion
    case Door

  object Command:
    case object Trigger extends Command

  export Command.*
  export Type.*

  def createSensorKey[T <: SensorActor.Command : ClassTag](sensorId: String): ServiceKey[T] = {
    ServiceKey[T](sensorId)
  }

  def apply(father: ActorRef[ZoneActor.Command], sensorType: Type): Behavior[Command] =
    Behaviors.setup: context =>
      context.system.receptionist ! Receptionist.Register(createSensorKey("sensor"), context.self)
      context.log.info("Spawned " + context.self.path.name + " of type " + sensorType.toString + " in zone " + context.self.path.parent.name + ".")
      active(father)

  private def active(father: ActorRef[ZoneActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case Trigger =>
          context.log.info( context.self.path.name + "-" + context.self.path.parent.name + " triggered!" )
          father ! Alert
          Behaviors.same