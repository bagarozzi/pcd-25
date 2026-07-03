package org.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.player.PlayerRemote;

public class GameManager implements Server{
    List<MatchManager> matches;


    public GameManager(){
        matches = new ArrayList<>();
    }

    private MatchManagerImpl createGame(PlayerRemote player) {
        try {
            MatchManagerImpl match = new MatchManagerImpl();
            matches.add(match);
            match.addPlayer(player);
            return match;
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public synchronized MatchManagerRemote joinGame(PlayerRemote player) throws RemoteException {
        Optional<MatchManager> res = matches.stream().filter(MatchManager::matchNotFull)
                .findFirst();
        if(res.isPresent()){
            res.get().addPlayer(player);
            return (MatchManagerImpl) res.get();
        }
        return createGame(player);
    }
}
