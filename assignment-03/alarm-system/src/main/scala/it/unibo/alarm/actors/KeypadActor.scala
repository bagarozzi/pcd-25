package it.unibo.alarm.actors

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors

object KeypadActor:

  import it.unibo.alarm.actors.AlarmActor.Command.*

  enum Command:
    case Arm(pin: String, zone: String)
    case ArmAll(pin: String)
    case Disarm(pin: String, zone: String)
    case DisarmAll(pin: String)
    case Silence(pin: String)
    case EntryAlert(triggeredZones: Set[String])
    case ExitAlert(armedZones: Set[String])
    case AlarmAlert


  export Command.*

  def apply(pin: String, alarmActor: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.setup: context =>
      context.log.info("initialized")
      active(pin, alarmActor)

  private def active(pin: String, alarmActor: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case Arm(pin, zone) => alarmActor ! AlarmActor.Command.Arm(zone) ; active(pin, alarmActor)
        case Disarm(pin, zone) => alarmActor ! AlarmActor.Command.Disarm(zone) ; active(pin, alarmActor)
        case ArmAll(pin) => alarmActor ! AlarmActor.Command.ArmAll ; active(pin, alarmActor)
        case DisarmAll(pin) => alarmActor ! AlarmActor.Command.DisarmAll ; active(pin, alarmActor)
        case EntryAlert(triggeredZones) => context.log.warn(s"The zones $triggeredZones are triggered, alarm will sound in 30 seconds") ; entryAlert(pin, alarmActor)
        case ExitAlert(armedZones) => context.log.info(s"The zones $armedZones be armed in 30 seconds") ; active(pin, alarmActor)
        case _ => Behaviors.same

  private def entryAlert(pin: String, alarmActor: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case DisarmAll(pin) => alarmActor ! AlarmActor.Command.DisarmAll ; active(pin, alarmActor)
        case Disarm(pin, zone) => alarmActor ! AlarmActor.Command.Disarm(zone) ; active(pin, alarmActor)
        case AlarmAlert => alarm(pin, alarmActor)
        case _ => Behaviors.same

  private def alarm(pin: String, alarmActor: ActorRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      context.log.info("ALARM STATE: insert pin to silence the alarm")
      message match
        case Silence(pin) => active(pin, alarmActor)
        case _ => Behaviors.same