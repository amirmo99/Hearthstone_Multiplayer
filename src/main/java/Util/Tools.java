package Util;

import configs.PathConfigs;
import models.Card;
import models.Heroes;
import logic.Player;
import com.fasterxml.jackson.databind.*;
import models.InfoPassive;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Tools {

    static ObjectMapper objectMapper = new ObjectMapper().enableDefaultTyping();
    static PathConfigs configs = new PathConfigs();

    public synchronized static List<Card> getAllCards() {
        List<Card> list = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(configs.getCardsData());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            list = Arrays.asList(objectMapper.readValue(bufferedReader, Card[].class));
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public synchronized static List<InfoPassive> getAllPassives() {
        List<InfoPassive> passives = new ArrayList<>();
        FileReader fileReader;
        try {
            fileReader = new FileReader(configs.getPassivesData());
            passives = Arrays.asList(objectMapper.readValue(fileReader, models.InfoPassive[].class));
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return passives;
    }

//    public static void showHelp(String place) {
//        try {
//            String line;
//            FileReader fileReader = new FileReader("src/main/resources/Data/" + place + "_Help.txt");
//            BufferedReader br = new BufferedReader(fileReader);
//
//            while ((line = br.readLine()) != null)
//                System.out.println(line);
//
//            br.close();
//            fileReader.close();
//        } catch (Exception e) {
//            Log.writeLog("error", e.getMessage());
//        }
//
//    }

    public synchronized static List<Heroes> getAllHeroes() {
        List<Heroes> list = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(configs.getHeroesData());
            list = Arrays.asList(objectMapper.readValue(fileReader, Heroes[].class));
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;

    }

    public synchronized static List<Player> getAllPlayers() {
        try {
            FileReader fileReader = new FileReader(configs.getPlayersData());

            List<Player> players = Arrays.asList(objectMapper.readValue(fileReader, Player[].class));

            fileReader.close();

            return players;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized static void writeAllPlayers(List<Player> allPlayers) {
        try {

            File inputFile = new File(configs.getPlayersData());
            BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile));
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, allPlayers);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String generateHash(String input) {
        StringBuilder hash = new StringBuilder();

        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] hashedBytes = sha.digest(input.getBytes());
            char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f'};
            for (byte b : hashedBytes) {
                hash.append(digits[(b & 0xf0) >> 4]);
                hash.append(digits[b & 0x0f]);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash.toString();
    }

//    static int difference(String x, String y) {
//        int[][] dp = new int[x.length() + 1][y.length() + 1];
//
//        for (int i = 0; i <= x.length(); i++) {
//            for (int j = 0; j <= y.length(); j++) {
//                if (i == 0) {
//                    dp[i][j] = j;
//                } else if (j == 0) {
//                    dp[i][j] = i;
//                } else {
//                    dp[i][j] = min(dp[i - 1][j - 1]
//                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
//                            dp[i - 1][j] + 1,
//                            dp[i][j - 1] + 1);
//                }
//            }
//        }
//
//        return dp[x.length()][y.length()];
//    }

//    public static int costOfSubstitution(char a, char b) {
//        return a == b ? 0 : 1;
//    }
//
//    public static int min(int... numbers) {
//        return Arrays.stream(numbers)
//                .min().orElse(Integer.MAX_VALUE);
//    }

//    public static String findBestMatch(String command) {
//        command = command.toLowerCase();
//        String str = "";
//        int min = 4;
//        int[] differences = new int[allcommands.size()];
//        for (int i = 0; i < allcommands.size(); i++) {
//            differences[i] = difference(command, allcommands.get(i).toLowerCase());
//
//            if (differences[i] < min) {
//                min = differences[i];
//                str = allcommands.get(i);
//            }
//        }
//
//        if (min < 4)
//            return str;
//        else
//            return null;
//
//    }
}
