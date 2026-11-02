package terminalMS;

import java.util.ArrayList;

public class Pos extends ArrayList<Integer> {
    Pos(int x, int y) {
        this.add(x);
        this.add(y);
    }

    Pos(Pos originPos ,int dx, int dy) {
        this.add(originPos.x()+dx);
        this.add(originPos.y()+dy);
    }

    public int x() {
        return this.get(0);
    }

    public int y() {
        return this.get(1);
    }

    public Pos shift(int x, int y) {
        this.set(0, this.x()+x);
        this.set(1, this.y()+y);
        return this;
    }

    public ArrayList<Pos> getAroundEight() {
        ArrayList<Pos> list = new ArrayList<>();
        list.add(new Pos(this, -1, -1));
        list.add(new Pos(this, 0, -1));
        list.add(new Pos(this, 1, -1));
        list.add(new Pos(this, 1, 0));
        list.add(new Pos(this, 1, 1));
        list.add(new Pos(this, 0, 1));
        list.add(new Pos(this, -1, 1));
        list.add(new Pos(this, -1, 0));
        return list;
    }

    public ArrayList<Pos> getAround(int distance, boolean includeOrigin) {
        ArrayList<Pos> list = new ArrayList<>();
        for (int i = -distance; i <= distance; i++) {
            for (int j = -distance; j <= distance; j++) {
                if(!includeOrigin && i == 0 && j == 0) {
                    continue;
                }

                list.add(new Pos(this, j, i));
            }
        }
        return list;
    }

    public ArrayList<Pos> getAround(int distance) {
        return getAround(distance, false);
    }

    public ArrayList<Pos> getSquare(int xSize, int ySize) {
        ArrayList<Pos> list = new ArrayList<>();
        for (int j = -ySize; j <= ySize; j++) {
            for (int i = -xSize; i <= xSize; i++) {
                list.add(new Pos(this, i, j));
            }
        }
        return list;
    }
}
