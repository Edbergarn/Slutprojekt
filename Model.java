
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Model {

    private Player getStartInfo() { // Get Player-information before game start
        final String DEFAULT_DRIVER_CLASS = "com.mysql.jdbc.Driver";
        String hostname = "10.80.44.40";
        String dbName = "eliren16";
        int port = 3306;
        final String DEFAULT_URL = "jdbc:mysql://"+ hostname +":"+port+"/"+dbName + "?useSSL=false";
        final String DEFAULT_USERNAME = "eliren16";
        final String DEFAULT_PASSWORD = "Elev123";
        String currentUser = View.getUsername(); // Player inserts username
        Player P = null;

        try {

            Connection conn = DriverManager.getConnection(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            String startInfo = "SELECT * FROM player WHERE name = '" + currentUser + "'"; // Fetching data from the current player

            Statement gameStart = conn.createStatement();
            ResultSet gS = gameStart.executeQuery(startInfo);//-----------------------------------------------------------------------------RECREATING ALREADY EXISTING PLAYER-------------------------------------------------

            P = getPlayer(gS);
            System.out.println(P.id);
            System.out.println(P.HP);
            System.out.println(P.weapon);
            System.out.println(P.armor);
            System.out.println(P.potion);
            System.out.println(P.room);

            if (P.id == 0){ // If username not found, create a new player-instance--------------------------------------------------------------------------------------------------------------
                String Username = View.getNewUsername(); // Player inserts the chosen Username

                Statement createUserSt = conn.createStatement();
                createUserSt.executeUpdate("INSERT INTO player (name, HP, weapon, armor, potion, room) VALUES ('" + Username + "', 100, 1, 1, 1, 1)");
                ResultSet nU = createUserSt.executeQuery("SELECT * FROM player WHERE name = '" + Username + "'"); // Fetching data from the new player-instance in the DB, to get ID
                while (nU.next()) {

                    int id = nU.getInt("ID");
                    String name = nU.getString("name");
                    int HP = nU.getInt("HP");
                    int weapon = nU.getInt("weapon");
                    int armor = nU.getInt("armor");
                    int potion = nU.getInt("potion");
                    int room = nU.getInt("room");


                    P = new Player(id, name, HP, weapon, armor, potion, room);// Making a Player P

                }
                createUserSt.executeUpdate("INSERT INTO stats ( PlayerID, kills, deaths, level) VALUES (" + P.id + ", 0, 0, 1)");
                Statement WSt = conn.createStatement();
                WSt.executeUpdate("INSERT INTO weapon (name, PlayerID, damage, wear) VALUES ('Starter'," + P.id + ", 15, 25)");
                ResultSet Weapon = WSt.executeQuery("SELECT id FROM weapon where PlayerID = " + P.id);
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
    private Player getPlayer(ResultSet a){
        int id = 0;
        String name = "";
        int HP = 0;
        int weapon = 0;
        int armor = 0;
        int potion = 0;
        int room = 0;

            try {
                while (a.next()) {
                    id = a.getInt("ID");
                    name = a.getString("name");
                    HP = a.getInt("HP");
                    weapon = a.getInt("weapon");
                    armor = a.getInt("armor");
                    potion = a.getInt("potion");
                    room = a.getInt("room");
            }


            return new Player(id, name, HP, weapon, armor, potion, room);// Making a Player P

        } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
    }
    private Player updatePlayer(String a){ // update a specific Player-instance in the database
        final String DEFAULT_DRIVER_CLASS = "com.mysql.jdbc.Driver";
        String hostname = "10.80.44.40";
        String dbName = "eliren16";
        int port = 3306;
        final String DEFAULT_URL = "jdbc:mysql://"+ hostname +":"+port+"/"+dbName + "?useSSL=false";
        final String DEFAULT_USERNAME = "eliren16";
        final String DEFAULT_PASSWORD = "Elev123";
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
        String hostname = "10.80.44.40";
        String dbName = "eliren16";
        int port = 3306;
        final String DEFAULT_URL = "jdbc:mysql://"+ hostname +":"+port+"/"+dbName + "?useSSL=false";
        final String DEFAULT_USERNAME = "eliren16";
        final String DEFAULT_PASSWORD = "Elev123";

        Scanner sc = new Scanner(System.in);
        Weapon W = null;
        Armor A = null;
        Potion PO = null;
        int tempID = 0;

        String name = P.name;
        int currentRoom = P.room;
        int hp = P.HP;
        boolean play = true;
        boolean fighting;
        try {
            Connection conn = DriverManager.getConnection(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            Statement st = conn.createStatement();
            ResultSet w = st.executeQuery("SELECT * FROM weapon WHERE PlayerID = " +  P.id);
            while(w.next()){
                String wName = w.getString("name");
                int damage = w.getInt("damage");
                int wear = w.getInt("wear");
                W = new Weapon(P.id, wName, damage, wear);
            }
            ResultSet a = st.executeQuery("SELECT * from armor WHERE PlayerID = " + P.id);
            while (a.next()){
                String aName = a.getString("name");
                int defence = a.getInt("defence");
                int wear = a.getInt("wear");
                A = new Armor(P.id, aName, defence, wear);
            }
            ResultSet p = st.executeQuery("SELECT * from potion WHERE PlayerID = " + P.id);
            while(p.next()){
                String pName = p.getString("name");
                int healing = p.getInt("healing");
                PO = new Potion(P.id, pName, healing);
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
                    View.dialog("Mob has " + M.HP + " HP left");
                    View.dialog("You have " + P.HP + " HP left");
                    String nextLn = sc.nextLine();
                    if (nextLn.equalsIgnoreCase("a")){
                        M.HP = M.HP - W.damage;
                        if (M.HP < 1){
                            currentRoom++;
                            updatePlayer("UPDATE player SET room = " + currentRoom + " WHERE id = " + P.id);
                            Random R = new Random();
                            int nr = R.nextInt(100);

                            if (nr > 54){
                                if (nr <= 69 ){//------------------------------------------------------WEAPON--------------------------------------
                                    Weapon newW = getWeapon(nr, P.id);
                                    View.dialog("You found a " + newW.name + "!");
                                    View.dialog("Damage: " + newW.damage);
                                    View.dialog("Press \"Y\" to keep or \"T\" to throw");

                                    if (sc.nextLine().equalsIgnoreCase("Y")){
                                        updatePlayer("UPDATE weapon SET PlayerID = 0 WHERE PlayerID = " + P.id);
                                        updatePlayer("INSERT INTO weapon (name, PlayerID, damage, wear) VALUES ('" + newW.name + "'," + P.id + ", " + newW.damage + ", " + newW.wear + ")");
                                        ResultSet gw = st.executeQuery("SELECT * FROM weapon WHERE PlayerID = " +  P.id);
                                        while (gw.next()){
                                            tempID = gw.getInt("id");
                                            String wName = gw.getString("name");
                                            int damage = gw.getInt("damage");
                                            int wear = gw.getInt("wear");
                                            W = new Weapon(P.id, wName, damage, wear);
                                            System.out.println(tempID + " " + wName + " " + damage);

                                        }
                                        System.out.println(tempID);
                                        updatePlayer("UPDATE player SET weapon = " + tempID + " WHERE id = " + P.id);

                                    }
                                }else if (nr <= 84){//-----------------------------------------------------------ARMOR---------------------------
                                    Armor newA = getArmor(nr, P.id);
                                    View.dialog("You found a " + newA.name + "!");
                                    View.dialog("Defence: " + newA.defence);
                                    View.dialog("Press \"Y\" to keep or \"T\" to throw");

                                    if (sc.nextLine().equalsIgnoreCase("Y")){
                                        updatePlayer("UPDATE armor SET PlayerID = 0 WHERE PlayerID = " + P.id);
                                        updatePlayer("INSERT INTO armor (name, PlayerID, defence, wear) VALUES ('" + newA.name + "'," + P.id + ", " + newA.defence + ", " + newA.wear + ")");

                                        ResultSet gw = st.executeQuery("SELECT * FROM armor WHERE PlayerID = " +  P.id);
                                        while (gw.next()){
                                            tempID = gw.getInt("id");
                                            String aName = gw.getString("name");
                                            int defence = gw.getInt("defence");
                                            int wear = gw.getInt("wear");
                                            A = new Armor(P.id, aName, defence, wear);
                                            System.out.println(tempID + " " + aName + " " + defence);


                                        }
                                        updatePlayer("UPDATE player SET armor = " + tempID + " WHERE id = " + P.id);
                                    }
                                }else if (nr <= 100){//----------------------------------------------------------POTION--------------------------------------------------------
                                    Potion newPO = getPotion(nr, P.id);
                                    View.dialog("You found a " + newPO.name + "!");
                                    View.dialog("Healing: " + newPO.healing );
                                    View.dialog("Press \"Y\" to keep or \"T\" to throw");

                                    if (sc.nextLine().equalsIgnoreCase("Y")){
                                        updatePlayer("UPDATE potion SET PlayerID = 0 WHERE PlayerID = " + P.id);
                                        updatePlayer("INSERT INTO potion (name, PlayerID, healing) VALUES ('" + newPO.name + "'," + P.id + ", " + newPO.healing + ")");
                                        ResultSet gw = st.executeQuery("SELECT * FROM potion WHERE PlayerID = " +  P.id);
                                        while (gw.next()){
                                            tempID = gw.getInt("id");
                                            String pName = gw.getString("name");
                                            int healing = gw.getInt("healing");
                                            PO = new Potion(P.id, pName, healing);
                                            System.out.println(tempID + " " + pName + " " + healing);



                                        }
                                        updatePlayer("UPDATE player SET potion = " + tempID + " WHERE id = " + P.id);
                                    }
                                }
                            }




                            View.dialog(finishedRoom);
                            fighting = false;

                        }else{
                            P.HP = P.HP - (M.dmg - A.defence);
                            A.wear--;
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
            Weapon newW = new Weapon(id,"Sword", 18, 30);
            return newW;
        }else if (60 <= nr && nr <= 63){
            Weapon newW = new Weapon(id,"Sword", 20, 30);
            return newW;
        }else if (64 <= nr && nr <= 66){
            Weapon newW = new Weapon(id,"Sword", 21, 40);
            return newW;
        }else if (67 <= nr && nr <= 68){
            Weapon newW = new Weapon(id,"Sword", 26, 25);
            return newW;
        }else if (69 == nr){
            Weapon newW = new Weapon(id,"Sword", 35, 20);
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

    public Potion getPotion(int nr, int id){
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

