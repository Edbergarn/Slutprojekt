public class Stats {
    int PlayerID;
    int rounds;
    int kills;
    int deaths;
    int level;
    int maxRoom;
    int foundSwords;
    int foundArmors;
    int foundPotions;
    int destroyedItems;

    public Stats(int PlayerID, int rounds, int kills, int deaths, int level, int maxRoom, int foundSwords, int foundArmors, int foundPotions, int destroyedItems){
        this.PlayerID = PlayerID;
        this.rounds = rounds;
        this.kills = kills;
        this.deaths = deaths;
        this.level = level;
        this.maxRoom = maxRoom;
        this.foundSwords = foundSwords;
        this.foundArmors = foundArmors;
        this.foundPotions = foundPotions;
        this.destroyedItems = destroyedItems;
    }
}
