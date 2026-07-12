package it.unibo.alarm.actors

import com.typesafe.config.ConfigFactory
import it.unibo.alarm.actors.KeypadActor.Command.Silence
import it.unibo.alarm.actors.ZoneActor.Command.Alert
import it.unibo.alarm.cluster.{AlarmNode, KeypadNode, SensorNode, ZoneNode}
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
    case LookForSensors

  export Command.*

  private val sensorKey: ServiceKey[SensorActor.Command] = SensorActor.createSensorKey("sensor")

  def apply(): Behavior[Command] =
          Behaviors.withTimers: timer =>
              Behaviors.setup: context =>
                val listingAdapter: ActorRef[Receptionist.Listing] =
                  context.messageAdapter(listing => SensorsUpdated(listing))
                timer.startTimerAtFixedRate(LookForSensors, FiniteDuration(4, TimeUnit.SECONDS))
                active(listingAdapter, Set())

  private def active(listingAdapter: ActorRef[Receptionist.Listing], sensors: Set[ActorRef[SensorActor.Command]]): Behavior[Command] =
    Behaviors.receive: (context, message) =>
      message match
        case TriggerSensors =>
          sensors.foreach(s => s ! SensorActor.Trigger)
          Behaviors.same
        case SensorsUpdated(listing) =>
          active(listingAdapter, listing.serviceInstances(sensorKey))
        case LookForSensors =>
          context.system.receptionist ! Receptionist.Find(sensorKey, listingAdapter)
          Behaviors.same

  @main
  def run(): Unit =

    val zones = Set(
        "firstFloor",
        "groundFloor",
        "garden"
    )

    val firstFloorSensors = Map(
        "hallway-motion" -> SensorActor.Type.Motion,
        "bedroom-window" -> SensorActor.Type.Door
    )

      val groundFloorSensors = Map(
          "kitchen-motion" -> SensorActor.Type.Motion,
          "front-door" -> SensorActor.Type.Door
      )

      val outsideSensors = Map(
          "porch-motion" -> SensorActor.Type.Motion,
          "garden-motion" -> SensorActor.Type.Motion,
      )

    println("-" * 100)
    println("-" * 50 + " STARTING CENTRAL ALARM NODE " + "-" * 50)
    println("-" * 100)

    AlarmNode(zones)

    Thread.sleep(3000)

    println("-" * 100)
    println("-" * 50 + " STARTING ZONE NODES " + "-" * 50)
    println("-" * 100)

    ZoneNode(FiniteDuration(10, TimeUnit.SECONDS), FiniteDuration(15, TimeUnit.SECONDS))
    ZoneNode(FiniteDuration(10, TimeUnit.SECONDS), FiniteDuration(20, TimeUnit.SECONDS))
    ZoneNode(FiniteDuration(10, TimeUnit.SECONDS), FiniteDuration(22, TimeUnit.SECONDS))

    firstFloorSensors.foreach( (id, st) => SensorNode(id,st, "firstFloor"))
    groundFloorSensors.foreach( (id, st) => SensorNode(id,st, "groundFloor"))
    outsideSensors.foreach( (id, st) => SensorNode(id,st, "garden"))

    Thread.sleep(5000)

    println("-" * 100)
    println("-" * 50 + " STARTING KEYPAD NODES " + "-" * 50)
    println("-" * 100)


    val kitchenKeypad = KeypadNode("kp-kitchen", "1234")
    val bedroomKeypad = KeypadNode("kp-bedroom", "4321")

    Thread.sleep(5000)

    println("-" * 100)
    println("-" * 50 + " STARTING USER NODE " + "-" * 50)
    println("-" * 100)


    val config = ConfigFactory.parseString("""
                pekko.remote.artery.canonical.port = 0
                pekko.cluster.roles = ["user-node"]
                """).withFallback(ConfigFactory.load("application.conf"))

    val intruder = ActorSystem(AlarmUser(), "AlarmCluster", config)

    Thread.sleep(5000)

    println("-" * 100)
    println("-" * 50 + " TRIGGERING DISARM ALL TO LOAD THE SHARDS " + "-" * 50)
    println("-" * 100)


    kitchenKeypad ! KeypadActor.DisarmAll("1234")

    Thread.sleep(5000)

    println("-"*50 + "INITIALIZATION DONE" + "-"*50)
    println("-"*100)
    println("-"*50 + "Sensors triggering on alarm disarmed" + "-"*50 + "\n")

    intruder ! TriggerSensors

    Thread.sleep(1000)

    println("-" * 100)
    println("-" * 50 + "Arming zone \"First Floor\"" + "-" * 50 + "\n")

    kitchenKeypad ! KeypadActor.Arm("1234", "firstFloor")

    Thread.sleep(19500)

    println("-" * 100)
    println("-" * 50 + "Triggering sensors" + "-" * 50 + "\n")
    intruder ! TriggerSensors

    Thread.sleep(2000)
    println("-" * 100)
    println("-" * 50 + "Disarming within the entry-delay" + "-" * 50 + "\n")

    kitchenKeypad ! KeypadActor.Disarm("1234","firstFloor")

    Thread.sleep(2000)

    println("-" * 100)
    println("-" * 50 + "Arming zone \"Garden\"" + "-" * 50 + "\n")

    bedroomKeypad ! KeypadActor.Arm("4321", "garden")

    Thread.sleep(16500)

    println("-" * 100)
    println("-" * 50 + "Triggering sensors" + "-" * 50 + "\n")

    intruder ! TriggerSensors

    Thread.sleep(2000)

    println("-" * 100)
    println("-" * 50 + "Wrong pin on entry-delay, alarm should sound immediately" + "-" * 50 + "\n")

    bedroomKeypad ! KeypadActor.Disarm("5678","garden")

    Thread.sleep(5000)

    println("-" * 100)
    println("-" * 50 + "Silencing alarm" + "-" * 50 + "\n")
    bedroomKeypad ! KeypadActor.Silence("4321")