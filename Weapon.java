public class Weapon {
    int PlayerID;
    int type;
    String name;
    int damage;
    int wear;

    public Weapon(int PlayerID, String name, int damage, int wear, int type){
        this.PlayerID = PlayerID;
        this.type = type;
        this.name = name;
        this.damage = damage;
        this.wear = wear;
    }

}
