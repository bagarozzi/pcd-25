package it.unibo.alarm.cluster

import com.typesafe.config.ConfigFactory
import it.unibo.alarm.actors.{SensorActor, ZoneActor}
import it.unibo.alarm.cluster.ZoneNode.initialization
import org.apache.pekko.actor.typed.{ActorSystem, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object SensorNode:
    def apply(sensorId: String, sensorType: SensorActor.Type, zoneId: String): Unit =
        val config = ConfigFactory.parseString("""
                    pekko.remote.artery.canonical.port = 0
                    pekko.cluster.roles = ["sensor-node"]
                    """).withFallback(ConfigFactory.load("application.conf"))
        val _ = ActorSystem(initialization(sensorId, sensorType, zoneId), "AlarmCluster", config)

    private def initialization(sensorId: String, sensorType: SensorActor.Type, zoneId: String): Behavior[SensorActor.Command] =
        Behaviors.setup: context =>
            val sharding = ClusterSharding(context.system)

            sharding.init(Entity(typeKey = ZoneActor.TypeKey) { entityContext =>
                ZoneActor(entityContext.entityId: String)
            }.withRole("worker-node"))

            SensorActor(sensorId, zoneId, sensorType)


