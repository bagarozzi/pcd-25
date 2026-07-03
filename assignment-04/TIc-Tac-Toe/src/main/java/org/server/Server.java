package org.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.player.PlayerRemote;

public interface Server extends Remote {
    public MatchManagerRemote joinGame(PlayerRemote player) throws RemoteException;
}
