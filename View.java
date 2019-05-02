import java.util.Scanner;

public class View {

    public static String getUsername(){
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter username");
        String currentUser = sc.nextLine();
        return currentUser;
    }
    public static String getNewUsername(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Type \"Yes \"to create a new Player, or \"No\" to try again");
        if (sc.nextLine().equalsIgnoreCase("Yes")) {
            System.out.println("Enter your new Username");
            String Username = sc.nextLine();
            return Username;
        } else if(!sc.nextLine().equalsIgnoreCase("No")){
            getNewUsername();
            return "try again";
        }else {
                System.out.println("Men spela inte då");
                System.exit(0);
                return null;
            }
        }

    public static void dialog(String a){
        System.out.println(a);
        return;
    }
}
