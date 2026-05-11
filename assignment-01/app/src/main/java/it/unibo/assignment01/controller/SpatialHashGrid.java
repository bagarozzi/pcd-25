package it.unibo.assignment01.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unibo.assignment01.model.Ball;

public class SpatialHashGrid {

    private final double cellSize;

    private final Map<Long, List<Ball>> grid = new HashMap<>();

    public SpatialHashGrid(double cellSize) {
        this.cellSize = cellSize;
    }

    public void clear() {
        grid.clear();
    }

    private long hash(int x, int y) {
        return (((long)x) << 32) | (y & 0xffffffffL);
    }

    public void insert(Ball b) {

        int cellX = (int)Math.floor(b.getPos().x() / cellSize);
        int cellY = (int)Math.floor(b.getPos().y() / cellSize);

        long key = hash(cellX, cellY);

        grid.computeIfAbsent(key, k -> new ArrayList<>()).add(b);
    }

    public List<Ball> getCell(int x, int y) {
        return grid.getOrDefault(hash(x, y), List.of());
    }

    public int getCellX(Ball b) {
        return (int)Math.floor(b.getPos().x() / cellSize);
    }

    public int getCellY(Ball b) {
        return (int)Math.floor(b.getPos().y() / cellSize);
    }
}