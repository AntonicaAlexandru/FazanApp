package com.fazan.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;

public class WordService {

    private static Set<String> wordSet = null;
    private Set<String> usedWords;


    public WordService() {
        usedWords = new HashSet<>();

        /*
            In order to make this easier i'm gonna use a file as database
            Read file just once and set it to class
        */
        if (WordService.wordSet == null) {
            WordService.wordSet = new HashSet<>();
            try {
                Path path = Paths.get(getClass().getClassLoader()
                        .getResource("resources/words.txt").toURI());

                Stream<String> lines = Files.lines(path);
                lines.forEach(s -> wordSet.add(s));
                lines.close();
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Method that validates word
     * agains the "database" file from
     * resources/words.txt
     */
    public boolean validateWord(String previousWord, String playerWord, boolean roundStart) {

        /* Sanity checks */
        assertNotNull(previousWord);
        assertNotNull(previousWord);
        assertNotNull(playerWord);

        if (usedWords.contains(playerWord)) {
            log("You have already used this word!");
            return false;
        }

        if (!wordSet.contains(playerWord)) {
            log("Not a word in our dictionary!");
            return false;
        }

        String endingOfPreviousWord = previousWord.substring(previousWord.length() - (roundStart ? 1 : 2));
        if (!playerWord.startsWith(endingOfPreviousWord)) {
            log("Use a word that starts with last 2 letters of " + previousWord);
            return false;
        }

        usedWords.add(playerWord);

        return true;
    }

    /**
     * Method that checks whether or not the given word
     * is a closed word or not
     */
    public boolean closeWord(String playerWord) {
        /* Sanity checks */
        assertNotNull(playerWord);
        String suffix = playerWord.substring(playerWord.length() - 2);

        if (wordSet.stream().filter(s -> s.startsWith(suffix)).collect(Collectors.toList()).size() <= 0) {
            log("There is no word in out dictionary with :" + suffix);
            return true;
        }

        return false;

    }

    private void log(String message) {
        System.out.println("[" + getClass().getSimpleName() + "] " + message);
    }
}
