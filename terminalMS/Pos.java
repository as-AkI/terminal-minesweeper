package terminalMS;

import java.util.ArrayList;
import java.util.List;

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

    public List<Pos> getAroundEight() {
        List<Pos> list = new ArrayList<>();
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

    public List<Pos> getAround(int range, boolean includeOrigin) {
        List<Pos> list = new ArrayList<>();
        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                if(!includeOrigin && i == 0 && j == 0) {
                    continue;
                }

                list.add(new Pos(this, j, i));
            }
        }
        return list;
    }
}
