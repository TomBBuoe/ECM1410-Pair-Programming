package cityrescue;
/**
 * Contains attributes for the city grid and blockages
 */
public class CityMap {
    private int gridHeight;
    private int gridWidth;
    private boolean[][] blocked;

    public CityMap(int width, int height) {
        this.gridWidth = width;
        this.gridHeight = height;
        this.blocked = new boolean[width][height];
    }

    public int getWidth() {
        return gridWidth;
    }

    public int getHeight() {
        return gridHeight;
    }

    public boolean isBlocked(int x, int y) {
        return blocked[x][y];
    }

    public void blockCell(int x, int y) {
        blocked[x][y] = true;
    }

    public void unblockCell(int x, int y) {
        blocked[x][y] = false;
    }

    public boolean isValidLocation(int x, int y) {
        if (0 <= x && x <= gridWidth && 0 <= y && y <= gridHeight) {
            return true;
        }
        return false;
    }

    public boolean isLegalMove(int x, int y) {
        if (isValidLocation(x, y) && !isBlocked(x, y)) {
            return true;
        }
        return false;
    }

}
