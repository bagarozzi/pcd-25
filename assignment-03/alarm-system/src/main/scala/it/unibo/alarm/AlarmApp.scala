package it.unibo.alarm

import it.unibo.alarm.actors.KeypadActor.Command.LinkAlarm
import it.unibo.alarm.actors.{AlarmActor, KeypadActor, SensorActor}
import org.apache.pekko.actor.typed.scaladsl.*
import org.apache.pekko.actor.typed.*

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object AlarmApp:

  @main def app(): Unit =
    import actors.AlarmActor
    import AlarmProtocol.*

    val homeMap = Map(
      "kitchen" -> Set(SensorActor.Type.Door, SensorActor.Type.Motion),
      "outside" -> Set(SensorActor.Type.Door, SensorActor.Type.Motion),
    )

    val system = ActorSystem(
      KeypadActor("1234",
        homeMap,
        FiniteDuration(20, TimeUnit.SECONDS),
        FiniteDuration(10, TimeUnit.SECONDS)),
      "SmartAlarm"
    )

    system.tell(KeypadActor.Arm("1234", "kitchen"))

