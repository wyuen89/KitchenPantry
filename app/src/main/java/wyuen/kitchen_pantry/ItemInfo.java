package wyuen.kitchen_pantry;

public class ItemInfo {
    private String name;
    private int id;

    public ItemInfo(int inID, String inName){
        name = inName;
        id = inID;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
