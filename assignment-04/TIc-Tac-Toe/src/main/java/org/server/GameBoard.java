package org.server;

import org.utilities.Pair;

public class GameBoard {

    public static final int HEIGHT_WIDTH = 3;
    private final char[][] board;

    public GameBoard() {
        board = new char[HEIGHT_WIDTH][HEIGHT_WIDTH];
        for (int i = 0; i < HEIGHT_WIDTH; i++) {
            for (int j = 0; j < HEIGHT_WIDTH; j++) {
                board[i][j] = '.';
            }
        }
    }


    public enum result {
        WINNER_FOUND,
        WINNER_NOT_FOUND,
        DRAW
    }

    public synchronized result makeMove(Pair pos, char sign){
        board[pos.x()][pos.y()] = sign;
        return checkForWinner(pos, sign);
    }

    public synchronized char[][] getBoard(){
        return board;
    }

    private boolean boardIsFull(){
        for (int i = 0; i < HEIGHT_WIDTH; i++) {
            for (int j = 0; j < HEIGHT_WIDTH; j++) {
                if (board[i][j] == '.')
                    return false;
            }
        }
        return true;
    }

    private result checkForWinner(Pair lastMove, char sign){
        if(board[0][lastMove.y()] == sign
            & board[1][lastMove.y()] == sign
            & board[2][lastMove.y()] == sign
        ){
            return result.WINNER_FOUND;
        } else if(board[lastMove.x()][0] == sign
            &board[lastMove.x()][1] == sign
            & board[lastMove.x()][2] == sign
        ){
            return result.WINNER_FOUND;
        }else if(board[0][0] == sign
            & board[1][1] == sign
            &board[2][2] == sign
        ){
            return result.WINNER_FOUND;
        } else if(board[0][2] == sign
            & board[1][1] == sign
            &board[2][0] == sign
        ){
            return result.WINNER_FOUND;
        }
        if(boardIsFull()){
            return result.DRAW;
        }
        return result.WINNER_NOT_FOUND;
    }
}

