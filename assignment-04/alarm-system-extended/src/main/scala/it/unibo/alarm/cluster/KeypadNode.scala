package it.unibo.alarm.cluster


import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.typed.{ActorSystem, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}

import scala.concurrent.duration.FiniteDuration
import it.unibo.alarm.actors.{AlarmActor, KeypadActor, SensorActor, ZoneActor}

object KeypadNode:

    def apply(keypadId: String, pin: String): ActorSystem[KeypadActor.Command] =
        val config = ConfigFactory.parseString("pekko.remote.artery.canonical.port = 0")
            .withFallback(ConfigFactory.load("application.conf"))
        ActorSystem(init(keypadId, pin), "AlarmCluster", config)

    private def init(keypadId: String, pin: String): Behavior[KeypadActor.Command] =
        Behaviors.setup: context =>
            val sharding = ClusterSharding(context.system)

            sharding.init(Entity(typeKey = AlarmActor.TypeKey) { entityContext =>
                AlarmActor(entityContext.entityId, Set.empty)
            }.withRole("central-node"))

            KeypadActor(keypadId, pin)





