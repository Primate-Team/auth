package ru.samsonium.primate.auth;

import java.time.Instant;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.HOURS;

public class Profile {
    public final int id;
    public final String uuid;
    public final String password;
    public Instant lastLogin;
    public boolean isInGame;

    public Profile(int id, String uuid, String password, Instant lastLogin, int in_game) {
        this.id = id;
        this.uuid = uuid;
        this.password = password;
        this.lastLogin = lastLogin;
        this.isInGame = in_game == 1;
    }

    /**
     * Updates the value of the "in_game" field in the database
     * @param newState New state
     */
    public void setInGameState(boolean newState) {
        Objects.requireNonNull(DB.get()).updateInGameState(uuid, newState);
        isInGame = newState;
    }

    /**
     * Updates player's last login
     */
    public void updateLastLogin() {
        Instant now = Instant.now();
        DB.get().updateLastLogin(uuid, now);
        lastLogin = now;
    }

    /**
     * Can player can play without login
     * @return authorization state
     */
    public boolean isAuthorized() {
        Instant now = Instant.now();
        return HOURS.between(now, lastLogin) <= 8;
    }
}
