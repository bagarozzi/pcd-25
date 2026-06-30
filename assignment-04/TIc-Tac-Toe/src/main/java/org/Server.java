package org;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    public MatchManagerRemote joinGame(PlayerRemote player) throws RemoteException;
}
