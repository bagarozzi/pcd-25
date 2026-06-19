package it.unibo.alarm.cluster

import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.typed.{ActorSystem, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}

import scala.concurrent.duration.FiniteDuration
import it.unibo.alarm.actors.SensorActor
import it.unibo.alarm.actors.ZoneActor

object ZoneNode:

    def apply(sensors: Set[SensorActor.Type], entryTimeout: FiniteDuration, exitTimeout: FiniteDuration): Unit =
        val config = ConfigFactory.parseString(
            s"""
          pekko.remote.artery.canonical.port = 7354
          """).withFallback(ConfigFactory.load("application-sharding.conf"))
        val _ = ActorSystem(initialization(sensors, entryTimeout, exitTimeout), "AlarmCluster", config)

    private def initialization(sensors: Set[SensorActor.Type], entryTimeout: FiniteDuration, exitTimeout: FiniteDuration): Behavior[Nothing] =
        Behaviors.setup: context =>
            val sharding = ClusterSharding(context.system)

            val _ = sharding.init(Entity(typeKey = ZoneActor.TypeKey) { entityContext =>
                ZoneActor(entityContext.entityId: String, sensors: Set[SensorActor.Type], entryTimeout: FiniteDuration, exitTimeout: FiniteDuration)
            })
            Behaviors.empty





