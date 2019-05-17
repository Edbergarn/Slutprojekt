
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Model {
    /**
     * Fetching player-data if player already exists, otherwise it's creating a new player-instance
     * Player inserts a username, if username exists, it's fetching the data. If username does'nt exist it asks if the user want's to create a new player, if answer is yes it ask for username and then if username is not used, it creates a new player-, weapon- and armor-instance in the database
     * @return
     */
    private Player getStartInfo() { // Get Player-information before game start
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
            String startInfo = "SELECT * FROM players WHERE name = '" + currentUser + "'"; // Fetching data from the current player

            Statement gameStart = conn.createStatement();
            ResultSet gS = gameStart.executeQuery(startInfo);//-----------------------------------------------------------------------------RECREATING ALREADY EXISTING PLAYER----------------------------

            P = getPlayer(gS);

            assert P != null;

            if (P.id == 0){ // ------------------------------------------------------------------------------------------------------If username not found, create a new player-instance------------------
                String Username = View.getNewUsername(); // Player inserts the chosen Username

                Statement createUserSt = conn.createStatement();
                createUserSt.executeUpdate("INSERT INTO players (name, HP, weapon, armor, potion, room) VALUES ('" + Username + "', 100, 1, 1, 0, 1)");
                ResultSet nU = createUserSt.executeQuery("SELECT * FROM players WHERE name = '" + Username + "'"); // Fetching data from the new player-instance in the DB, to get ID
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
                createUserSt.executeUpdate("INSERT INTO stats (player_id, name, rounds, kills, deaths, level, maxRoom, foundSwords, foundArmors, foundPotions, destroyedItems) VALUES (" + P.id + ", '" + P.name + "', 0, 0, 0, 1, 1, 0, 0, 0, 0)");
                Statement WSt = conn.createStatement();
                WSt.executeUpdate("INSERT INTO weapon (name, player_id, damage, wear) VALUES ('Starter'," + P.id + ", 15, 25)");
                WSt.executeUpdate("INSERT INTO armor (name, player_id, defence, wear) VALUES ('Starter'," + P.id + ", 2, 25)");

                ResultSet Weapon = WSt.executeQuery("SELECT id FROM weapon where player_id = " + P.id);
                while(Weapon.next()){
                    int weaponID = Weapon.getInt("ID");
                    createUserSt.executeUpdate("UPDATE players SET weapon = " + weaponID + " WHERE id = " + P.id);
                }

                conn.close();

            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
        return P;
    }

    /**
     * Createing Player with from ResultSet
     * @param a ResultSet with Player info
     * @return
     */
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

    /**
     * Executes a update-statement with the SQL String a
     * @param a SQL String
     */
    private void update(String a){ // update a specific Player-instance in the database
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

    }

    /**
     * Updating stats-table in DB
     * @param s The stats
     */
    private void updateStats(Stats s){
        String hostname = "10.80.44.40";
        String dbName = "eliren16";
        int port = 3306;
        final String DEFAULT_URL = "jdbc:mysql://"+ hostname +":"+port+"/"+dbName + "?useSSL=false";
        final String DEFAULT_USERNAME = "eliren16";
        final String DEFAULT_PASSWORD = "Elev123";
        try {
            Connection conn = DriverManager.getConnection(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            Statement st = conn.createStatement();
            st.executeUpdate("UPDATE stats SET rounds = " + s.rounds + ", kills = " + s.kills + ", deaths = " + s.deaths + ", level = " + s.level + ", maxRoom = " + s.maxRoom + ", foundSwords = " + s.foundSwords + ", foundArmors = " + s.foundArmors + ", foundPotions = " + s.foundPotions + ", destroyedItems = " + s.destroyedItems + " WHERE player_id = " + s.player_id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *Where the game is played
     * @param P Player that plays the game
     */
    private void playGame(Player P){ // Start the game
        //DB
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

        int currentRoom = P.room;
        //boolean play = true;
        boolean fighting;
        try {
            Connection conn = DriverManager.getConnection(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            Statement st = conn.createStatement();
            ResultSet w = st.executeQuery("SELECT * FROM weapon WHERE player_id = " +  P.id);
            while(w.next()){
                String wName = w.getString("name");
                int damage = w.getInt("damage");
                int wear = w.getInt("wear");
                W = new Weapon(P.id, wName, damage, wear);
            }
            ResultSet a = st.executeQuery("SELECT * from armor WHERE player_id = " + P.id);
            while (a.next()){
                String aName = a.getString("name");
                int defence = a.getInt("defence");
                int wear = a.getInt("wear");
                A = new Armor(P.id, aName, defence, wear);
            }
            ResultSet p = st.executeQuery("SELECT * from potion WHERE player_id = " + P.id);
            while(p.next()){
                String pName = p.getString("name");
                int healing = p.getInt("healing");
                PO = new Potion(P.id, pName, healing);
            }
            ResultSet s = st.executeQuery("SELECT * FROM stats WHERE player_id = " + P.id);

            Stats stats = null;
            while(s.next()){
                int player_id = s.getInt("player_id");
                String name = s.getString("name");
                int rounds = s.getInt("rounds");
                int kills = s.getInt("kills");
                int deaths = s.getInt("deaths");
                int level = s.getInt("level");
                int maxRoom = s.getInt("maxRoom");
                int foundSwords = s.getInt("foundSwords");
                int foundArmors = s.getInt("foundArmors");
                int foundPotions = s.getInt("foundPotions");
                int destroyedItems = s.getInt("destroyedItems");

                stats = new Stats(player_id, name, rounds, kills, deaths, level, maxRoom, foundSwords, foundArmors, foundPotions, destroyedItems);

            }
            while (true){
                Monster M = new Monster (1, 25 + currentRoom * 2, "Mob", 15);
                String enterRoom = "Welcome to room " + currentRoom + ".";
                String compRoom = "Type a to attack " + M.name;
                String finishedRoom = "Congratulations, you completed this room";
                fighting = true;
                //stats.rounds = stats.rounds + 1;
                View.dialog(enterRoom);
                View.dialog(M.name + " Spawned");
                while (fighting){
                    View.dialog(compRoom);
                    View.dialog("Mob has " + M.HP + " HP left");
                    View.dialog("You have " + P.HP + " HP left");
                    String nextLn = sc.nextLine();
                    if (nextLn.equalsIgnoreCase("a")){
                        assert W != null;
                        M.HP -= W.damage;
                        W.wear--;
                        if (W.wear < 1){
                            P.weapon = 0;
                            W.damage = 10;
                        }
                        if (M.HP < 1){
                            currentRoom++;
                            update("UPDATE players SET room = " + currentRoom + " WHERE id = " + P.id);
                            assert stats != null;
                            stats.kills++;
                            View.dialog(finishedRoom);
                            Random R = new Random();
                            int nr = R.nextInt(100);

                            if (nr > 54){
                                if (nr <= 69 ){//------------------------------------------------------WEAPON--------------------------------------
                                    stats.foundSwords++;
                                    Weapon newW = getWeapon(nr, P.id);
                                    assert newW != null;
                                    View.dialog("You found a " + newW.name + "!");
                                    View.dialog("Damage: " + newW.damage + " Wear: " + newW.wear);
                                    View.dialog("Current Damage: " + W.damage + " Wear: " + W.wear);

                                    View.dialog("Press \"Y\" to keep or \"T\" to throw");

                                    if (sc.nextLine().equalsIgnoreCase("Y")){
                                        update("DELETE FROM weapon WHERE player_id = " + P.id);
                                        update("INSERT INTO weapon (name, player_id, damage, wear) VALUES ('" + newW.name + "'," + P.id + ", " + newW.damage + ", " + newW.wear + ")");
                                        ResultSet gw = st.executeQuery("SELECT * FROM weapon WHERE player_id = " +  P.id);
                                        while (gw.next()){
                                            tempID = gw.getInt("id");
                                            String wName = gw.getString("name");
                                            int damage = gw.getInt("damage");
                                            int wear = gw.getInt("wear");
                                            W = new Weapon(P.id, wName, damage, wear);
                                            System.out.println(tempID + " " + wName + " " + damage);

                                        }
                                        System.out.println(tempID);
                                        update("UPDATE players SET weapon = " + tempID + " WHERE id = " + P.id);

                                    }
                                }else if (nr <= 84){//-----------------------------------------------------------ARMOR---------------------------
                                    stats.foundArmors++;
                                    Armor newA = getArmor(nr, P.id);
                                    assert newA != null;
                                    View.dialog("You found a " + newA.name + "!");
                                    View.dialog("Defence: " + newA.defence + " Wear: " + newA.wear);
                                    assert A != null;
                                    View.dialog("Current Defence: " + A.defence + " Wear: " + A.wear);

                                    View.dialog("Press \"Y\" to keep or \"T\" to throw");

                                    if (sc.nextLine().equalsIgnoreCase("Y")){
                                        update("DELETE FROM armor WHERE player_id = " + P.id);
                                        update("INSERT INTO armor (name, player_id, defence, wear) VALUES ('" + newA.name + "'," + P.id + ", " + newA.defence + ", " + newA.wear + ")");

                                        ResultSet gw = st.executeQuery("SELECT * FROM armor WHERE player_id = " +  P.id);
                                        while (gw.next()){
                                            tempID = gw.getInt("id");
                                            String aName = gw.getString("name");
                                            int defence = gw.getInt("defence");
                                            int wear = gw.getInt("wear");
                                            A = new Armor(P.id, aName, defence, wear);
                                            System.out.println(tempID + " " + aName + " " + defence);


                                        }
                                        update("UPDATE players SET armor = " + tempID + " WHERE id = " + P.id);
                                    }
                                }else if (nr <= 100){//----------------------------------------------------------POTION--------------------------------------------------------
                                    stats.foundPotions++;
                                    Potion newPO = getPotion(nr, P.id);
                                    assert newPO != null;
                                    View.dialog("You found a " + newPO.name + "!");
                                    View.dialog("Healing: " + newPO.healing );
                                    if (PO != null) {
                                        View.dialog("Current healing: " + PO.healing);
                                    }else{
                                    System.out.println("You currently have no potion");
                                    }

                                    View.dialog("Press \"Y\" to keep or \"T\" to throw");

                                    if (sc.nextLine().equalsIgnoreCase("Y")){
                                        update("DELETE FROM potion WHERE player_id = " + P.id);
                                        update("INSERT INTO potion (name, player_id, healing) VALUES ('" + newPO.name + "'," + P.id + ", " + newPO.healing + ")");
                                        ResultSet gw = st.executeQuery("SELECT * FROM potion WHERE player_id = " +  P.id);
                                        while (gw.next()){
                                            tempID = gw.getInt("id");
                                            String pName = gw.getString("name");
                                            int healing = gw.getInt("healing");
                                            PO = new Potion(P.id, pName, healing);
                                            System.out.println(tempID + " " + pName + " " + healing);



                                        }
                                        update("UPDATE players SET potion = " + tempID + " WHERE id = " + P.id);
                                    }
                                }
                            }


                            fighting = false;

                        }else{
                            assert A != null;
                            int hit = (M.dmg - A.defence);
                            if (hit < 0){
                                hit = 0;
                            }
                            P.HP = P.HP - hit;
                            A.wear--;
                            if (A.wear < 1){
                                P.armor = 0;
                                A.defence = 0;
                            }
                            if (P.HP <= 0){

                                assert stats != null;
                                stats.deaths++;
                                if (currentRoom > stats.maxRoom){
                                    stats.maxRoom = currentRoom;
                                }
                                View.dialog("You're dead, GG!");
                                update("UPDATE players SET HP = 100 WHERE id = " + P.id);
                                updateStats(stats);
                                System.exit(0);
                            }
                            update("UPDATE players SET HP = " + P.HP + " WHERE id = " + P.id);
                        }

                    }else if (nextLn.equalsIgnoreCase("reset")){
                        currentRoom = 1;
                        P.HP = 100;
                        fighting = false;
                    }else if (nextLn.equalsIgnoreCase("quit")) {
                        System.exit(0);
                    }else if (nextLn.equalsIgnoreCase("Potion")){
                        if (P.potion != 0) {
                            if (PO != null) {
                                P.HP += PO.healing;
                            }
                            if (P.HP > 100){
                                P.HP = 100;
                            }
                            update("DELETE FROM potion WHERE player_id = " + P.id);
                            PO.healing = 0;
                        }
                    }
                    assert stats != null;
                    updateStats(stats);

                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method that decides what weapon you get.
     * @param nr Generated number that decides which weapon you get.
     * @param id Playerid
     * @return
     */
    private Weapon getWeapon(int nr, int id){
        if (55 <= nr && nr <= 59){
            return new Weapon(id,"Sword", 18, 30);
        }else if (60 <= nr && nr <= 63){
            return new Weapon(id,"Sword", 20, 30);
        }else if (64 <= nr && nr <= 66){
            return new Weapon(id,"Sword", 21, 40);
        }else if (67 <= nr && nr <= 68){
            return new Weapon(id,"Sword", 26, 25);
        }else if (69 == nr){
            return new Weapon(id,"Sword", 35, 20);
        }else{
            return null;
        }
    }

    /**
     * Method that decides what armor you get.
     * @param nr Generated number that decides which armor you get.
     * @param id Playerid
     * @return
     */
    private Armor getArmor(int nr, int id){
        if (70 <= nr && nr <= 74){//Armor
            return new Armor(id, "Armor", 4, 20);
        }else if (75 <= nr && nr <= 78){
            return new Armor(id, "Armor", 6, 20);
        }else if (79 <= nr && nr <= 81){
            return new Armor(id, "Armor", 7, 30);
        }else if (82 <= nr && nr <= 83){
            return new Armor(id, "Armor", 10, 25);
        }else if (84 == nr){
            return new Armor(id, "Armor", 18, 20);
        }else{
            return null;
        }
    }

    /**
     * Method that decides what potion you get.
     * @param nr Generated number that decides which potion you get.
     * @param id Playerid
     * @return
     */
    private Potion getPotion(int nr, int id){
        if (85 <= nr && nr <= 89){//Potions
            return new Potion(id, "Potion", 20);
        }else if (90 <= nr && nr <= 93){
            return new Potion(id, "Potion", 35);
        }else if (94 <= nr && nr <= 96){
            return new Potion(id, "Potion", 50);
        }else if (97 <= nr && nr <= 98){
            return new Potion(id, "Potion", 75);
        }else if (99 <= nr && nr <= 100){
            return new Potion(id, "Potion", 100);
        }else{
            return null;
        }
    }

    public Model() {
        Player P = getStartInfo();

        playGame(P);


    }

}