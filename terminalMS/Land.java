package terminalMS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import terminalMS.Tile.ANSI;
import terminalMS.Tile.Content;
import terminalMS.Tile.State;

public class Land extends HashMap<Pos, Tile> {
    static final long LONG_MAX = 9223372036854775807L;

    private final long SEED;

    private Random random;
    private List<Pos> keyList = new ArrayList<>();
    private int startMineCount;
    private boolean genCompleted = false;

    public Land() {
        SEED = Math.round(Math.random() * LONG_MAX);
        random = new Random(SEED);
    }

    public Land(int xSize, int ySize, int mineCount) {
        this();
        startMineCount = mineCount;

        genPlainBox(xSize, ySize);
    }

    public void genPlainBox(int xSize, int ySize) {
        for (int j = 0; j < ySize; j++) {
            for (int i = 0; i < xSize; i++) {
                Pos targetPos = new Pos(i, j);
                Tile targetTile = new Tile(Content.NONE, State.NONE);
                this.put(targetPos, targetTile);
                keyList.add(targetPos);
            }
        }
    }

    public void genDetail(Pos startPos) {
        genState();
        genContent(genContentData(startPos));
        genDetec();
        setGenCompleted(true);
    }

    public void genState() {
        List<Pos> allPosList = keyList();

        for (int i = 0; i < allPosList.size(); i++) {
            Pos targetPos = allPosList.get(i);
            Tile targetTile = getTile(targetPos);

            targetTile.setState(State.HIDE);
        }
    }

    public void genContent(List<Integer> contentData) {
        List<Pos> allPosList = keyList();

        for (int i = 0; i < allPosList.size(); i++) {
            Pos targetPos = allPosList.get(i);
            Tile targetTile = getTile(targetPos);

            targetTile.setState(State.HIDE);

            targetTile.setContent(switch (contentData.get(i)) {
                case 0 -> Content.SAFE;
                case 1 -> Content.MINE;
                default -> Content.NONE;
            });
        }

    }

    public List<Integer> genContentData(Pos startPos) {
        int mineCount = this.startMineCount;
        List<Integer> contentData;
        List<Pos> allPosList = keyList();
        List<Pos> aroundPosList = getIncludedfrom(startPos.getAround(1, true));

        int tileCount = keyList().size();

        int unknown = tileCount - aroundPosList.size();

        if (unknown < mineCount) {
            contentData = genComb(tileCount, mineCount);
        } else {
            contentData = genComb(unknown, mineCount);

            for (int i = 0; i < allPosList.size(); i++) {
                Pos targetPos = allPosList.get(i);

                if (aroundPosList.contains(targetPos)) {
                    contentData.add(i, 0);
                }
            }
        }

        return contentData;
    }

    public void genDetec() {
        List<Pos> allPosList = keyList();
        for (int i = 0; i < allPosList.size(); i++) {
            Pos originPos = allPosList.get(i);

            List<Pos> targetPosList = originPos.getAroundEight();

            for (int j = 0; j < targetPosList.size(); j++) {
                Pos targetPos = targetPosList.get(j);

                if (isIncluded(targetPos) && getTile(originPos).getContent() == Content.MINE) {
                    getTile(targetPos).setdetec(getTile(targetPos).getdetec() + 1);
                }
            }
        }
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

    public boolean isIncluded(Pos pos) {
        return keyList().contains(pos);
    }

    public boolean isGenCompleted() {
        return this.genCompleted;
    }

    public void setGenCompleted(boolean genCompleted) {
        this.genCompleted = genCompleted;
    }

    public long getSeed() {
        return this.SEED;
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

    public Tile getTile(Pos pos) {
        return this.get(pos);
    }

    public List<Pos> keyList() {
        return this.keyList;
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
                    color = ANSI.DIM_LIGHT_GRAY_BG;
                }

                sb.append(color.code()).append(indexX).append(indexY).append(ANSI.RESET.code());
                break;

            case State.NONE:
                if (bright) {
                    color = ANSI.LIGHT_GRAY_BG;
                } else {
                    color = ANSI.DIM_LIGHT_GRAY_BG;
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

            default: // FLAG, NONE
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

    public void appendScale(StringBuilder sb, int n) {
        sb.append(ANSI.WHITE_BG.code()).append(ANSI.BLACK.code()).append((char)(n+CommandManager.CHAR_OFFSET)).append(" ").append(ANSI.RESET.code());
    }

    public void showData() {
        System.out.println(getStatus());
        int[] box = getBox();

        StringBuilder sbScaleX = new StringBuilder();

        sbScaleX.append(ANSI.WHITE_BG.code()).append(ANSI.BLACK.code()).append("  ");
        for (int i = box[0]; i <= box[1]; i++) {
            appendScale(sbScaleX, i);
        }
        System.out.println(sbScaleX);

        for (int j = box[2]; j <= box[3]; j++) {
            StringBuilder sb = new StringBuilder();

            appendScale(sb, j);

            for (int i = box[0]; i <= box[1]; i++) {
                sb.append(getAppearance(new Pos(i, j)));

                if (i == box[1]) {
                    System.out.println(sb);
                }
            }
        }
    }

    public int[] getBox() {
        int[] ar = new int[4];

        List<Pos> allPosList = keyList();

        ar[0] = allPosList.get(0).x();
        ar[1] = ar[0];
        ar[2] = allPosList.get(0).y();
        ar[3] = ar[2];
        for (int i = 1; i < allPosList.size(); i++) {
            int X = allPosList.get(i).x();
            int Y = allPosList.get(i).y();

            if (X < ar[0])
                ar[0] = X;
            if (X > ar[1])
                ar[1] = X;
            if (Y < ar[2])
                ar[2] = Y;
            if (Y > ar[3])
                ar[3] = Y;
        }

        return ar;
    }

    public String getStatus() {
        return "M:" + count(Content.MINE) + " F:" + count(State.FLAG);
    }

    public int count(Predicate<Tile> condition, List<Pos> posList) {
        int count = 0;

        for (int i = 0; i < posList.size(); i++) {
            Pos targetPos = posList.get(i);

            if (isIncluded(targetPos)) {
                if (condition.test(getTile(targetPos))) {
                    count++;
                }
            }
        }

        return count;
    }

    public int count(Tile keyTile, List<Pos> posList) {
        State keyState = keyTile.getState();
        Content keyContent = keyTile.getContent();

        Predicate<Tile> condition;

        condition = (tile) -> (tile.getState() == keyState && tile.getContent() == keyContent);
        if (keyState != State.NONE && keyContent == Content.NONE) {
            condition = (tile) -> (tile.getState() == keyState);
        }
        if (keyState == State.NONE && keyContent != Content.NONE) {
            condition = (tile) -> (tile.getContent() == keyContent);
        }

        return count(condition, posList);
    }

    public int count(State state, List<Pos> posList) {
        return count((tile) -> (tile.getState() == state), posList);
    }

    public int count(State state, Pos originPos, int range, boolean includeOrigin) {
        return count(state, originPos.getAround(range, includeOrigin));
    }

    public int count(State state) {
        return count(state, keyList());
    }

    public int count(Content content, List<Pos> posList) {
        return count((tile) -> (tile.getContent() == content), posList);
    }

    public int count(Content content) {
        return count(content, keyList());
    }

    public enum Event {
        NONE,
        CLEAR,
        FAIL;
    }

    public Event getEvent() {
        int openSafe = 0;

        List<Pos> allPosList = keyList();
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

        if (isGenCompleted() && openSafe == count(Content.SAFE)) {
            return Event.CLEAR;
        }

        return Event.NONE;
    }
}
