package com.fazan.model;

import com.fazan.service.WordService;
import com.fazan.utils.Constants;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Method that prints some games stats
     * like end game standings
     */
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

    /**
     * Main logic method. Take input from the given
     * player and validates the word, gives the player
     * chances to input a valid word until hs life or
     * chances get to 0
     */
    private void continueGameWithPlayer(Player currentPlayer) {

        String playerWord = this.getInputWordFromPlayer(currentPlayer);
        boolean isValidWord = wordService.validateWord(previousWord, playerWord, roundStart);

        while (!isValidWord && currentPlayer.getLifes() > 0 && currentPlayer.getChances() > 0) {
            currentPlayer.updateOnWrongWord();

            System.out.printf("[" + getClass().getSimpleName() + "] Your word '%s' is NOT VALID. Lifes left: %d. Chances left %d\n", playerWord, currentPlayer.getLifes(), currentPlayer.getChances());
            if (currentPlayer.getChances() == 0 || currentPlayer.getLifes() == 0) {
                currentPlayer.updateOnWrongWord();
                isValidWord = false;
                break;
            }
            playerWord = this.getInputWordFromPlayer(currentPlayer);
            isValidWord = wordService.validateWord(previousWord, playerWord, roundStart);
        }


        /*
         * Check to see if the word was valid if it was we replace the previous word
         * and we also check if it's a close word case in which we update the life of the
         * next player and skip him
         *
         * If it wasn't valid, we finished a round reinitialise the previous word and set
         * roundStart on true
         * */
        if (isValidWord) {
            System.out.printf("[" + getClass().getSimpleName() + "] Your word '%s' is VALID. Lifes left: %d. Chances left %d\n", playerWord, currentPlayer.getLifes(), currentPlayer.getChances());
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

    /**
     * Maine method that takes input from currentPlayer
     */
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

    /**
     * Return the next player that has active status on true
     * */
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


    /**
     * Return a list of players that are still eligible to play
     * */
    private List<Player> getPlayersEligibleForPlay() {

        return this.playerList
                .stream()
                .filter(player -> player.isActive())
                .collect(Collectors.toList());

    }


    /**
     * Method that initializes the main properties
     * of the game.
     * */
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
