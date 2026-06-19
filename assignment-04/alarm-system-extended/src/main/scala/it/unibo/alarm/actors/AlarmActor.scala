package it.unibo.alarm.actors

import it.unibo.alarm.actors.KeypadActor.Command.ExitAlert
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import it.unibo.alarm.actors.SensorActor
import it.unibo.alarm.actors.SensorActor.Type
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef, EntityTypeKey}

import scala.concurrent.duration.FiniteDuration

/**
 * The actor coordinating the whole alarm: locking, unlocking and catching signals.
 */
object AlarmActor:

  val TypeKey: EntityTypeKey[Command] = EntityTypeKey[Command]("AlarmHub")

  enum Command:
    case Arm(zone: String)
    case ArmAll()
    case Disarm(zone: String)
    case DisarmAll()
    case Trigger
    case Alert(from: String)
    case Silence

  export Command.*

  def apply(keypad: ActorRef[KeypadActor.Command], zones: Set[String], entryTimeout: FiniteDuration, exitTimeout: FiniteDuration): Behavior[Command] =
    Behaviors.setup: context =>
      context.log.info("Spawned AlarmActor for house")
      val sharding = ClusterSharding(context.system)
      val zoneActors: Map[String, EntityRef[ZoneActor.Command]] =
        zones.map(z => (z -> sharding.entityRefFor(ZoneActor.TypeKey, z))).toMap
      new AlarmActor(zoneActors, Map.empty, keypad).activeState()

  class AlarmActor(
      var disarmedZones: Map[String, EntityRef[ZoneActor.Command]],
      var armedZones: Map[String, EntityRef[ZoneActor.Command]],
      val keypad: ActorRef[KeypadActor.Command]
                  ):

    def activeState(): Behavior[Command] =
      Behaviors.receive: (context, message) =>
        message match
          case Arm(zone) => arm(Set(zone))
          case ArmAll() => arm(disarmedZones.keySet)
          case Disarm(zone) => disarm(Set(zone))
          case DisarmAll() => disarm(armedZones.keySet)
          case Alert(from) => keypad ! KeypadActor.Command.EntryAlert(from) ; activeState()
          case Trigger => keypad ! KeypadActor.Command.AlarmAlert ; alarmState()
          case _ => activeState()

    private def alarmState(): Behavior[Command] =
      Behaviors.receive: (context, message) =>
        message match
          case Silence => disarm(armedZones.keySet)
          case _ => Behaviors.same

    private def arm(zonesToArm: Set[String]): Behavior[Command] =
      zonesToArm.foreach(zoneToArm =>
        if !armedZones.contains(zoneToArm) && disarmedZones.contains(zoneToArm) then
          disarmedZones(zoneToArm) ! ZoneActor.Command.Arm
          armedZones = armedZones + (zoneToArm -> disarmedZones(zoneToArm))
          disarmedZones = disarmedZones.removed(zoneToArm)
      )
      keypad ! KeypadActor.Command.ExitAlert(zonesToArm)
      activeState()

    private def disarm(zonesToDisarm: Set[String]): Behavior[Command] =
      zonesToDisarm.foreach(zoneToDisarm =>
        if armedZones.contains(zoneToDisarm) && !disarmedZones.contains(zoneToDisarm) then
          armedZones(zoneToDisarm) ! ZoneActor.Command.Disarm
          disarmedZones = disarmedZones + (zoneToDisarm -> armedZones(zoneToDisarm))
          armedZones = armedZones.removed(zoneToDisarm)
      )
      activeState()
