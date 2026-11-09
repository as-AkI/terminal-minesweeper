package terminalMS;

import java.util.Scanner;

import terminalMS.Land.Event;


public class Main {
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Land land = new Land(10, 5, 7);

        Sweeper sweeper = new Sweeper(land);

        CommandManager cm = new CommandManager();

        Loop:
        while (true) {
            land.showData();

            switch (land.getEvent()) {
                case Event.CLEAR:
                    System.out.println("clear!");
                    break Loop;

                case Event.FAIL:
                    System.out.println("fail!");
                    break Loop;
            
                default:
                    break;
            }

            cm.inputCommand(land, sweeper);
        }
    }
}
