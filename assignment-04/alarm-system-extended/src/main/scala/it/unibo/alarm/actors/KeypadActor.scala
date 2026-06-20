package it.unibo.alarm.actors

import it.unibo.alarm.AlarmProtocol
import it.unibo.alarm.cluster.CborSerializable
import org.apache.pekko.actor.typed.pubsub.Topic
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef, EntityTypeKey}
import scala.reflect.ClassTag

object KeypadActor:

  val TypeKey: EntityTypeKey[Command] = EntityTypeKey[Command]("AlarmHub")

  enum Command extends CborSerializable:
    case Arm(pin: String, zone: String)
    case ArmAll(pin: String)
    case Disarm(pin: String, zone: String)
    case DisarmAll(pin: String)
    case Silence(pin: String)
    case EntryAlert(triggeredZones: String)
    case ExitAlert(armedZones: Set[String])
    case AlarmAlert
    case LinkAlarm(alarmActor: ActorRef[AlarmActor.Command])


  export Command.*

  def apply(keypadId: String, pin: String): Behavior[Command] =
    Behaviors.setup: context =>
      context.log.info(s"Keypad $keypadId initialized with PIN $pin" )

      val topic = context.spawn(Topic[Command](AlarmProtocol.KeypadTopicName), "KeypadTopicProxy")
      topic ! Topic.Subscribe(context.self)

      val sharding = ClusterSharding(context.system)
      val alarmActorRef = sharding.entityRefFor(AlarmActor.TypeKey, "central-alarm")

      active(pin, alarmActorRef)

  private def active(pin: String, alarmActor: EntityRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case Arm(insertedPin, zone) if insertedPin == pin => alarmActor ! AlarmActor.Command.Arm(zone) ; active(pin, alarmActor)
        case Disarm(insertedPin, zone) if insertedPin == pin => alarmActor ! AlarmActor.Command.Disarm(zone) ; active(pin, alarmActor)
        case ArmAll(insertedPin) if insertedPin == pin => alarmActor ! AlarmActor.Command.ArmAll() ; active(pin, alarmActor)
        case DisarmAll(insertedPin) if insertedPin == pin =>alarmActor ! AlarmActor.Command.DisarmAll() ; active(pin, alarmActor)
        case EntryAlert(triggeredZones) => context.log.warn(s"The zone $triggeredZones is triggered, alarm will sound unless the PIN is inserted") ; entryAlert(pin, alarmActor)
        case ExitAlert(armedZones) => context.log.info(s"The zones $armedZones be armed soon") ; active(pin, alarmActor)
        case _ => Behaviors.same

  private def entryAlert(pin: String, alarmActor: EntityRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case DisarmAll(insertedPin) if insertedPin == pin => alarmActor ! AlarmActor.Command.DisarmAll() ; active(pin, alarmActor)
        case Disarm(insertedPin, zone) if insertedPin == pin => alarmActor ! AlarmActor.Command.Disarm(zone) ; active(pin, alarmActor)
        case Disarm(insertedPin, _) if insertedPin != pin =>
          context.log.info("ALARM STATE: insert pin to silence the alarm")
          alarmActor ! AlarmActor.Command.Trigger ; alarm(pin, alarmActor)
        case DisarmAll(insertedPin) if insertedPin != pin =>
          context.log.info("ALARM STATE: insert pin to silence the alarm")
          alarmActor ! AlarmActor.Command.Trigger ; alarm(pin, alarmActor)
        case AlarmAlert =>
          context.log.info("ALARM STATE: insert pin to silence the alarm")
          alarm(pin, alarmActor)
        case _ => Behaviors.same

  private def alarm(pin: String, alarmActor: EntityRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case Silence(insertedPin) if insertedPin == pin =>
          context.log.info("Pin correct, alarm silenced")
          alarmActor ! AlarmActor.Command.Silence ; active(pin, alarmActor)
        case _ => Behaviors.same