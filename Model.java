

import javax.swing.plaf.nimbus.State;
import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.sql.*;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Model {

    private Player getStartInfo() { // Get Player-information before game start
        final String DEFAULT_DRIVER_CLASS = "com.mysql.jdbc.Driver";
        String hostname = "localhost";
        String dbName = "SlutProjektWU";
        int port = 3306;
        final String DEFAULT_URL = "jdbc:mysql://"+ hostname +":"+port+"/"+dbName + "?useSSL=false";
        final String DEFAULT_USERNAME = "emil";
        final String DEFAULT_PASSWORD = "edberg00";
        String currentUser = View.getUsername(); // Player inserts username
        Player P = null;
        try {

            Connection conn = DriverManager.getConnection(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            String startInfo = "SELECT * FROM player WHERE name = '" + currentUser + "'"; // Fetching data from the current player

            Statement gameStart = conn.createStatement();
            ResultSet gS = gameStart.executeQuery(startInfo);


            if (gS.next()){ // Create a Player P with the data fetched

                int id = gS.getInt("ID");
                String name = gS.getString("name");
                int HP = gS.getInt("HP");
                int weapon = gS.getInt("weapon");
                int armor = gS.getInt("armor");
                int room = gS.getInt("room");

                P = new Player(id, name, HP, weapon, armor, room); // Making a Player P

            }else { // If username not found, create a new player-instance--------------------------------------------------------------------------------------------------------------
                String Username = View.getNewUsername(); // Player inserts the chosen Username

                Statement createUserSt = conn.createStatement();
                createUserSt.executeUpdate("INSERT INTO player (name, HP, weapon, armor, room) VALUES ('" + Username + "', 100, 1, 1, 1)");
                ResultSet nU = createUserSt.executeQuery("SELECT * FROM player WHERE name = '" + Username + "'"); // Fetching data from the new player-instance in the DB, to get ID
                while (nU.next()) {

                    int id = nU.getInt("ID");
                    String name = nU.getString("name");
                    int HP = nU.getInt("HP");
                    int weapon = nU.getInt("weapon");
                    int armor = nU.getInt("armor");
                    int room = nU.getInt("room");


                    P = new Player(id, name, HP, weapon, armor, room);// Making a Player P

                }
                createUserSt.executeUpdate("INSERT INTO stats ( PlayerID, kills, deaths, level) VALUES (" + P.id + ", 0, 0, 1)");
                Statement WSt = conn.createStatement();
                WSt.executeUpdate("INSERT INTO weapons (name, PlayerID, damage, wear) VALUES ('Starter'," + P.id + ", 15, 25)");
                ResultSet Weapon = WSt.executeQuery("SELECT id FROM weapons where PlayerID = " + P.id);
                while(Weapon.next()){
                    int weaponID = Weapon.getInt("ID");
                    createUserSt.executeUpdate("UPDATE player SET weapon = " + weaponID + " WHERE id = " + P.id);
                }

                conn.close();

            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
        return P;
    }
    private Player updatePlayer(String a){ // update a specific Player-instance in the database
        final String DEFAULT_DRIVER_CLASS = "com.mysql.jdbc.Driver";
        String hostname = "localhost";
        String dbName = "SlutProjektWU";
        int port = 3306;
        final String DEFAULT_URL = "jdbc:mysql://"+ hostname +":"+port+"/"+dbName + "?useSSL=false";
        final String DEFAULT_USERNAME = "emil";
        final String DEFAULT_PASSWORD = "edberg00";
        try {
            Connection conn = DriverManager.getConnection(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            Statement st = conn.createStatement();
            st.executeUpdate(a);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void playGame(Player P){ // Start the game
        //DB
        final String DEFAULT_DRIVER_CLASS = "com.mysql.jdbc.Driver?useSSL=false";
        String hostname = "localhost";
        String dbName = "SlutProjektWU";
        int port = 3306;
        final String DEFAULT_URL = "jdbc:mysql://"+ hostname +":"+port+"/"+dbName + "?useSSL=false";
        final String DEFAULT_USERNAME = "emil";
        final String DEFAULT_PASSWORD = "edberg00";

        Scanner sc = new Scanner(System.in);
        Weapon W = null;
        String name = P.name;
        int currentRoom = P.room;
        int hp = P.HP;
        boolean play = true;
        boolean fighting;
        try {
            Connection conn = DriverManager.getConnection(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM weapons WHERE id = " +  P.id);
            while(rs.next()){
                String wName = rs.getString("name");
                int damage = rs.getInt("damage");
                int wear = rs.getInt("wear");
                W = new Weapon(P.id, wName, damage, wear, 1);
            }
            while (play){
                Monster M = new Monster (1, 25 + currentRoom * 2, "Mob", 15);
                String enterRoom = "Welcome to room " + currentRoom + ".";
                String compRoom = "Type a to attack " + M.name;
                String finishedRoom = "Congratulations, you completed this room";
                fighting = true;
                View.dialog(enterRoom);
                View.dialog(M.name + " Spawned");
                while (fighting){
                    View.dialog(compRoom);
                    View.dialog("Mob has " + M.HP + " left");
                    View.dialog("You have " + P.HP + " left");
                    String nextLn = sc.nextLine();
                    if (nextLn.equalsIgnoreCase("a")){
                        M.HP = M.HP - W.damage;
                        if (M.HP < 1){
                            currentRoom++;
                            updatePlayer("UPDATE player SET room = " + currentRoom + " WHERE id = " + P.id);
                            Random R = new Random();
                            int nr = R.nextInt(100);

                            if (nr > 54){
                                if (nr <= 69 ){
                                    Weapon newW = getWeapon(nr, P.id);
                                    View.dialog("You found a " + newW.name + "!");
                                    View.dialog("Press \"Y\" to keep or \"T\" to throw");

                                    if (sc.nextLine().equalsIgnoreCase("Y")){
                                        updatePlayer("INSERT INTO weapons (name, PlayerID, damage, wear) VALUES ('" + newW.name + "'," + P.id + ", " + newW.damage + ", " + newW.wear + ")");
                                        ResultSet gw = st.executeQuery("SELECT * FROM weapons WHERE PlayerID = " +  P.id);
                                        while (gw.next()){
                                            int id = gw.getInt("id");
                                            String wName = rs.getString("name");
                                            int damage = rs.getInt("damage");
                                            int wear = rs.getInt("wear");
                                            W = new Weapon(P.id, wName, damage, wear, 1);

                                            updatePlayer("UPDATE player SET weapon = " + id);
                                        }
                                    }
                                }else if (nr <= 84){
                                    getPotion(nr, P.id);
                                }else if (nr <= 100){
                                    getPotion(nr, P.id);
                                }
                            }




                            View.dialog(finishedRoom);
                            fighting = false;

                        }else{
                            P.HP = P.HP - M.dmg;
                            if (P.HP <= 0){

                                View.dialog("You're dead, GG!");
                                updatePlayer("UPDATE player SET HP = 100 WHERE id = " + P.id);
                                System.exit(0);
                            }
                            updatePlayer("UPDATE player SET HP = " + P.HP + " WHERE id = " + P.id);
                        }

                    }else if (nextLn.equalsIgnoreCase("reset")){
                        currentRoom = 1;
                        P.HP = 100;
                        fighting = false;
                    }else if (nextLn.equalsIgnoreCase("quit")) {
                        play = false;
                        System.exit(0);
                    }

                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public Weapon getWeapon(int nr, int id){
        if (55 <= nr && nr <= 59){//Weapon
            Weapon newW = new Weapon(id,"Sword", 18, 30, 1);
            return newW;

        }else if (60 <= nr && nr <= 63){
            Weapon newW = new Weapon(id,"Sword", 20, 30, 1);
            return newW;
        }else if (64 <= nr && nr <= 66){
            Weapon newW = new Weapon(id,"Sword", 21, 40, 1);
            return newW;
        }else if (67 <= nr && nr <= 68){
            Weapon newW = new Weapon(id,"Sword", 26, 25, 1 );
            return newW;
        }else if (69 == nr){
            Weapon newW = new Weapon(id,"Sword", 35, 20, 1 );
            return newW;
        }else{
            return null;
        }
    }
    public Armor getArmor(int nr, int id){
        if (70 <= nr && nr <= 74){//Armor
            Armor newA = new Armor(id, "Armor", 4, 20);
            return newA;
        }else if (75 <= nr && nr <= 78){
            Armor newA = new Armor(id, "Armor", 6, 20);
            return newA;
        }else if (79 <= nr && nr <= 81){
            Armor newA = new Armor(id, "Armor", 7, 30);
            return newA;
        }else if (82 <= nr && nr <= 83){
            Armor newA = new Armor(id, "Armor", 10, 25);
            return newA;
        }else if (84 == nr){
            Armor newA = new Armor(id, "Armor", 18, 20);
            return newA;
        }else{
            return null;
        }
    }

    public Object getPotion(int nr, int id){
        if (85 <= nr && nr <= 89){//Potions
            Potion newP = new Potion(id, "Potion", 20);
            return newP;
        }else if (90 <= nr && nr <= 93){
            Potion newP = new Potion(id, "Potion", 35);
            return newP;
        }else if (94 <= nr && nr <= 96){
            Potion newP = new Potion(id, "Potion", 50);
            return newP;
        }else if (97 <= nr && nr <= 98){
            Potion newP = new Potion(id, "Potion", 75);
            return newP;
        }else if (99 <= nr && nr <= 100){
            Potion newP = new Potion(id, "Potion", 100);
            return newP;
        }else{
            return null;
        }
    }

    public Model() throws SQLException {
        Player P = getStartInfo();

        playGame(P);


    }

}

