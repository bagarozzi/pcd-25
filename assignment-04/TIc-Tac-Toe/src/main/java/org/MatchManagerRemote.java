package org;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatchManagerRemote extends Remote {
    public void notifyMove(Pair pos,char id) throws RemoteException;
}
