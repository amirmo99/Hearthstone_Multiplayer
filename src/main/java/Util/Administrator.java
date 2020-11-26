package Util;

import configs.LogicConstants;
import enums.LogType;
import models.Card;
import models.Deck;
import models.Heroes;
import logic.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Administrator {

    public static final int SUCCESS = 0;
    public static final int SHORT_PASSWORD = 1;
    public static final int TAKEN_USER = 2;


    private static final String SALT = "WOW-Its AmaZING";

    public synchronized static void deletePlayer(Player playerToDelete) {
        try {
            String username = playerToDelete.getUsername();
            List<Player> updatedPlayers = new ArrayList<>();

            List<Player> players = Tools.getAllPlayers();

            for (Player player : players) {
                if (!player.getUsername().equals(username))
                    updatedPlayers.add(player);
            }
//            PlayerLogger.writeLog("Delete Player");
            playerToDelete.getLogger().logDeleteAccount();
            Tools.writeAllPlayers(updatedPlayers);

        } catch (NullPointerException e) {
//            PlayerLogger.writeLog("Error", "Error in deleting player : " + e.getMessage());
            playerToDelete.getLogger().writeLog(LogType.ERROR, "Error in deleting player : " + e.getMessage());
        }

    }

    public static boolean isUsernameTaken(String user) {
        try {
            for (Player player : Tools.getAllPlayers()) {
                if (user.equalsIgnoreCase(player.getUsername()))
                    return true;
            }
            return false;
        } catch (NullPointerException e) {
//            PlayerLogger.writeLog("error", "Error in finding username : " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    public synchronized static void updateDataModels(Player player) {

        List<Player> updatePlayers = new ArrayList<>();
        try {
            List<Player> players = Tools.getAllPlayers();

            for (Player player1 : players) {
                if (player.getUsername().equals(player1.getUsername())) {
                    updatePlayers.add(player);
                } else
                    updatePlayers.add(player1);
            }

            Tools.writeAllPlayers(updatePlayers);
        } catch (NullPointerException e) {
//            PlayerLogger.writeLog("Error", "Error in updating data models : " + e.getMessage());
            player.getLogger().writeLog(LogType.ERROR, "Error in updating data models : " + e.getMessage());
        }
    }

    public static boolean isAuthorized(String username, String password) {

        String hashedPassword = Tools.generateHash(SALT + password);

        try {
            List<Player> allPlayers = Tools.getAllPlayers();

            for (Player player : allPlayers) {
                if (player.getUsername().equals(username) &&
                        player.getPassword().equals(hashedPassword))
                    return true;
            }

        } catch (NullPointerException e) {
//            PlayerLogger.writeLog("Error", "Error in authorizing player : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public synchronized static void makePlayer(String username, String password) {

        try {
            String hashedPassword = Tools.generateHash(SALT + password);

            List<Player> allPlayers = Tools.getAllPlayers();

            // All models.heroes available
            List<Heroes> myHeroes = new ArrayList<>(Heroes.getAllHeroes());
            System.out.println("my models.heroes size : " + myHeroes.size());

//            // Only Mage is available
//            List<Heroes> myHeroes = new ArrayList<>();
//                        myHeroes.add(Heroes.getHero("Mage"));

            Player newPlayer = new Player(UUID.randomUUID().toString(), username, hashedPassword,
                    new LogicConstants().getInitialGems(), myHeroes, Card.getAllCards());
            newPlayer.getLogger().logCreateAccount(password);

            List<Player> updatedPlayers = new ArrayList<>(allPlayers);
            updatedPlayers.add(newPlayer);

            Tools.writeAllPlayers(updatedPlayers);

        } catch (NullPointerException e) {
//            PlayerLogger.writeLog("Error", "Error in making player : " + e.getMessage());
            e.printStackTrace();
            System.out.println("Could not create account with " + username + ":" + password);
        }

    }

    public static Player getPlayer(String user) {

        Player requestedPlayer = null;
        try {
            List<Player> players = Tools.getAllPlayers();

            for (Player player : players) {
                if (player.getUsername().equalsIgnoreCase(user)) {
                    requestedPlayer = player;
                    syncDecks(player);
                    break;
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Error in getting player: " + user + " | " + e.getMessage());
//            PlayerLogger.writeLog("error", "Error in getting player : " + e.getMessage());
        }

        return requestedPlayer;
    }

    private static void syncDecks(Player requestedPlayer) {
        if (requestedPlayer.getActiveDeck() != null) {
            for (Deck myDeck : requestedPlayer.getMyDecks()) {
                if (requestedPlayer.getActiveDeck().getName().equalsIgnoreCase(myDeck.getName())) {
                    requestedPlayer.setActiveDeck(myDeck);
                    break;
                }
            }
        }
    }

    public static boolean loginRequest(String username, String password) {
        if (Administrator.isAuthorized(username, password)) {
            Player player = Administrator.getPlayer(username);
//            PlayerLogger.setPlayer(player);
//            PlayerLogger.writeLog("Sign In", "");
            player.getLogger().writeLog(LogType.SIGN_IN);
            SyncData.syncWithDataBase(player);
            return true;
        }
        return false;
    }

    public static int signUpRequest(String username, String password) {
        if (password.length() < 3)
            return SHORT_PASSWORD;
        else if (Administrator.isUsernameTaken(username))
            return TAKEN_USER;
        else {
            Administrator.makePlayer(username, password);
            Player player = Administrator.getPlayer(username);
//            PlayerLogger.setPlayer(player);
//            PlayerLogger.writeLog("Sign Up", password);
//            PlayerLogger.writeLog("Sign In", "");
            player.getLogger().writeLog(LogType.SIGN_IN);
            return SUCCESS;
        }
    }
}
