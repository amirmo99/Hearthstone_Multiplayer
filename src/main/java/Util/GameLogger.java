package Util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import configs.PathConfigs;
import enums.LogType;
import logic.Player;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class GameLogger implements Serializable {

    private static HashMap<Player, GameLogger> map = new HashMap<>();

    private PrintWriter printer;
    private String fileName;
    private PathConfigs configs;
    private Player player;

    private GameLogger(Player player) {
        this.player = player;
        this.configs = new PathConfigs();
        openFile();
    }

    public static GameLogger getInstance(Player player) {
        if (!map.containsKey(player)) {
            map.put(player, new GameLogger(player));
        }
        return map.get(player);
    }

    private GameLogger() { }

    private void openFile() {
        try {
            fileName = configs.getLogs() + "/" + player.getUsername() + "_" + player.getId() + ".txt";
            printer = new PrintWriter(new FileWriter(fileName, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logCreateAccount(String password) {

        writeString("Username: " + player.getUsername());
        writeString("Created @ " + LocalDateTime.now());
        writeString("Password:  " + password);

        writeString("\n");
    }

    public void logDeleteAccount() {
        try {
            File tempFile = new File("temp.txt");
            File inputFile = new File(fileName);

            FileWriter fileWriter = new FileWriter(tempFile);
            List<String> strings = Files.readAllLines(inputFile.toPath());

            printer = new PrintWriter(fileWriter);
            writeLog(LogType.DELETE_ACCOUNT);

            for (String string : strings) {
                printer.println(string);
            }
            printer.close();
            fileWriter.close();

            changeFileLocation(tempFile, inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeFileLocation(File tempFile, File targetFile) {
        boolean successful = tempFile.renameTo(targetFile);

        if (!successful) {
            try {
                Files.move(tempFile.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                System.out.println("Could not move temp file");
                ex.printStackTrace();
            }
        }
    }


    public void writeLog(LogType type) {
        writeLog(type, "No Message");
    }

    public void writeLog(LogType event, String message) {
        String log = "__" + event.toString().replace("_", " ") + "__ | @ " + LocalDateTime.now() + " | Message: " + message;

        writeString(log);
    }

    private void writeString(String string) {
        printer.println(string);
        printer.flush();
//        System.out.println(string);
    }
}
