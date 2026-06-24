package it.unibo.alarm.actors

import it.unibo.alarm.AlarmProtocol
import it.unibo.alarm.cluster.CborSerializable
import org.apache.pekko.actor.typed.pubsub.Topic
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef, EntityTypeKey}
import scala.reflect.ClassTag

object KeypadActor:

  sealed trait Command extends CborSerializable

  val TypeKey: EntityTypeKey[Command] = EntityTypeKey[Command]("AlarmHub")

  object Command:
    case class Arm(pin: String, zone: String) extends Command
    case class ArmAll(pin: String) extends Command
    case class Disarm(pin: String, zone: String) extends Command
    case class DisarmAll(pin: String) extends Command
    case class Silence(pin: String) extends Command
    case class EntryAlert(triggeredZones: String) extends Command
    case class ExitAlert(armedZones: Set[String]) extends Command
    case object AlarmAlert extends Command
    case class LinkAlarm(alarmActor: ActorRef[AlarmActor.Command]) extends Command
    case object OtherKeypadSilenced extends Command
    case object OtherKeypadDisarmed extends Command


  export Command.*

  def apply(keypadId: String, pin: String): Behavior[Command] =
    Behaviors.setup: context =>
      context.log.info(s"Keypad $keypadId initialized with PIN $pin" )

      val topic = context.spawn(Topic[Command](AlarmProtocol.KeypadTopicName), "KeypadTopicProxy")
      topic ! Topic.Subscribe(context.self)

      val sharding = ClusterSharding(context.system)
      val alarmActorRef = sharding.entityRefFor(AlarmActor.TypeKey, "central-alarm")

      active(topic, keypadId, pin, alarmActorRef)

  private def active(keypadTopic:ActorRef[Topic.Command[KeypadActor.Command]], keypadId: String, pin: String, alarmActor: EntityRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case Arm(insertedPin, zone) if insertedPin == pin =>
          alarmActor ! AlarmActor.Command.Arm(zone)
          active(keypadTopic, keypadId, pin, alarmActor)
        case Disarm(insertedPin, zone) if insertedPin == pin =>
          alarmActor ! AlarmActor.Command.Disarm(zone)
          active(keypadTopic, pin, keypadId, alarmActor)
        case ArmAll(insertedPin) if insertedPin == pin =>
          alarmActor ! AlarmActor.Command.ArmAll
          active(keypadTopic, keypadId, pin, alarmActor)
        case DisarmAll(insertedPin) if insertedPin == pin =>
          alarmActor ! AlarmActor.Command.DisarmAll
          active(keypadTopic, keypadId, pin, alarmActor)
        case EntryAlert(triggeredZones) =>
          context.log.warn(s"KEYPAD-$keypadId:  The zone $triggeredZones is triggered, alarm will sound unless the PIN is inserted")
          entryAlert(keypadTopic, keypadId, pin, alarmActor)
        case ExitAlert(armedZones) =>
          context.log.info(s"KEYPAD-$keypadId: The zones $armedZones be armed soon")
          active(keypadTopic, keypadId, pin, alarmActor)
        case _ => Behaviors.same

  private def entryAlert(keypadTopic:ActorRef[Topic.Command[KeypadActor.Command]], keypadId: String, pin: String, alarmActor: EntityRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case DisarmAll(insertedPin) if insertedPin == pin =>
          context.log.info(s"KEYPAD-$keypadId: the alarm was disarmed within the entry-timeout")
          keypadTopic ! Topic.publish(OtherKeypadDisarmed)
          alarmActor ! AlarmActor.Command.DisarmAll
          active(keypadTopic, keypadId, pin, alarmActor)
        case Disarm(insertedPin, zone) if insertedPin == pin =>
          context.log.info(s"KEYPAD-$keypadId: the zone $zone was disarmed within the entry-timeout")
          keypadTopic ! Topic.publish(OtherKeypadDisarmed)
          alarmActor ! AlarmActor.Command.Disarm(zone)
          active(keypadTopic, keypadId, pin, alarmActor)
        case Disarm(insertedPin, _) if insertedPin != pin =>
          context.log.info(s"KEYPAD-$keypadId: ALARM STATE: insert pin to silence the alarm")
          alarmActor ! AlarmActor.Command.Trigger
          alarm(keypadTopic, keypadId, pin, alarmActor)
        case DisarmAll(insertedPin) if insertedPin != pin =>
          context.log.info(s"KEYPAD-$keypadId: ALARM STATE: insert pin to silence the alarm")
          alarmActor ! AlarmActor.Command.Trigger
          alarm(keypadTopic, keypadId, pin, alarmActor)
        case AlarmAlert =>
          context.log.info(s"KEYPAD-$keypadId: ALARM STATE: insert pin to silence the alarm")
          alarm(keypadTopic, keypadId, pin, alarmActor)
        case OtherKeypadDisarmed =>
          context.log.warn(s"KEYPAD-$keypadId: alarm was disarmed from another keypad")
          active(keypadTopic, keypadId, pin, alarmActor)
        case _ => Behaviors.same

  private def alarm(keypadTopic:ActorRef[Topic.Command[KeypadActor.Command]], keypadId: String, pin: String, alarmActor: EntityRef[AlarmActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case Silence(insertedPin) if insertedPin == pin =>
          context.log.info(s"KEYPAD-$keypadId: Pin correct, alarm silenced")
          keypadTopic ! Topic.publish(OtherKeypadSilenced)
          alarmActor ! AlarmActor.Command.Silence
          active(keypadTopic, keypadId, pin, alarmActor)
        case OtherKeypadSilenced =>
          context.log.warn(s"KEYPAD-$keypadId: alarm was silenced from another keypad")
          active(keypadTopic, keypadId, pin, alarmActor)
        case _ => Behaviors.same