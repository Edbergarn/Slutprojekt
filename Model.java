

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Model {

    private Player getStartInfo() { // Get Player-information before game start
        final String DEFAULT_DRIVER_CLASS = "com.mysql.jdbc.Driver";
        String hostname = "localhost";
        String dbName = "SlutProjektWU";
        int port = 3306;
        final String DEFAULT_URL = "jdbc:mysql://"+ hostname +":"+port+"/"+dbName;
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

            }else { // If username not found, create a new player-instance
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
                conn.close();
                Statement WSt = conn.createStatement();
                WSt.executeUpdate("INSERT INTO weapons (name, PlayerID, damage, wear) VALUES ('Starter'," + P.id + ", 20, 25)");
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
        final String DEFAULT_URL = "jdbc:mysql://"+ hostname +":"+port+"/"+dbName;
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
        final String DEFAULT_DRIVER_CLASS = "com.mysql.jdbc.Driver";
        String hostname = "localhost";
        String dbName = "SlutProjektWU";
        int port = 3306;
        final String DEFAULT_URL = "jdbc:mysql://"+ hostname +":"+port+"/"+dbName;
        final String DEFAULT_USERNAME = "emil";
        final String DEFAULT_PASSWORD = "edberg00";

        Scanner sc = new Scanner(System.in);
        Weapon W = null;
        String name = P.name;
        int currentRoom = P.room;
        int hp = P.HP;
        boolean play = true;
        try {
            Connection conn = DriverManager.getConnection(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM weapons WHERE id = " +  P.id);
            while(rs.next()){
                String wName = rs.getString("name");
                int damage = rs.getInt("damage");
                int wear = rs.getInt("wear");
                W = new Weapon(wName, damage, wear);
            }
            Monster M = new Monster (1, 25, "Mob", 15);
            while (play){
                String enterRoom = "Welcome to room " + currentRoom + ".";
                View.dialog(M.name + " Spawned");
                String compRoom = "Type a to attack " + M.name;
                String finishedRoom = "Congratulations, you completed this room";
                while (true){
                    View.dialog(enterRoom);
                    View.dialog(compRoom);
                    if (sc.nextLine().equalsIgnoreCase("a")){
                        M.HP = M.HP - W.damage;
                        P.HP = P.HP - M.dmg;
                        if (M.HP <= 0){
                            updatePlayer("UPDATE player SET room = " + currentRoom + " WHERE id = " + P.id);
                            currentRoom++;
                            View.dialog(finishedRoom);

                        }

                    }

                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }





    }

    public Model() throws SQLException {
        Player P = getStartInfo();

        playGame(P);


    }

}

