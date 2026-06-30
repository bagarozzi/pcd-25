package org;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PlayerRemote extends Remote {

    enum Result{
        WIN,
        DRAW,
        LOSE
    }

    void notifyToMove() throws RemoteException;
    void blockMove() throws RemoteException;
    void notifyOpponentMove(Pair pos, Character sign) throws RemoteException;
    void notifyEndGame(Result res) throws RemoteException;
    void notifyWinOrLose(boolean winner) throws RemoteException;
    void enterGame(char sign) throws RemoteException;
    public void notifyWaiting() throws RemoteException;
}