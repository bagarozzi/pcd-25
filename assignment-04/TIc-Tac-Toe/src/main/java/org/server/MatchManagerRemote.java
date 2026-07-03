package org.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.utilities.Pair;

public interface MatchManagerRemote extends Remote {
    public void notifyMove(Pair pos,char id) throws RemoteException;
}
