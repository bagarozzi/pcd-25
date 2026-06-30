package org;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RunPlayer {
    public static void main(String[] args) throws Exception {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Server server = (Server) registry.lookup(RunServer.SERVER_NAME);

            PlayerImpl p = new PlayerImpl(server);

            System.out.println("SERVER  registered.");
        } catch (Exception e) {
            System.err.println("Player exception: " + e.getMessage());
        }
    }
}
