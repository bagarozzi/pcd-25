package it.unibo.alarm.actors

import it.unibo.alarm.AlarmProtocol
import it.unibo.alarm.actors.KeypadActor.Command
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import it.unibo.alarm.cluster.CborSerializable
import org.apache.pekko.actor.typed.pubsub.Topic
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityRef, EntityTypeKey}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

/**
 * The actor coordinating the whole alarm: locking, unlocking and catching signals.
 */
object AlarmActor:

  sealed trait Command extends CborSerializable

  val TypeKey: EntityTypeKey[Command] = EntityTypeKey[Command]("AlarmHub")

  object Command:
    case class Arm(zone: String) extends Command
    case object ArmAll extends Command
    case class Disarm(zone: String) extends Command
    case object DisarmAll extends Command
    case object Trigger extends Command
    case class Alert(from: String) extends Command
    case object Silence extends Command

  export Command.*

  def apply(hubId: String, zones: Set[String]): Behavior[Command] =
    Behaviors.setup: context =>
      context.log.info(s"Spawned $hubId Alarm Hub for house")

      val keypadTopic = context.spawn(Topic[KeypadActor.Command](AlarmProtocol.KeypadTopicName), "KeypadTopicProxy")

      val sharding = ClusterSharding(context.system)
      val zoneActors: Map[String, EntityRef[ZoneActor.Command]] =
        zones.map(z => z -> sharding.entityRefFor(ZoneActor.TypeKey, z)).toMap

      zoneActors.values.foreach(zoneRef => zoneRef ! ZoneActor.Command.Disarm)

      activeState(zoneActors, Map.empty, keypadTopic)

  private def activeState(
       disarmedZones: Map[String, EntityRef[ZoneActor.Command]],
       armedZones: Map[String, EntityRef[ZoneActor.Command]],
       keypad: ActorRef[Topic.Command[KeypadActor.Command]]
   ): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case Arm(zone) => arm(Set(zone), disarmedZones, armedZones, keypad)
        case ArmAll => arm(disarmedZones.keySet, disarmedZones, armedZones, keypad)
        case Disarm(zone) => disarm(Set(zone), disarmedZones, armedZones, keypad)
        case DisarmAll => disarm(armedZones.keySet, disarmedZones, armedZones, keypad)
        case Alert(from) =>
          keypad ! Topic.Publish(KeypadActor.EntryAlert(from))
          activeState(disarmedZones, armedZones, keypad)
        case Trigger =>
          keypad ! Topic.publish(KeypadActor.Command.AlarmAlert)
          alarmState(disarmedZones, armedZones, keypad)
        case _ => activeState(disarmedZones, armedZones, keypad)

  private def alarmState(
      disarmedZones: Map[String, EntityRef[ZoneActor.Command]],
      armedZones: Map[String, EntityRef[ZoneActor.Command]],
      keypad: ActorRef[Topic.Command[KeypadActor.Command]]
  ): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case Silence => disarm(armedZones.keySet, disarmedZones, armedZones, keypad)
        case _ => Behaviors.same

  private def arm(
      zonesToArm: Set[String],
      disarmedZones: Map[String, EntityRef[ZoneActor.Command]],
      armedZones: Map[String, EntityRef[ZoneActor.Command]],
      keypad: ActorRef[Topic.Command[KeypadActor.Command]]
  ): Behavior[Command] =
    val zones = consumeZones(zonesToArm, disarmedZones, armedZones, keypad)(ref => ref ! ZoneActor.Command.Arm)
    keypad ! Topic.publish(KeypadActor.Command.ExitAlert(zones.keySet))
    activeState(disarmedZones -- zones.keys, armedZones ++ zones, keypad)

  private def disarm(
      zonesToDisarm: Set[String],
      disarmedZones: Map[String, EntityRef[ZoneActor.Command]],
      armedZones: Map[String, EntityRef[ZoneActor.Command]],
      keypad: ActorRef[Topic.Command[KeypadActor.Command]]
  ): Behavior[Command] =
    val zones = consumeZones(zonesToDisarm, armedZones, disarmedZones, keypad)(ref => ref ! ZoneActor.Command.Disarm)
    activeState(disarmedZones ++ zones, armedZones -- zones.keys, keypad)

  private def consumeZones(
        target: Set[String],
        fromConsume: Map[String, EntityRef[ZoneActor.Command]],
        toConsume: Map[String, EntityRef[ZoneActor.Command]],
        keypad: ActorRef[Topic.Command[KeypadActor.Command]]
    )(consumer: EntityRef[ZoneActor.Command] => Unit): Map[String, EntityRef[ZoneActor.Command]] =
    val validZonesMap = target
        .filter(fromConsume.contains)
        .filterNot(toConsume.contains)
        .map(z => z -> fromConsume(z))
        .toMap

    validZonesMap.values.foreach(consumer)

    validZonesMap
