package it.unibo.assignment01.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jpftesting.model.Ball;

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
        // Morton code (Z-order curve) for better 2D spatial locality
        long result = 0;
        for (int i = 0; i < 32; i++) {
            result |= ((long) ((x >> i) & 1) << (2 * i));
            result |= ((long) ((y >> i) & 1) << (2 * i + 1));
        }
        return result;
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