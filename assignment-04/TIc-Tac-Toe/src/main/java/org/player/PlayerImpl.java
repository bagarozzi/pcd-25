package org.player;

import javax.swing.*;

import org.gui.GameFrame;
import org.server.MatchManagerRemote;
import org.server.Server;
import org.utilities.Pair;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PlayerImpl extends UnicastRemoteObject implements Player, PlayerRemote{
    private final Server gm;
    private MatchManagerRemote match;
    private final GameFrame gui;
    private volatile char sign;

    public PlayerImpl(Server gm) throws RemoteException {
        super();
        this.gm = gm;
        this.gui = new GameFrame(this);
    }

    public Character getSign() {
        return sign;
    }

    @Override
    public void clearMatch() {
        match = null;
        sign = ' ';
    }

    @Override
    public void notifyToMove() throws RemoteException {
        gui.unlockMove();
    }

    @Override
    public void blockMove() throws RemoteException {
        gui.blockMove();
    }

    @Override
    public void notifyOpponentMove(Pair pos, Character sign) throws RemoteException {
        gui.update(pos, sign);
    }

    @Override
    public void notifyEndGame(Result res) throws RemoteException {
        gui.showEndGame(res);
    }

    @Override
    public void notifyWinOrLose(boolean winner) throws RemoteException {

    }

    @Override
    public void enterGame(char sign) throws RemoteException {
        this.sign = sign;
        gui.setGamePanel();
    }


    @Override
    public void joinGame() {
        try {
            this.match = gm.joinGame(this);
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void makeMove(Pair pos) {
        try {
            this.match.notifyMove(pos, this.sign);
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void notifyWaiting() throws RemoteException {
        SwingUtilities.invokeLater(gui::setWaitingPanel);
    }
}
