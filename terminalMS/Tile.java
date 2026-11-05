package terminalMS;

public class Tile {
    private Content content;
    private State state;
    private int detec;

    public enum Content {
        NONE(),
        SAFE(),
        MINE(ANSI.WHITE_B.code()+" M"+ANSI.RESET.code());

        private final String icon;

        Content(String icon) {
            this.icon = icon;
        }
        Content() {
            this.icon = "  ";
        }

        public String getIcon() {
            return icon;
        }
    }

    public enum State {
        NONE(),
        OPEN(),
        HIDE(),
        FLAG(ANSI.BLACK_B.code()+" F"+ANSI.RESET.code());

        private final String icon;

        State(String icon) {
            this.icon = icon;
        }
        State() {
            this.icon = "  ";
        }

        public String getIcon() {
            return icon;
        }
    }

    // https://gist.github.com/JBlond/2fea43a3049b38287e5e9cefc87b2124
    public enum ANSI {
        RESET ("00"),

        BLACK ("30"),
        RED   ("31"),
        GREEN ("32"),
        YELLOW("33"),
        BLUE  ("34"),
        PURPLE("35"),
        CYAN  ("36"),
        WHITE ("37"),
        
        BLACK_HI ("90"),
        RED_HI   ("91"),
        GREEN_HI ("92"),
        YELLOW_HI("93"),
        BLUE_HI  ("94"),
        PURPLE_HI("95"),
        CYAN_HI  ("96"),
        WHITE_HI ("97"),

        BLACK_BG ("40"),
        RED_BG   ("41"),
        GREEN_BG ("42"),
        YELLOW_BG("43"),
        BLUE_BG  ("44"),
        PURPLE_BG("45"),
        CYAN_BG  ("46"),
        WHITE_BG ("47"),

        BLACK_HI_BG ("100"),
        RED_HI_BG   ("101"),
        GREEN_HI_BG ("102"),
        YELLOW_HI_BG("103"),
        BLUE_HI_BG  ("104"),
        PURPLE_HI_BG("105"),
        CYAN_HI_BG  ("106"),
        WHITE_HI_BG ("107"),

        BLACK_B ("1;30"),
        RED_B   ("1;31"),
        GREEN_B ("1;32"),
        YELLOW_B("1;33"),
        BLUE_B  ("1;34"),
        PURPLE_B("1;35"),
        CYAN_B  ("1;36"),
        WHITE_B ("1;37"),

        BLACK_HI_B ("1;90"),
        RED_HI_B   ("1;91"),
        GREEN_HI_B ("1;92"),
        YELLOW_HI_B("1;93"),
        BLUE_HI_B  ("1;94"),
        PURPLE_HI_B("1;95"),
        CYAN_HI_B  ("1;96"),
        WHITE_HI_B ("1;97"),

        DEEP_DARK_GRAY   ("38;5;235"),
        DEEP_DARK_GRAY_BG("48;5;235"),
        LIGHT_GRAY   ("38;5;247"),
        LIGHT_GRAY_BG("48;5;247");

        private final String code;

        ANSI(String index) {
            this.code = "\u001b[" + index + "m";
        }

        public String code() {
            return code;
        }
    }

    public String getDetecIcon(int n) {
        if(n == 0) {
            return "  "+ANSI.RESET.code();
        }
        ANSI color = switch (n) {
            case 1 -> ANSI.CYAN_HI_B;
            case 2 -> ANSI.GREEN_HI_B;
            case 3 -> ANSI.RED_HI_B;
            case 4 -> ANSI.PURPLE_HI_B;
            case 5 -> ANSI.YELLOW_HI_B;
            case 6 -> ANSI.BLUE_HI_B;
            case 7 -> ANSI.BLACK_HI_B;
            case 8 -> ANSI.WHITE_HI_B;
            default -> {
                System.err.println("Tile: not in range!" + "(" + n + "∈[0,8])");
                yield ANSI.WHITE;
            }
        };

        return color.code()+" "+n+ANSI.RESET.code();
    }

    public String getDetecIcon() {
        return getDetecIcon(this.getdetec());
    }

    public Tile(Content content, State state) {
        this.content = content;
        this.state = state;
        this.detec = 0;
    }

    public Content getContent() {
        return content;
    }

    public Tile setContent(Content content) {
        this.content = content;
        return this;
    }

    public State getState() {
        return state;
    }

    public Tile setState(State state) {
        this.state = state;
        return this;
    }

    public int getdetec() {
        return detec;
    }

    public Tile setdetec(int detec) {
        this.detec = detec;
        return this;
    }
}
