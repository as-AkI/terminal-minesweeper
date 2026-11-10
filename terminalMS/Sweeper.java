package terminalMS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import terminalMS.Tile.Content;
import terminalMS.Tile.State;

public class Sweeper {
    
    final Land land;

    Sweeper (Land land) {
        this.land = land;
    }

    public boolean open(Pos pos) {
        if (!land.isGenCompleted()) {
            land.genDetail(pos);
        }

        boolean aroundSafe;
        if (!land.isIncluded(pos)) {
            System.err.println("Land open: not included!" + "(" + pos.x() + "," + pos.y() + ")");
        }

        Tile targetTile = land.getTile(pos);

        if (targetTile.getState() == State.HIDE) {
            targetTile.setState(State.OPEN);
            aroundSafe = (targetTile.getdetec() == 0);
        } else {
            // consider flag
            aroundSafe = (targetTile.getdetec() - land.count(State.FLAG, pos, 1, true) == 0);
        }

        if (targetTile.getContent() == Content.SAFE && aroundSafe) {
            List<Pos> aroundPosList = pos.getAroundEight();

            Pos targetPos;

            for (int i = 0; i < aroundPosList.size(); i++) {
                targetPos = aroundPosList.get(i);

                if (land.isIncluded(targetPos) && land.getTile(targetPos).getState() == State.HIDE) {
                    open(targetPos);
                }
            }

            return true;
        }

        return false;
    }

    public boolean flag(Pos pos) {
        if (land.isIncluded(pos)) {
            Tile targetTile = land.getTile(pos);
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
        if (land.isIncluded(pos)) {
            Tile targetTile = land.getTile(pos);
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
        System.out.println("seed:" + land.getSeed());
        return true;
    }

    public void deduce(Pos pos, int range) {
        List<Pos> aroundList = pos.getAround(1, true);

        Map<Pos, Tile> deduData = new Land();

        List<Pos> unknownList = new ArrayList<>();

        for (int i = 0; i < aroundList.size(); i++) {
            Pos targetPos = aroundList.get(i);
            if (land.isIncluded(targetPos)) {
                switch (land.getTile(targetPos).getState()) {
                    case State.OPEN:
                        deduData.put(targetPos, new Tile(Content.SAFE, State.OPEN));
                        break;

                    case State.FLAG:
                        deduData.put(targetPos, new Tile(Content.MINE, State.FLAG));
                        break;

                    default: // HIDE
                        deduData.put(targetPos, new Tile(Content.NONE, State.HIDE));
                        unknownList.add(targetPos);
                        break;
                }
            } else { // out of land
                deduData.put(targetPos, new Tile(Content.SAFE, State.OPEN));
            }
        }

        for (int i = 0; i < unknownList.size(); i++) {
        }
    }
}
