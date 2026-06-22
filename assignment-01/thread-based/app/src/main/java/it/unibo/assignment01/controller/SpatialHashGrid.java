package it.unibo.assignment01.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Boundary;

public class SpatialHashGrid {

    private final double cellSize;

    private final Boundary bounds; 

    private final int rows;
    private final int cols;

    //private final Map<Long, List<Ball>> grid = new HashMap<>();
    private final List<Ball>[] grid;

@SuppressWarnings("unchecked")
    public SpatialHashGrid(double cellSize, Boundary bounds) {
        this.cellSize = cellSize;
        this.bounds = bounds;
        
        this.cols = (int) Math.ceil((bounds.x1() - bounds.x0()) / cellSize);
        this.rows = (int) Math.ceil((bounds.y1() - bounds.y0()) / cellSize);

        this.grid = new ArrayList[cols * rows];
        for (int i = 0; i < grid.length; i++) {
            grid[i] = new ArrayList<>();
        }
    }

    public void clear() {
        for (List<Ball> cell : grid) {
            cell.clear();
        }
    }

    private int indexFromCoordinates(double x, double y) {
        double shiftedX = x - bounds.x0();
        double shiftedY = y - bounds.y0();

        int cellX = (int) (shiftedX / cellSize);
        int cellY = (int) (shiftedY / cellSize);

        if (cellX >= cols) cellX = cols - 1;
        if (cellX < 0) cellX = 0;
        
        if (cellY >= rows) cellY = rows - 1;
        if (cellY < 0) cellY = 0;

        return indexFromCells(cellX, cellY);
    }

    private int indexFromCells(int x, int y) {
        return (y * cols) + x;
    }

    public void insert(Ball b) {
        int index = indexFromCoordinates(b.getPos().x(), b.getPos().y());
        grid[index].add(b);
    }

    public List<Ball> getCell(int x, int y) {
        if (x < 0 || x >= cols || y < 0 || y >= rows) {
            return List.of(); 
        }
        return grid[indexFromCells(x, y)];
    }

    public int getCellX(Ball b) {
        double shiftedX = b.getPos().x() - bounds.x0();
        int cx = (int)(shiftedX / cellSize);
        return Math.max(0, Math.min(cx, cols - 1));
    }

    public int getCellY(Ball b) {
        double shiftedY = b.getPos().y() - bounds.y0();
        int cy = (int)(shiftedY / cellSize);
        return Math.max(0, Math.min(cy, rows - 1));
    }

    public int getSize() {
        return grid.length;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

}