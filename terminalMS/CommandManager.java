package terminalMS;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private Command fixedCommand;

    public CommandManager() {
        fixedCommand = Command.NONE;
    }

    public Command getFixedCommand() {
        return fixedCommand;
    }

    public void setFixedCommand(Command fixedCommand) {
        this.fixedCommand = fixedCommand;
    }

    static final int CHAR_OFFSET = 48;

    public enum Command {
        NONE,
        FIX,
        OPEN("o"),
        FLAG("f"),
        INFO,
        END;

        final String ABBR;

        Command(String abbr) {
            this.ABBR = abbr;
        }
        Command() {
            this.ABBR = "";
        }

        public String abbr() {
            return this.ABBR;
        }
    }

    public Command convertCommand(String raw) {
        Command[] list = Command.values();
        for (int i = 0; i < list.length; i++) {
            if (list[i].toString().equalsIgnoreCase(raw) 
            || list[i].abbr().toString().equalsIgnoreCase(raw)) {
                return list[i];
            }
        }
        return Command.NONE;
    }

    /** @return if succeeded */
    public Boolean inputCommand(Land land) {
        try {
            if (this.getFixedCommand() != Command.NONE) {
                System.out.print(this.getFixedCommand() + " ");
            }

            String[] inputList = Main.scanner.nextLine().split(" +");

            Command command = convertCommand(inputList[0]);
            List<String> paramList = new ArrayList<String>();

            if (command != Command.NONE) {
                if (inputList.length > 1) {
                    for (int i = 1; i < inputList.length; i++) {
                        paramList.add(inputList[i]);
                    }
                }
            } else {
                if (this.getFixedCommand() != Command.NONE) {
                    command = this.getFixedCommand();
                    for (int i = 0; i < inputList.length; i++) {
                        paramList.add(inputList[i]);
                    }
                }
            }

            switch (command) {
                case Command.FIX:
                    return fix(paramList, land);

                case Command.OPEN:
                    return open(paramList, land);

                case Command.FLAG:
                    return flag(paramList, land);

                case Command.INFO:
                    return info(paramList, land);

                case Command.END:
                    return end(paramList, land);

                default:
                    System.out.println("unknown command!");
                    return false;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return false;
    }

    public boolean fix(List<String> paramList, Land land) {
        int paramCount = paramList.size();

        switch (paramCount) {
            case 0:
                this.setFixedCommand(Command.NONE);
                return true;

            case 1:
                this.setFixedCommand(convertCommand(paramList.get(0)));
                return true;

            default:
                System.out.println("incorrect fix!");
                return false;
        }

    }

    public Boolean open(List<String> paramList, Land land) {
        int paramCount = paramList.size();

        switch (paramCount) {
            case 0:
                this.setFixedCommand(Command.OPEN);
                return true;

            case 1:
                int x = (int) (paramList.get(0).charAt(0) - CHAR_OFFSET);
                int y = (int) (paramList.get(0).charAt(1) - CHAR_OFFSET);
                return land.open(new Pos(x, y));

            default:
                System.out.println("incorrect open!");
                return false;
        }
    }

    public Boolean flag(List<String> paramList, Land land) {
        int paramCount = paramList.size();

        switch (paramCount) {
            case 0:
                this.setFixedCommand(Command.FLAG);
                return true;

            case 1:
                int x = (int) (paramList.get(0).charAt(0) - CHAR_OFFSET);
                int y = (int) (paramList.get(0).charAt(1) - CHAR_OFFSET);
                return land.flag(new Pos(x, y));

            default:
                System.out.println("incorrect flag!");
                return false;
        }
    }

    public Boolean info(List<String> paramList, Land land) {
        int paramCount = paramList.size();

        if (paramCount == 1) {
            int x = (int) (paramList.get(0).charAt(0) - CHAR_OFFSET);
            int y = (int) (paramList.get(0).charAt(1) - CHAR_OFFSET);
            return land.info(new Pos(x, y));
        }
        System.out.println("incorrect flag!");
        return false;
    }

    public Boolean end(List<String> paramList, Land land) {
        System.exit(0);
        return false;
    }
}
