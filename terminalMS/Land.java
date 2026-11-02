package terminalMS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import terminalMS.Tile.ANSI;
import terminalMS.Tile.Content;
import terminalMS.Tile.State;

public class Land {
    final int X_SIZE;
    final int Y_SIZE;
    final int M_COUNT;

    final long SEED;
    final Random random;

    static final long LONG_MAX = 9223372036854775807L;

    private Map<Pos, Tile> data;

    public Land(int xSize, int ySize, int mineCount) {
        this.M_COUNT = mineCount;

        this.X_SIZE = xSize;
        this.Y_SIZE = ySize;

        SEED = Math.round(Math.random() * LONG_MAX);
        random = new Random(SEED);

        // System.out.println(SEED);

        genData();
    }

    public void genData() {
        this.data = new HashMap<>();

        ArrayList<Integer> landSeed = genComb(X_SIZE * Y_SIZE, M_COUNT);

        for (int j = 0; j < Y_SIZE; j++) {
            for (int i = 0; i < X_SIZE; i++) {
                Pos targetPos = new Pos(i, j);
                Tile targetTile = new Tile(Content.SAFE, State.HIDE);
                data.put(targetPos, targetTile);

                if (landSeed.get(i + j * Y_SIZE) == 1) {
                    targetTile.setContent(Content.MINE);
                }
            }
        }

        for (int j1 = 0; j1 < Y_SIZE; j1++) {
            for (int i1 = 0; i1 < X_SIZE; i1++) {
                Pos originPos = new Pos(i1, j1);

                List<Pos> targetPosList = originPos.getAroundEight();

                Pos targetPos;

                for (int i = 0; i < targetPosList.size(); i++) {
                    targetPos = targetPosList.get(i);

                    if (isIncluded(targetPos) && getTile(originPos).getContent() == Content.MINE) {
                        getTile(targetPos).setdetec(getTile(targetPos).getdetec() + 1);
                    }
                }
            }
        }
    }

    public Boolean isIncluded(Pos pos) {
        int x = pos.x();
        int y = pos.y();

        return (0 <= x && x < X_SIZE && 0 <= y && y < Y_SIZE);
    }

    public ArrayList<Integer> genComb(int n, int r) {
        if (r > n) {
            throw new IllegalArgumentException("r:" + r + " is larger than n:" + n + "!");
        }

        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < r; i++)
            list.add(1);
        for (int i = 0; i < n - r; i++)
            list.add(0);

        for (int i = 0; i < list.size(); i++) {
            int x = random.nextInt(0, n);
            int tmp = list.get(x);
            list.set(x, list.get(i));
            list.set(i, tmp);
        }

        return list;
    }

    public Tile getTile(Pos pos) {
        return data.get(pos);
    }

    // public Tile getTile(int x, int y) {
    // return getTile(new Pos(x, y));
    // }

    public String getAppearance(Pos pos) {
        int x = pos.x();
        int y = pos.y();
        Boolean bright = ((x + y) % 2 == 0);
        Tile targetTile = getTile(pos);
        State targetState = targetTile.getState();
        StringBuilder sb = new StringBuilder();
        ANSI color;

        char indexX = (char) (x + CommandManager.CHAR_OFFSET);
        char indexY = (char) (y + CommandManager.CHAR_OFFSET);

        switch (targetState) {
            case State.HIDE:
                if (bright) {
                    color = ANSI.LIGHT_GRAY_BG;
                } else {
                    color = ANSI.WHITE_BG;
                }

                sb.append(color.code()).append(indexX).append(indexY).append(ANSI.RESET.code());
                break;

            case State.OPEN:
                if (targetTile.getContent() == Content.SAFE) {
                    if (bright) {
                        color = ANSI.BLACK_BG;
                    } else {
                        color = ANSI.DEEP_DARK_GRAY_BG;
                    }

                    sb.append(color.code()).append(targetTile.getDetecIcon());
                } else {
                    sb.append(targetTile.getContent().getIcon());
                }
                break;

            default:// FLAG
                if (bright) {
                    color = ANSI.LIGHT_GRAY_BG;
                } else {
                    color = ANSI.WHITE_BG;
                }

                sb.append(color.code()).append(targetState.getIcon()).append(ANSI.RESET.code());
                break;
        }

        return sb.toString();
    }

    public void showData() {
        System.out.println(getStatus());
        for (int j = 0; j < Y_SIZE; j++) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < X_SIZE; i++) {

                sb.append(getAppearance(new Pos(i, j)));

                if (i == X_SIZE - 1) {
                    System.out.println(sb);
                }
            }
        }
    }

    public Boolean open(Pos pos) {
        Boolean aroundSafe;
        if (!isIncluded(pos)) {
            System.err.println("Land open: not included!" + "(" + pos.x() + "," + pos.y() + ")");
        }

        Tile targetTile = getTile(pos);

        if (targetTile.getState() == State.HIDE) {
            targetTile.setState(State.OPEN);
            aroundSafe = (targetTile.getdetec() == 0);
        } else {
            //concider flag
            aroundSafe = (targetTile.getdetec() - countState(State.FLAG, pos, 1, true) == 0);
        }

        if (targetTile.getContent() == Content.SAFE && aroundSafe) {
            List<Pos> aroundPosList = pos.getAroundEight();

            Pos targetPos;

            for (int i = 0; i < aroundPosList.size(); i++) {
                targetPos = aroundPosList.get(i);

                if (isIncluded(targetPos) && getTile(targetPos).getState() == State.HIDE) {
                    open(targetPos);
                }
            }

            return true;
        }

        return false;
    }

    public int countState(State state, Pos originPos, int distance, boolean includeOrigin) {
        int count = 0;

        List<Pos> aroundPosList = originPos.getAround(distance, includeOrigin);

        Pos targetPos;

        for (int i = 0; i < aroundPosList.size(); i++) {
            targetPos = aroundPosList.get(i);

            if (isIncluded(targetPos) && getTile(targetPos).getState() == state) {
                count++;
            }
        }

        return count;
    }

    public int countState(State state) {
        int count = 0;

        for (int j = 0; j < Y_SIZE; j++) {
            for (int i = 0; i < X_SIZE; i++) {
                Pos targetPos = new Pos(i, j);

                if (getTile(targetPos).getState() == state) {
                    count++;
                }
            }
        }

        return count;
    }

    public Boolean flag(Pos pos) {
        if (isIncluded(pos)) {
            Tile targetTile = getTile(pos);
            switch (targetTile.getState()) {
                case State.HIDE:
                    targetTile.setState(State.FLAG);
                    return true;

                case State.FLAG:
                    targetTile.setState(State.HIDE);
                    return true;

                default:
                    return false;
            }
        }
        return false;
    }

    public Boolean info(Pos pos) {
        if (isIncluded(pos)) {
            Tile targetTile = getTile(pos);
            System.out.println(
                "pos:" + pos +
                ", state:" + targetTile.getState() +
                ", content:" + targetTile.getContent() +
                ", detec:" + targetTile.getdetec());
            return true;
        }
        return false;
    }

    public String getStatus() {
        return "M:"+M_COUNT+" F:"+countState(State.FLAG);
    }

    public enum Event {
        NONE,
        CLEAR,
        FAIL;
    }

    public Event getEvent() {
        int openSafe = 0;

        for (int j = 0; j < Y_SIZE; j++) {
            for (int i = 0; i < X_SIZE; i++) {
                Tile targetTile = getTile(new Pos(i, j));
                if (targetTile.getState() == State.OPEN) {
                    switch (targetTile.getContent()) {
                        case Content.MINE:
                            return Event.FAIL;
                        case Content.SAFE:
                            openSafe++;
                            break;
                        default:
                            break;
                    }
                }

            }
        }

        if (openSafe == X_SIZE * Y_SIZE - M_COUNT) {
            return Event.CLEAR;
        }

        return Event.NONE;
    }
}
