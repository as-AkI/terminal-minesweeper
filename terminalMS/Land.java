package terminalMS;

import java.util.ArrayList;
import java.util.Collections;
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
    private boolean opened = false;

    public Land(int xSize, int ySize, int mineCount) {
        this.M_COUNT = mineCount;

        this.X_SIZE = xSize;
        this.Y_SIZE = ySize;

        SEED = Math.round(Math.random() * LONG_MAX);
        random = new Random(SEED);

        // System.out.println(SEED);

        this.data = genPlainData();
    }

    public Map<Pos, Tile> genPlainData() {
        Map<Pos, Tile> data = new HashMap<>();

        for (int j = 0; j < Y_SIZE; j++) {
            for (int i = 0; i < X_SIZE; i++) {
                Pos targetPos = new Pos(i, j);
                Tile targetTile = new Tile(Content.NONE, State.NONE);
                data.put(targetPos, targetTile);
            }
        }

        return data;
    }

    public void genContent() {
        List<Integer> landSeed = genComb(X_SIZE * Y_SIZE, M_COUNT);

        for (int j = 0; j < Y_SIZE; j++) {
            for (int i = 0; i < X_SIZE; i++) {
                Tile targetTile = getTile(new Pos(i, j));

                targetTile.setState(State.HIDE);

                if (landSeed.get(i + j * Y_SIZE) == 1) {
                    targetTile.setContent(Content.MINE);
                } else {
                    targetTile.setContent(Content.SAFE);
                }
            }
        }
    }

    public void genContent(Pos startPos) {
        List<Integer> landSeed;
        List<Pos> allPosList = getAllPos();
        List<Pos> aroundPosList = getIncludedfrom(startPos.getAround(1, true));

        int unknown = X_SIZE * Y_SIZE - aroundPosList.size();

        if (unknown < M_COUNT) {
            landSeed = genComb(X_SIZE * Y_SIZE, M_COUNT);
        } else {
            landSeed = genComb(unknown, M_COUNT);

            for (int i = 0; i < allPosList.size(); i++) {
                Pos targetPos = allPosList.get(i);

                if (aroundPosList.contains(targetPos)) {
                    landSeed.add(i, 0);
                }
            }
        }

        for (int i = 0; i < allPosList.size(); i++) {
            Pos targetPos = allPosList.get(i);
            Tile targetTile = getTile(targetPos);

            targetTile.setState(State.HIDE);

            if (landSeed.get(i) == 1) {
                targetTile.setContent(Content.MINE);
            } else {
                targetTile.setContent(Content.SAFE);
            }
        }

    }

    public void genDetec() {
        for (int j = 0; j < Y_SIZE; j++) {
            for (int i = 0; i < X_SIZE; i++) {
                Pos originPos = new Pos(i, j);

                List<Pos> targetPosList = originPos.getAroundEight();

                Pos targetPos;

                for (int k = 0; k < targetPosList.size(); k++) {
                    targetPos = targetPosList.get(k);

                    if (isIncluded(targetPos) && getTile(originPos).getContent() == Content.MINE) {
                        getTile(targetPos).setdetec(getTile(targetPos).getdetec() + 1);
                    }
                }
            }
        }
    }

    public boolean isIncluded(Pos pos) {
        int x = pos.x();
        int y = pos.y();

        return (0 <= x && x < X_SIZE && 0 <= y && y < Y_SIZE);
    }

    public List<Pos> getIncludedfrom(List<Pos> rawList) {
        List<Pos> list = new ArrayList<>();
        for (int i = 0; i < rawList.size(); i++) {
            Pos targetPos = rawList.get(i);
            if (isIncluded(targetPos)) {
                list.add(targetPos);
            }
        }

        return list;
    }

    public List<Integer> genComb(int n, int r) {
        if (r > n) {
            throw new IllegalArgumentException("r:" + r + " is larger than n:" + n + "!");
        }

        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < r; i++)
            list.add(1);
        for (int i = 0; i < n - r; i++)
            list.add(0);

        for (int i = 0; i < list.size(); i++) {
            Collections.swap(list, i, random.nextInt(0, n));
        }

        return list;
    }

    public Tile getTile(Pos pos) {
        return data.get(pos);
    }

    // public Tile getTile(int x, int y) {
    // return getTile(new Pos(x, y));
    // }

    public List<Pos> getAllPos() {
        ArrayList<Pos> list = new ArrayList<>();
        for (int j = 0; j < Y_SIZE; j++) {
            for (int i = 0; i < X_SIZE; i++) {
                Pos targetPos = new Pos(i, j);
                if (isIncluded(targetPos)) {
                    list.add(targetPos);
                }
            }
        }
        return list;
    }

    public String getAppearance(Pos pos) {
        int x = pos.x();
        int y = pos.y();
        boolean bright = ((x + y) % 2 == 0);
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

            case State.NONE:
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

            default: // FLAG
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

    public int countState(State state, Pos originPos, int range, boolean includeOrigin) {
        int count = 0;

        List<Pos> aroundPosList = originPos.getAround(range, includeOrigin);

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

    public boolean open(Pos pos) {
        if (!this.opened) {
            genContent(pos);
            genDetec();
            this.opened = true;
        }

        boolean aroundSafe;
        if (!isIncluded(pos)) {
            System.err.println("Land open: not included!" + "(" + pos.x() + "," + pos.y() + ")");
        }

        Tile targetTile = getTile(pos);

        if (targetTile.getState() == State.HIDE) {
            targetTile.setState(State.OPEN);
            aroundSafe = (targetTile.getdetec() == 0);
        } else {
            // consider flag
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

    public boolean flag(Pos pos) {
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

    public boolean info(Pos pos) {
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

    public boolean info() {
        System.out.println("seed:" + this.SEED);
        return true;
    }

    public String getStatus() {
        return "M:" + M_COUNT + " F:" + countState(State.FLAG);
    }

    public enum Event {
        NONE,
        CLEAR,
        FAIL;
    }

    public Event getEvent() {
        int openSafe = 0;

        List<Pos> allPosList = getAllPos();
        for (int i = 0; i < allPosList.size(); i++) {
            Tile targetTile = getTile(allPosList.get(i));
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

        if (openSafe == X_SIZE * Y_SIZE - M_COUNT) {
            return Event.CLEAR;
        }

        return Event.NONE;
    }

    public long getSeed() {
        return this.SEED;
    }

    public void deduce(Pos pos, int range) {
        List<Pos> aroundList = pos.getAround(1, true);

        Map<Pos, Content> deduData = new HashMap<>();

        int unknown = 0;

        for (int i = 0; i < aroundList.size(); i++) {
            Pos targetPos = aroundList.get(i);
            if (!isIncluded(targetPos)) {
                continue;
            }

            switch (getTile(targetPos).getState()) {
                case State.OPEN:
                    deduData.put(targetPos, Content.SAFE);
                    break;

                case State.FLAG:
                    deduData.put(targetPos, Content.MINE);
                    break;

                default: // HIDE
                    deduData.put(targetPos, Content.NONE);
                    unknown++;
                    break;
            }
        }

        for (int i = 0; i < unknown; i++) {

        }
    }
}
