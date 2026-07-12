package it.unibo.alarm.actors

import it.unibo.alarm.actors.ZoneActor.Command.Alert
import it.unibo.alarm.cluster.CborSerializable
import org.apache.pekko.actor.typed.*
import org.apache.pekko.actor.typed.receptionist.{Receptionist, ServiceKey}
import org.apache.pekko.actor.typed.scaladsl.*
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef, EntityTypeKey}

import scala.reflect.ClassTag

object SensorActor:

  sealed trait Command extends CborSerializable

  val TypeKey: EntityTypeKey[Command] = EntityTypeKey[Command]("SensorEntity")

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

  def apply(sensorId: String, zoneId: String, sensorType: Type): Behavior[Command] =
    Behaviors.setup: context =>
      context.system.receptionist ! Receptionist.Register(createSensorKey("sensor"), context.self)

      val sharding = ClusterSharding(context.system)
      val father: EntityRef[ZoneActor.Command] = sharding.entityRefFor(ZoneActor.TypeKey, zoneId)

      context.log.info("Spawned " + sensorId + " of type " + sensorType.toString + " in zone " + zoneId + ".")
      active(father, sensorId)

  private def active(father: EntityRef[ZoneActor.Command], sensorId: String): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case Trigger =>
          context.log.info( sensorId + " triggered!" )
          father ! Alert
          Behaviors.same