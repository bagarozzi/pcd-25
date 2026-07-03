package org.server;

import org.player.PlayerRemote;

public interface MatchManager {

    public void addPlayer(PlayerRemote player);
    public boolean matchNotFull();
}
