package org;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatchManagerImpl extends UnicastRemoteObject implements MatchManager, MatchManagerRemote {
    private final GameBoard board;
    private PlayerRemote firstPlayer;
    private PlayerRemote secondPlayer;
    private State state;

    private enum State{
        PLAYER_1_MOVE,
        PLAYER_2_MOVE,
    }

    public MatchManagerImpl() throws RemoteException {
        firstPlayer = null;
        secondPlayer = null;
        board = new GameBoard();
    }

    public void addPlayer(PlayerRemote player) {
        if(firstPlayer == null){
            System.out.println("trovato player 1");
            firstPlayer = player;
            try {
                firstPlayer.notifyWaiting();
            } catch (RemoteException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println("trovato player 2");
            secondPlayer = player;
            try {
                if((int)(Math.random()*2) == 0) {
                    firstPlayer.enterGame('X');
                    secondPlayer.enterGame('O');
                    secondPlayer.blockMove();
                    firstPlayer.notifyToMove();
                    state = State.PLAYER_1_MOVE;
                } else  {
                    firstPlayer.enterGame('0');
                    firstPlayer.blockMove();
                    secondPlayer.enterGame('X');
                    secondPlayer.notifyToMove();
                    state = State.PLAYER_2_MOVE;
                }
            } catch (RemoteException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public boolean matchNotFull(){
        return secondPlayer == null;
    }

    public void notifyMove(Pair pos,char sign) throws RemoteException {
        if (state == State.PLAYER_1_MOVE) {
            GameBoard.result res = board.makeMove(pos, sign);
            if (res == GameBoard.result.WINNER_FOUND) {
                firstPlayer.notifyEndGame(PlayerRemote.Result.WIN);
                secondPlayer.notifyEndGame(PlayerRemote.Result.LOSE);
            } else if(res == GameBoard.result.DRAW){
                firstPlayer.notifyEndGame(PlayerRemote.Result.DRAW);
                secondPlayer.notifyEndGame(PlayerRemote.Result.DRAW);
            }
            firstPlayer.blockMove();
            secondPlayer.notifyOpponentMove(pos, sign);
            secondPlayer.notifyToMove();
            state = State.PLAYER_2_MOVE;
        } else if (state == State.PLAYER_2_MOVE) {
            GameBoard.result res = board.makeMove(pos, sign);
            if (res == GameBoard.result.WINNER_FOUND) {
                firstPlayer.notifyEndGame(PlayerRemote.Result.LOSE);
                secondPlayer.notifyEndGame(PlayerRemote.Result.WIN);
            } else if(res == GameBoard.result.DRAW){
                firstPlayer.notifyEndGame(PlayerRemote.Result.DRAW);
                secondPlayer.notifyEndGame(PlayerRemote.Result.DRAW);
            }
            secondPlayer.blockMove();
            firstPlayer.notifyOpponentMove(pos, sign);
            firstPlayer.notifyToMove();
            state = State.PLAYER_1_MOVE;
        }
    }
}

