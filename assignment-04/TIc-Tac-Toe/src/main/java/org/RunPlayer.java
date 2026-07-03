package org;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.player.PlayerImpl;
import org.server.Server;

public class RunPlayer {
    public static void main(String[] args) throws Exception {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Server server = (Server) registry.lookup(RunServer.SERVER_NAME);

            PlayerImpl p = new PlayerImpl(server);
        } catch (Exception e) {
            System.err.println("Player exception: " + e.getMessage());
        }
    }
}
