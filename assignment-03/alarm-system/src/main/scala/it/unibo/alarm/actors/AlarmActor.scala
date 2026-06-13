package it.unibo.alarm.actors

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import it.unibo.alarm.actors.SensorActor
import it.unibo.alarm.actors.SensorActor.Type
import org.apache.pekko.actor.typed.scaladsl.Behaviors

/**
 * The actor coordinating the whole alarm: locking, unlocking and catching signals.
 */
object AlarmActor:

  enum Command:
    case Arm(zone: String)
    case ArmAll
    case Disarm(zone: String)
    case DisarmAll

  export Command.*

  def apply(zones: Map[String, Set[SensorActor.Type]]): Behavior[Command] =
    Behaviors.setup: context =>
      val zoneActors: Map[String, ActorRef[ZoneActor.Command]] = zones.view.map(z => (z._1, context.spawn(ZoneActor(z._2, context.self), z._1))).toMap
      active(zoneActors, Map.empty)

  private def active(disarmedZones: Map[String, ActorRef[ZoneActor.Command]], armedZones: Map[String, ActorRef[ZoneActor.Command]]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case Arm(zone) if !armedZones.contains(zone) && disarmedZones.contains(zone) => arm(Set(disarmedZones(zone)), disarmedZones.removed(zone), armedZones + (zone -> disarmedZones(zone)))
        case Disarm(zone) if armedZones.contains(zone) && !disarmedZones.contains(zone) => disarm(Set(armedZones(zone)), disarmedZones + (zone -> armedZones(zone)), armedZones.removed(zone))
        case ArmAll => arm(disarmedZones.values.toSet, Map.empty, armedZones ++ disarmedZones)
        case DisarmAll => arm(armedZones.values.toSet, armedZones ++ disarmedZones, Map.empty)
        case _ => active(disarmedZones, armedZones)

  private def arm(zonesToArm: Set[ActorRef[ZoneActor.Command]], disarmedZones: Map[String, ActorRef[ZoneActor.Command]], armedZones: Map[String, ActorRef[ZoneActor.Command]]): Behavior[Command] =
    // send arm command to "zones"
    active(disarmedZones, armedZones)

  private def disarm(zonesToDisarm: Set[ActorRef[ZoneActor.Command]], disarmedZones: Map[String, ActorRef[ZoneActor.Command]], armedZones: Map[String, ActorRef[ZoneActor.Command]]): Behavior[Command] =
    // send disarm command zones
    active(disarmedZones, armedZones)

