package it.unibo.alarm.cluster


import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.typed.{ActorSystem, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}

import scala.concurrent.duration.FiniteDuration
import it.unibo.alarm.actors.{AlarmActor, KeypadActor, SensorActor, ZoneActor}

object KeypadNode:

    def apply(pin: String): Unit =
        val config = ConfigFactory.parseString(
            s"""
          pekko.remote.artery.canonical.port = 7354
          """).withFallback(ConfigFactory.load("application-sharding.conf"))
        val _ = ActorSystem(initialization(pin), "AlarmCluster", config)

    private def initialization(pin: String): Behavior[Nothing] =
        Behaviors.setup: context =>
            val sharding = ClusterSharding(context.system)

            val _ = sharding.init(Entity(typeKey = KeypadActor.TypeKey) { entityContext =>
                KeypadActor(entityContext.entityId: String, pin)
            })
            Behaviors.empty





