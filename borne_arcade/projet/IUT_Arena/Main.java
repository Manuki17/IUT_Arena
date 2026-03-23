import MG2D.FenetrePleinEcran;

public class Main {
    static final int pasTemps = 60;
    static final int TAILLEX = 1280;
    static final int TAILLEY = 1024;

    public static void main(String[] args) {
        FenetrePleinEcran f = new FenetrePleinEcran("IUT Arena");
        f.setVisible(true);

        Keyboard clavier = new Keyboard();
        f.addKeyListener(clavier);

        boolean fermetureJeu = false;
        int statut = Menu.STATUTMENU;

        Menu m = new Menu(f);

        int direction1 = 0;
        int boutonEnfonce1 = 0;

        while (!fermetureJeu) {
            direction1 = 0;
            if (clavier.getJoyJ1HautTape()) {
                direction1 = 1;
                System.out.println("Haut détecté");
            }
            if (clavier.getJoyJ1BasTape()) {
                direction1 = -1;
                System.out.println("Bas détecté");
            }

            boutonEnfonce1 = 0;
            if (clavier.getBoutonJ1ATape()) {
                boutonEnfonce1 = 1;
                System.out.println("Bouton A (F) détecté");
            }

            switch (statut) {
                case Menu.STATUTMENU:
                    int nouveauStatut = m.prochaineFrame(direction1, boutonEnfonce1);
                    if (nouveauStatut != statut) {
                        System.out.println("Changement de statut: " + statut + " -> " + nouveauStatut);
                    }
                    statut = nouveauStatut;
                    break;
                case Menu.BOUTON1JOUEUR:
                    System.out.println("Lancement partie 1 joueur");
                    statut = Menu.STATUTMENU;
                    m = new Menu(f);
                    break;
                case Menu.BOUTON2JOUEURS:
                    System.out.println("Lancement partie 2 joueurs");
                    statut = Menu.STATUTMENU;
                    m = new Menu(f);
                    break;
                case Menu.BOUTONEXIT:
                    System.out.println("Fermeture!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Statut inconnu! Statut : " + statut);
                    System.exit(1);
            }

            try {
                Thread.sleep(pasTemps);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}