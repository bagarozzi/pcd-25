package org;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RunServer {
    public static String SERVER_NAME = "MyServer";
    public static void main(String[] args){
        try {
            Server server = new GameManager();
            Server remoteServer = (Server) UnicastRemoteObject.exportObject(server, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(SERVER_NAME, remoteServer);

            System.out.println("SERVER  registered.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }
}
