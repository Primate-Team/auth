package ru.samsonium.primate.auth;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;
import java.time.Instant;
import java.util.logging.Level;

public class DB {
    private static DB instance;
    private Connection cn;

    public DB() throws SQLException {
        File dataFolder = PrimateAuth.get().getDataFolder();
        if (!dataFolder.exists())
            dataFolder.mkdirs();

        // Setup Connection instance
        String path = dataFolder.getAbsolutePath() + "/profiles.db";
        cn = DriverManager.getConnection("jdbc:sqlite:" + path);

        // Setup table
        Statement stmt = cn.createStatement();
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS profiles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uuid TEXT NOT NULL,
                password TEXT NOT NULL,
                last_login TEXT NOT NULL,
                in_game INTEGER NOT NULL
            );
        """);
        stmt.close();
    }

    /**
     * Initialize database instance
     * @throws SQLException if connection failed
     */
    public static void init() throws SQLException {
        if (instance == null)
            instance = new DB();
    }

    /**
     * Get DB instance
     * @return DB class instance
     */
    @NotNull
    public static DB get() {
        return instance;
    }

    /**
     * Closes connection to database
     */
    public void close() throws SQLException {
        cn.close();
    }

    /**
     * Updates "in_game" field for row with specified uuid
     * @param uuid Player ID
     * @param state New state
     */
    public void updateInGameState(String uuid, boolean state) {
        String sql = "UPDATE profiles SET in_game=? WHERE uuid=?";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setInt(1, state ? 1 : 0);
            stmt.setString(2, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            PrimateAuth.get().getLogger().log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Updates "last_login" field for row with specified uuid
     * @param uuid Player ID
     * @param instant New time
     */
    public void updateLastLogin(String uuid, Instant instant) {
        String sql = "UPDATE profiles SET last_login=? WHERE uuid=?";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setString(1, instant.toString());
            stmt.setString(2, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            PrimateAuth.get().getLogger().log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Adds new player to database
     * @param uuid Player ID
     */
    public void addPlayer(String uuid) {
        String sql = "INSERT INTO profiles(uuid, last_login, in_game) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            stmt.setString(2, Instant.now().toString());
            stmt.setInt(3, 1);
            stmt.executeUpdate();
        } catch (SQLException e) {
            PrimateAuth.get().getLogger().log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Verifies player's login and password
     * @param uuid Player ID
     * @param password Password
     * @return Is data correct
     */
    public boolean verifyLogin(String uuid, String password) {
        String sql = "SELECT COUNT(id) as result FROM profiles WHERE uuid=? AND password=?";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            rs.next();
            int data = rs.getInt("result");
            if (data == 0) return false;

            rs.close();
            return true;
        } catch (SQLException e) {
            PrimateAuth.get().getLogger().log(Level.SEVERE, e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves player's profile
     * @param uuid Player ID
     * @return Player profile or null
     */
    public Profile getPlayer(String uuid) {
        String sql = "SELECT * FROM profiles WHERE uuid=?";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;
            int id = rs.getInt("id");
            int in_game = rs.getInt("in_game");
            String last_login = rs.getString("last_login");

            rs.close();

            return new Profile(id, uuid, "", Instant.parse(last_login), in_game);
        } catch (SQLException e) {
            PrimateAuth.get().getLogger().log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
}
