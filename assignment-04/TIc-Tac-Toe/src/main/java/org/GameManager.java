package org;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameManager implements Server{
    List<MatchManager> matches;


    public GameManager(){
        matches = new ArrayList<>();
    }

    private MatchManagerImpl createGame(PlayerRemote player) {
        System.out.println("creating a new game");
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
    public MatchManagerRemote joinGame(PlayerRemote player) throws RemoteException {
        System.out.println("joining a new game");
        Optional<MatchManager> res = matches.stream().filter(MatchManager::matchNotFull)
                .findFirst();
        if(res.isPresent()){
            res.get().addPlayer(player);
            return (MatchManagerImpl) res.get();
        }
        return createGame(player);
    }
}
