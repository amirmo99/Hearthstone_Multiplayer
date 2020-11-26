# Hearthstone Multiplayer
This game is the result of a project which was accomplished during the course Advanced Programming and written using java 11.0.6 . I used Gradle building tool and Intellij Community Edition software to write this game. 
It is based on a similar game named Hearthstone which is available on Google Play and App Store. 

When you run ServerCreator.java, the program listens on the selected TCP port (default is 8080, it can be chenaged inside ServerCreator.java) on the localhost. After that you can run multiple instances of ClientCreator.java and play.  

In order to play you must:
1. Clone this folder
2. Have Intellij or Eclipse installed. Also you need gradle.
3. Run src/main/java/admin/ServerCreator.java
4. Run src/main/java/admin/ClientCreator.java, create an acoount and play! Your password will be hashed and therefore it is safe, but other configs can be changed in src/main/resources directory.
5. Enjoy playing :)
