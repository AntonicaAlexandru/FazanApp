package com.fazan.model;

import com.fazan.service.WordService;
import com.fazan.utils.Constants;
import com.oracle.tools.packager.Log;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FazanGame {

    private boolean gameOn = true;
    private int currentPlayerId = -1;
    private List<Player> playerList;
    private String previousWord = "";
    private boolean roundStart = true;
    private WordService wordService = new WordService();
    private String gameName;

    public void startGame() {
        this.log("Start new game!");

        playerList = this.initializeGame();

        Player currentPlayer = this.getNextActivePlayer();
        log("^^^^^^^^^^^^^^^^^^^^^");
        log("It is " + currentPlayer.getName() + "'s turn!");


        while (gameOn) {
            this.continueGameWithPlayer(currentPlayer);
            log("^^^^^^^^^^^^^^^^^^^^^\n");

            //select next player turn
            currentPlayer = this.getNextActivePlayer();
            log("It is " + currentPlayer.getName() + "'s turn!");
        }

        this.printGameStatistics();
    }

    private void printGameStatistics() {

        List<Player> eligiblePlayersList = this.playerList;
        Collections.sort(eligiblePlayersList, Comparator.comparing(Player::getGamePlace));

        Player winnerPlayer = eligiblePlayersList.get(0);

        if (winnerPlayer != null) {
            winnerPlayer.setGamePlace();
        }

        log("------GAME OVER-------");
        log("------STANDINGS-------");
        for (Player player : eligiblePlayersList) {
            log(player.toString());
        }
    }

    private void continueGameWithPlayer(Player currentPlayer) {

        String playerWord = this.getInputWordFromPlayer(currentPlayer);
        boolean isValidWord = wordService.validateWord(previousWord, playerWord, roundStart);

        while (!isValidWord && currentPlayer.getLifes() > 0 && currentPlayer.getChances() > 0) {
            currentPlayer.updateOnWrongWord();

            System.out.printf("[" + getClass().getSimpleName() + "] Your word '%s' is NOT VALID. Lifes left: %d. Changes left %d\n", playerWord, currentPlayer.getLifes(), currentPlayer.getChances());
            if (currentPlayer.getChances() == 0 || currentPlayer.getLifes() == 0) {
                currentPlayer.updateOnWrongWord();
                isValidWord = false;
                break;
            }
            playerWord = this.getInputWordFromPlayer(currentPlayer);
            isValidWord = wordService.validateWord(previousWord, playerWord, roundStart);
        }


        if (isValidWord) {
            System.out.printf("[" + getClass().getSimpleName() + "] Your word '%s' is VALID. Lifes left: %d. Changes left %d\n", playerWord, currentPlayer.getLifes(), currentPlayer.getChances());
            previousWord = playerWord;
            this.roundStart = false;

            if (wordService.closeWord(playerWord)) {
                this.roundStart = true;
                Player nextPlayer = this.getNextActivePlayer();
                nextPlayer.updateLifes();

                log("SORRY! " + nextPlayer.getName() + " has " + nextPlayer.getLifes() + " lives left!");

                this.previousWord = "";
            }
        } else {
            this.previousWord = "";
            this.roundStart = true;
        }

        currentPlayer.setChances(Constants.CHANCES);
    }

    private String getInputWordFromPlayer(Player currentPlayer) {

        String startWithString = "";
        String playerWord = "";

        /*
        * A little more complex check for the case when the game starts
        * but the words that the player inputs are not in the dictionary
        * so the player must enter a new word but with the same letter
        * */
        if (roundStart && currentPlayer.getChances() > 0 && (previousWord.length() == 0 || previousWord.length() == 2)) {
            Random r = new Random();
            startWithString = (char)(r.nextInt(26) + 'a') + "";
            previousWord = startWithString;
        } else {
            startWithString = this.previousWord.substring(this.previousWord.length() - (roundStart ? 1 : 2));
        }

        Scanner scanner = new Scanner(System.in);
        try {

            log("Enter a word starting with " + startWithString + " :");

            playerWord = scanner.nextLine();  // Read user input
            log("--------------------");

            return playerWord;
        } catch (Exception e) {
            this.log("You've input a wrong parameter! Just numbers please!");
            return "";
        }
    }

    protected Player getNextActivePlayer() {

        int tempCurrentPlayerId = this.currentPlayerId;
        tempCurrentPlayerId++;
        if (tempCurrentPlayerId >= this.playerList.size()){
            tempCurrentPlayerId = 0;
        }

        while (!playerList.get(tempCurrentPlayerId).isActive()) {
            if (tempCurrentPlayerId >= this.playerList.size()){
                tempCurrentPlayerId = 0;
            }
            tempCurrentPlayerId++;
        }

        List<Player> eligiblePlayersList = this.getPlayersEligibleForPlay();
        if (eligiblePlayersList.size() == 1) {
            this.gameOn = false;
        }
        this.currentPlayerId = tempCurrentPlayerId;
        return playerList.get(tempCurrentPlayerId);
    }

//    protected Player getNextPlayerFromPrevious() {
//
//        List<Player> eligiblePlayersList = this.getPlayersEligibleForPlay();
//
//        currentPlayerId++;
//        //Cycle reset
//        if (currentPlayerId >= eligiblePlayersList.size()) {
//            currentPlayerId = 0;
//        }
//
//        if (eligiblePlayersList.size() != 1) {
//
//            Player currentPlayer = this.playerList.get(currentPlayerId);
//            while (!currentPlayer.isActive()) {
//                currentPlayerId++;
//
//                //Cycle reset
//                if (currentPlayerId == eligiblePlayersList.size()) {
//                    currentPlayerId = 0;
//                }
//            }
//
//            return currentPlayer;
//        }
//
//        this.gameOn = false;
//        return eligiblePlayersList.get(0);
//    }

    private List<Player> getPlayersEligibleForPlay() {

        return this.playerList
                .stream()
                .filter(player -> player.isActive())
                .collect(Collectors.toList());

    }

    private List<Player> initializeGame() {

        /* Set game's name randomly*/
        this.gameName = "GameID " + Instant.now().toEpochMilli();

        //get number of players
        int numberOfPlayer = 0;
        Scanner scanner = new Scanner(System.in);
        try {

            this.log("Enter the number of players:");

            numberOfPlayer = scanner.nextInt();  // Read user input
            this.log("User Input : " + numberOfPlayer);
            this.log("\n--------------------");

            while (numberOfPlayer < 2) {
                //At least 2 players
                this.log("Enter 2 or more players please!!");
                numberOfPlayer = scanner.nextInt();  // Read user input
            }

            Player.setNoPlayers(numberOfPlayer);

            List<Player> playerList = new ArrayList<>();
            for (int i = 1; i <= numberOfPlayer; i++) {
                playerList.add(new Player(i));
            }

            Collections.shuffle(playerList);
            return playerList;

        } catch (Exception e) {
            this.log("You've input a wrong parameter! Just numbers please!");
            return initializeGame();
        }
    }

    @Override
    public String toString() {
        return "";
    }

    private void log(String message) {
        System.out.println("[" + getClass().getSimpleName() + "] " + message);
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public String getGameName() {
        return gameName;
    }
}
