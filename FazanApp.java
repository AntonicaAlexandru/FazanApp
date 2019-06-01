package com.fazan;

import com.fazan.model.FazanGame;
import com.fazan.model.Player;
import com.fazan.utils.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FazanApp {

    private boolean appOn = true;
    private List<FazanGame> gamesList;


    public FazanApp() {
        gamesList = new ArrayList<>();
    }

    public void startApp() {

        while (this.appOn) {

            switch (this.getUserCommandId()) {

                case Constants.SHOW_RESULTS:
                    System.out.println("\n------PREVIOUS GAMES--------\n");

                    this.listGameResult();
                    System.out.println("\n--------------------\n");

                    break;
                case Constants.NEW_GAME:
                    System.out.println("\n--------NEW GAME--------\n");

                    FazanGame fazanGame = new FazanGame();
                    fazanGame.startGame();
                    this.gamesList.add(fazanGame);
                    System.out.println("\n--------------------\n");
                    break;
                case Constants.END_GAME:
                    this.saveListOfGames();
                    this.appOn = false;
                case Constants.TAKE_USER_INPUT:
                    break;
                default:
                    System.out.println("No such option, please select again!\n-------------\n");
                    break;
            }
        }
    }

    private void saveListOfGames() {

        /*
         * Save to file the stats of games
         * */
        URI uri;
        File file = null;
        FileWriter fileWriter = null;

        // Explicitly get hold of working directory
        String workingDir = System.getProperty("user.dir");

        /**
         * TODO: UNCOMMENT THIS IF YOU'RE WORKING ON WIN
         * */
        uri = Paths.get(workingDir+File.separator+"src\\resources\\games.txt").toUri();


        /**
         * TODO: UNCOMMENT THIS IF YOU'RE WORKING ON UNIX
         * */
        uri = Paths.get(workingDir+File.separator+"src/resources/games.txt").toUri();

        try {

            file = new File(uri);
            fileWriter = new FileWriter(file,true);
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (FazanGame fazanGame : this.gamesList) {

            try {

                HashMap<String, ArrayList<String>> gameStats = new HashMap<>();
                ArrayList<String> playersListStats = new ArrayList<>();
                for (Player player : fazanGame.getPlayerList()) {
                    playersListStats.add(player.toString());
                }

                gameStats.put(fazanGame.getGameName(), playersListStats);

                fileWriter.append(gameStats.toString() + "\n");
                fileWriter.append("------------------\n");
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileWriter.close();
        } catch (IOException e) { }
    }

    private int getUserCommandId() {

        // auto close scanner
        Integer input = Constants.TAKE_USER_INPUT;
        Scanner scanner = new Scanner(System.in);
        try {

            System.out.print("What do you want to do : \n" +
                    "\t1 - Show Previous Results ( if any )\n" +
                    "\t2 - Start New Game\n" +
                    "\t3 - Close App\n--------------\n");

            input = scanner.nextInt();  // Read user input
            System.out.println("User Input : " + input);
            System.out.println("\n--------------------");

            //If you lose it while inside the loop
            //the System.in will be also closed so
            // you won't be able to read the input anymore
            if (input == Constants.END_GAME) {
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("You've input a wrong parameter!");
            return Constants.TAKE_USER_INPUT;
        }

        return input;
    }

    private void listGameResult() {
        System.out.println("List game result");

        try {
            Path path = Paths.get(getClass().getClassLoader()
                    .getResource("resources/games.txt").toURI());

            Files.lines(path).forEach(s -> System.out.println(s));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("No games have been played yet!");
        }
    }


    public static void main(String[] args) {

        FazanApp fazanApp = new FazanApp();
        fazanApp.startApp();

    }
}
