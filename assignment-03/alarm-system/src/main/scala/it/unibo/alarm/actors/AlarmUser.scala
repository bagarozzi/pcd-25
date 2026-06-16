package it.unibo.alarm.actors

import it.unibo.alarm.actors.KeypadActor.Command.Silence
import it.unibo.alarm.actors.ZoneActor.Command.Alert
import org.apache.pekko.actor.typed.*
import org.apache.pekko.actor.typed.receptionist.{Receptionist, ServiceKey}
import org.apache.pekko.actor.typed.scaladsl.*

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag

object AlarmUser:

  enum Command:
    case TriggerSensors
    case SensorsUpdated(listing: Receptionist.Listing)
    case Arm(zone: String)
    case Disarm(zone: String)
    case LookForSensors
    case WrongPin

  export Command.*

  private val sensorKey: ServiceKey[SensorActor.Command] = SensorActor.createSensorKey("sensor")

  def apply(
         pin: String,
         zones: Map[String, Set[SensorActor.Type]],
         entryTimeout: FiniteDuration,
         exitTimeout: FiniteDuration
           ): Behavior[Command] =
    Behaviors.withTimers: timer =>
      Behaviors.setup: context =>
        val listingAdapter: ActorRef[Receptionist.Listing] =
          context.messageAdapter(listing => SensorsUpdated(listing))
        timer.startTimerAtFixedRate(LookForSensors, FiniteDuration(2, TimeUnit.SECONDS))
        val keypad = context.spawn(KeypadActor(pin, zones, entryTimeout, exitTimeout), "keypad")
        active(listingAdapter, pin, Set(), keypad)

  private def active(listingAdapter: ActorRef[Receptionist.Listing], pin: String, sensors: Set[ActorRef[SensorActor.Command]],keypad: ActorRef[KeypadActor.Command]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case TriggerSensors =>
          sensors.foreach(s => s ! SensorActor.Trigger)
          Behaviors.same
        case SensorsUpdated(listing) =>
          active(listingAdapter, pin, listing.serviceInstances(sensorKey), keypad)
        case Command.Arm(zone) =>
          keypad ! KeypadActor.Command.Arm(pin, zone)
          Behaviors.same
        case Command.Disarm(zone) =>
          keypad ! KeypadActor.Command.Disarm(pin, zone)
          Behaviors.same
        case LookForSensors =>
          context.system.receptionist ! Receptionist.Find(sensorKey, listingAdapter)
          Behaviors.same
        case WrongPin =>
          keypad ! KeypadActor.Disarm("4321", "outside")
          Behaviors.same


  @main
  def run(): Unit =
    val homeMap = Map(
      "kitchen" -> Set(SensorActor.Type.Door, SensorActor.Type.Motion),
      "outside" -> Set(SensorActor.Type.Door, SensorActor.Type.Motion),
    )

    val system = ActorSystem(
      AlarmUser("1234", homeMap, FiniteDuration(15, TimeUnit.SECONDS), FiniteDuration(10, TimeUnit.SECONDS)),
      "SmartAlarm"
    )

    system ! Arm("kitchen")

    Thread.sleep(15000)

    system ! Disarm("kitchen")

    system ! TriggerSensors

    system ! Arm("outside")

    Thread.sleep(15000)

    system ! TriggerSensors

    Thread.sleep(1000)

    system ! WrongPin
