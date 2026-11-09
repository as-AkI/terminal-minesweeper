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
    public boolean inputCommand(Land land, Sweeper sweeper) {
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
                    return fix(paramList);

                case Command.OPEN:
                    return open(paramList, land, sweeper);

                case Command.FLAG:
                    return flag(paramList, land, sweeper);

                case Command.INFO:
                    return info(paramList, land, sweeper);

                case Command.END:
                    return end(paramList);

                default:
                    System.out.println("unknown command!");
                    return false;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return false;
    }

    public boolean fix(List<String> paramList) {
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

    public boolean open(List<String> paramList, Land land, Sweeper sweeper) {
        int paramCount = paramList.size();

        switch (paramCount) {
            case 0:
                this.setFixedCommand(Command.OPEN);
                return true;

            case 1:
                int x = (int) (paramList.get(0).charAt(0) - CHAR_OFFSET);
                int y = (int) (paramList.get(0).charAt(1) - CHAR_OFFSET);
                return sweeper.open(new Pos(x, y));

            default:
                System.out.println("incorrect open!");
                return false;
        }
    }

    public boolean flag(List<String> paramList, Land land, Sweeper sweeper) {
        int paramCount = paramList.size();

        switch (paramCount) {
            case 0:
                this.setFixedCommand(Command.FLAG);
                return true;

            case 1:
                int x = (int) (paramList.get(0).charAt(0) - CHAR_OFFSET);
                int y = (int) (paramList.get(0).charAt(1) - CHAR_OFFSET);
                return sweeper.flag(new Pos(x, y));

            default:
                System.out.println("incorrect flag!");
                return false;
        }
    }

    public boolean info(List<String> paramList, Land land, Sweeper sweeper) {
        int paramCount = paramList.size();

        switch (paramCount) {
            case 0:
                return sweeper.info();

            case 1:
            int x = (int) (paramList.get(0).charAt(0) - CHAR_OFFSET);
            int y = (int) (paramList.get(0).charAt(1) - CHAR_OFFSET);
            return sweeper.info(new Pos(x, y));

            default:
                System.out.println("incorrect info!");
                return false;
        }
    }

    public boolean end(List<String> paramList) {
        System.exit(0);
        return false;
    }
}
