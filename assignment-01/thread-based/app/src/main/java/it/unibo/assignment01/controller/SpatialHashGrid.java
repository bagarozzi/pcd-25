package it.unibo.assignment01.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unibo.assignment01.model.Ball;

public class SpatialHashGrid {

    private final double cellSize;

    private final Map<Long, List<Ball>> grid = new HashMap<>();

    public SpatialHashGrid(double cellSize) {
        this.cellSize = cellSize;
    }

    public void clear() {
        grid.values().forEach(List::clear);
    }

    private long hash(int x, int y) {
        // Pack x into the upper 32 bits, and y into the lower 32 bits.
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }

    public void insert(Ball b) {

        int cellX = (int) Math.floor(b.getPos().x() / cellSize);
        int cellY = (int) Math.floor(b.getPos().y() / cellSize);

        long key = hash(cellX, cellY);
        List<Ball> cell = grid.get(key);

        if (cell == null) {
            cell = new ArrayList<>();
            grid.put(key, cell);
        }

        cell.add(b);
    }

    public List<Ball> getCell(int x, int y) {
        return grid.getOrDefault(hash(x, y), List.of());
    }

    public int getCellX(Ball b) {
        return (int) Math.floor(b.getPos().x() / cellSize);
    }

    public int getCellY(Ball b) {
        return (int) Math.floor(b.getPos().y() / cellSize);
    }

    public Set<Map.Entry<Long, List<Ball>>> getCells() {
        return grid.entrySet();
    }
}