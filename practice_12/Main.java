package practice_12;

public class Main {
    public static void main(String[] args) {
        System.out.println(new Game("13579", "11110").play());
        System.out.println(new Game("12345", "67890").play());
        System.out.println(new Game("13579", "24680").play());
    }
}