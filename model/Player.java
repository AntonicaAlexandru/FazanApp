package com.fazan.model;

import com.fazan.utils.Constants;

import java.util.function.Supplier;

public class Player {
    private static int noPlayers;

    private boolean active = true;
    private Integer chances = Constants.CHANCES;
    private Integer lifes = Constants.LIFES;
    private Integer gamePlace = 0;
    private String name;

    public Player(int index) {
        this.name = "Player" + index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getChances() {
        return chances;
    }

    public void setChances(int chances) {
        this.chances = chances;
    }

    public Integer getLifes() {
        return lifes;
    }

    public void setLifes(int lifes) {
        this.lifes = lifes;
    }

    public boolean isActive() {
        return this.active;
    }


    public static void setNoPlayers(int noPlayers) {
        Player.noPlayers = noPlayers;
    }

    public void updateOnWrongWord() {

        this.chances--;

        if (getChances() <= 0) {
            this.lifes--;
            if (this.lifes <= 0) {
                this.lifes = 0;
                this.active = false;
                this.gamePlace = Player.noPlayers--;
            }
        }
    }

    public Integer getGamePlace() {
        return gamePlace;
    }

    public void setGamePlace() {
        this.gamePlace = Player.noPlayers;
    }

    public void updateLifes() {

        this.lifes--;
        if (this.lifes == 0) {
            this.active = false;
            this.gamePlace = Player.noPlayers--;
        }
    }
    @Override
    public String toString() {
        return String.format("Player %s finished on place %d", this.name, this.gamePlace);
    }
}

