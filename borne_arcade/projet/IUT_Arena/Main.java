/**
 * IUT Arena - Jeu de combat en arène
 * R6.06 - Maintenance applicative
 * 
 * @author CHAGOT - DOUILLY
 * Date: 24/03/26
 * Classe: BUT 3 APP
 */

public class Main {
    public static void main(String[] args) {
        IUTArena game = new IUTArena();

        while (true) {
            try {
                Thread.sleep(16);
            } catch (Exception e) {
                System.out.println(e);
            }
            game.update();
        }
    }
}
