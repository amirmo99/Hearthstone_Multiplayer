//package Util;
//
//import logic.Player;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class PlayerLogger {
//
//    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//
//    private static Player player = new Player();
//
//    public static void writeLog(String event, String descriptions) {
//
//        FileWriter fileWriter;
//        try {
//            fileWriter = new FileWriter("logs/" + player.getUsername() +
//                    "-" + player.getId(), true);
//
//            LocalDateTime now = LocalDateTime.now();
//
//            if (event.equals("Sign Up"))
//                fileWriter.write("USERNAME : " + player.getUsername() +
//                        "\nCREATED_AT : " + dtf.format(now) + "\nPASSWORD : " + descriptions + "\n\n\n");
//
//            else if (event.equals("Delete Player")) {
//                fileWriter.close();
//                File tempFile = new File("temp.txt");
//                File inputFile = new File("logs/" + player.getUsername() + "-" + player.getId());
//
//                BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
//                fileWriter = new FileWriter(tempFile);
//
//                fileWriter.write("DELETED_AT : " + dtf.format(now) + "\n");
//                String str;
//                while ((str = bufferedReader.readLine()) != null)
//                    fileWriter.write(str + "\n");
//
//                fileWriter.write(event.toUpperCase() + " @ " + dtf.format(now) + " : " + descriptions + "\n");
//
//                boolean successful = tempFile.renameTo(inputFile);
//                bufferedReader.close();
//                fileWriter.close();
//
//                if (!successful) {
//                    try {
//                        Files.move(tempFile.toPath(), inputFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
//                    } catch (IOException ex) {
//                        System.out.println("Could not move temp file");
//                        ex.printStackTrace();
//                    }
//                }
//            } else
//                fileWriter.write(event.toUpperCase() + " @ " + dtf.format(now) + " : " + descriptions + "\n");
//
//            fileWriter.close();
//
//        } catch (IOException e) {
//            System.out.println(" Read File error on logging " + e.getMessage());
//            e.printStackTrace();
//        }
//
//    }
//
//    public static void writeLog(String event) {
//        writeLog(event, "");
//    }
//
//    public static void setPlayer(Player newplayer) {
//        player = newplayer;
//    }
//}
