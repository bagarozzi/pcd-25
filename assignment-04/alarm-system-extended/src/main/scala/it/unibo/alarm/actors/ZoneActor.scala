package it.unibo.alarm.actors


import ch.qos.logback.core.model.processor.AllowAllModelFilter
import it.unibo.alarm.cluster.CborSerializable
import org.apache.pekko.actor.typed.*
import org.apache.pekko.actor.typed.scaladsl.*
import org.apache.pekko.cluster.sharding.typed.scaladsl.EntityTypeKey
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityRef}
import com.typesafe.config.ConfigFactory
import it.unibo.alarm.actors.AlarmActor.Command.Trigger
import org.apache.pekko.cluster.sharding.ShardRegion.ClusterShardingStats

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

/**
 * A ZoneActor manages a zone of a house, groups together sensors from a zone and reports triggers.
 * It can be enabled and disabled.
 */
object ZoneActor:

  sealed trait Command extends CborSerializable

  val TypeKey: EntityTypeKey[Command] = EntityTypeKey[Command]("ZoneEntity")

  object Command:
    case object Arm extends Command
    case object Disarm extends Command
    case object Alert extends Command
    case object EntryDelayOver extends Command
    case object ArmDelayOver extends Command

  export Command.*

  /**
   * Initialize the Zone actor and the sensors passed.
   * @param zoneId the name of this zone
   * @return the actor's behavior
   */
  def apply(zoneId: String): Behavior[Command] =
    Behaviors.setup: context =>
        Behaviors.withTimers: timers  =>
          val config = context.system.settings.config

          def getTimeout(configKey: String): FiniteDuration =
            val specificPath = s"alarm-system.zones.$zoneId.$configKey"
            val defaultPath = s"alarm-system.zones.default.$configKey"

            val pathToUse = if (config.hasPath(specificPath)) specificPath else defaultPath
            val durationStr = config.getDuration(pathToUse).toMillis
            FiniteDuration(durationStr, TimeUnit.MILLISECONDS)

          // 4. Retrieve the timeouts
          val entryTimeout = getTimeout("entry-timeout")
          val exitTimeout = getTimeout("exit-timeout")

          context.log.info(s"Spawned sharded ZoneActor for $zoneId with timeouts $entryTimeout/$exitTimeout")


          val sharding = ClusterSharding(context.system)
          val alarmActorRef: EntityRef[AlarmActor.Command] = sharding.entityRefFor(AlarmActor.TypeKey, "central-alarm")

          new ZoneActor(zoneId, alarmActorRef, timers, entryTimeout, exitTimeout).disarmed()


  class ZoneActor(
    val zoneId: String,
    val alarmActor: EntityRef[AlarmActor.Command],
    val timers: TimerScheduler[Command],
    val entryTimeout: FiniteDuration,
    val exitTimeout: FiniteDuration,
                 ):

    def disarmed(): Behavior[Command] =
      Behaviors.receive: (context, message) =>
        message match
          case Arm =>
            context.log.info(s"Zone $zoneId started arming.")
            exitDelay()
          case _ => Behaviors.same

    private def exitDelay(): Behavior[Command] =
      timers.startSingleTimer(ArmDelayOver, exitTimeout)
      Behaviors.receive: (context, message) =>
        message match
          case Disarm =>
            context.log.info(s"Zone $zoneId disarmed.")
            disarmed()
          case ArmDelayOver =>
            context.log.info(s"Zone $zoneId armed.")
            armed()
          case _ => Behaviors.same

    private def armed(): Behavior[Command] =
      Behaviors.receive: (context, message) =>
        message match
          case Alert =>
            alarmActor ! AlarmActor.Command.Alert(context.self.path.toStringWithoutAddress)
            context.log.info(s"Zone $zoneId intrusion detected, started timer.")
            entryDelay()
          case Disarm =>
            context.log.info(s"Zone $zoneId disarmed.")
            disarmed()
          case _ => Behaviors.same

    private def alarm() : Behavior[Command] =
      Behaviors.receive: (context, message) =>
        message match
          case Disarm =>
            context.log.info(s"Zone $zoneId disarmed.")
            disarmed()
          case _ => Behaviors.same

    private def entryDelay(): Behavior[Command] =
      timers.startSingleTimer(EntryDelayOver, entryTimeout)
      Behaviors.receive: (context, message) =>
        message match
        case Disarm  =>
          context.log.info(s"Zone $zoneId disarmed.")
          disarmed()
        case EntryDelayOver =>
          context.log.info(s"Entry delay over in zone $zoneId")
          alarmActor ! Trigger
          alarm()
        case _ => Behaviors.same