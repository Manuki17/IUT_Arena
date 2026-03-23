import MG2D.Fenetre;
import MG2D.geometrie.*;
import MG2D.geometrie.Point;
import MG2D.geometrie.Texture;
import MG2D.geometrie.Rectangle;
import MG2D.geometrie.Texte;
import MG2D.geometrie.Couleur;

import java.awt.Font;
import java.io.InputStream;

public class Menu {
    //CONSTANTES
    public static final int STATUTMENU=0;
    public final static int BOUTON1JOUEUR=1;
    public final static int BOUTON2JOUEURS=2;
    public final static int BOUTONEXIT=3;

    //ATTRIBUTS
    private int choix;

    private Fenetre f;

    private Texture bg;
    private Texture title;


    //ELEMENTS DU MENU
    private Rectangle fondTexte;
    private Rectangle contourFondTexte;
    private int largeurFondTexte=300;
    private int hauteurFondTexte=225;
    private int margeFondTexte=10;
    private Texte texte1P;
    private Texte texte2P;
    private Texte texteExit;
    private int yTexte1P;
    private int yTexte2P;
    private int yTexteExit;
    private int espaceElemMenu;

    private Font font;



    public Menu(Fenetre f){
        this.f=f;
        this.choix=this.BOUTON1JOUEUR;

        // Fond d'écran - adapté aux dimensions de la fenêtre
        this.bg=new Texture("img/menu/bg.png",new Point(0,0));
        this.bg.setLargeur(f.getP().getWidth());
        this.bg.setHauteur(f.getP().getHeight());
        f.ajouter(bg);

        this.title=new Texture("img/menu/title.png",new Point(0,0));
        // Redimensionner le titre proportionnellement (30% de la largeur de l'écran)
        int titleWidth = (int)(f.getP().getWidth() * 0.3);
        int titleHeight = (int)(titleWidth * ((double)this.title.getHauteur() / this.title.getLargeur()));
        this.title.setLargeur(titleWidth);
        this.title.setHauteur(titleHeight);
        // Positionner le titre en haut centré (à 65% de la hauteur de l'écran)
        this.title.setA(new Point(f.getMilieu().getX()-this.title.getLargeur()/2, (int)(f.getP().getHeight() * 0.65)));
        f.ajouter(title);

        // Adapter les positions et tailles du menu aux dimensions de la fenêtre
        this.contourFondTexte = new Rectangle(Couleur.BLANC,new Point(f.getMilieu().getX()-(largeurFondTexte+margeFondTexte)/2,(int)(f.getP().getHeight() * 0.15)-margeFondTexte/2),largeurFondTexte+margeFondTexte,hauteurFondTexte+margeFondTexte,true);
        this.fondTexte = new Rectangle(Couleur.NOIR,new Point(f.getMilieu().getX()-largeurFondTexte/2,(int)(f.getP().getHeight() * 0.15)),largeurFondTexte,hauteurFondTexte,true);
        f.ajouter(contourFondTexte);
        f.ajouter(fondTexte);

        this.font = null;
        try{
            String nomFont = "fonts/Spencer.ttf";
            InputStream is = getClass().getResourceAsStream("/" + nomFont);
            if (is == null) {
                is = getClass().getResourceAsStream(nomFont);
            }
            this.font = Font.createFont(Font.TRUETYPE_FONT, is);
            // Adapter la taille de la police à la hauteur de l'écran
            float fontSize = (float)(f.getP().getHeight() * 0.05);
            this.font = this.font.deriveFont(fontSize);
        }catch (Exception e) {
            System.err.println("Erreur chargement font: " + e.getMessage());
            // Adapter la taille par défaut aussi
            float fontSize = (float)(f.getP().getHeight() * 0.05);
            this.font = new Font("Arial", Font.BOLD, (int)fontSize);
        }

        this.espaceElemMenu = (int)(f.getP().getHeight() * 0.08);
        this.yTexte1P = (int)(f.getP().getHeight() * 0.35);
        this.texte1P = new Texte("1 PLAYER",this.font,new Point(this.f.getMilieu().getX(),this.yTexte1P));
        this.texte1P.setCouleur(Couleur.ORANGE);
        this.f.ajouter(this.texte1P);

        this.yTexte2P = yTexte1P - espaceElemMenu;
        this.texte2P = new Texte("2 PLAYERS",this.font,new Point(this.f.getMilieu().getX(),this.yTexte2P));
        this.texte2P.setCouleur(Couleur.BLEU);
        this.f.ajouter(this.texte2P);

        this.yTexteExit = yTexte2P - espaceElemMenu;
        this.texteExit = new Texte("EXIT",this.font,new Point(this.f.getMilieu().getX(),this.yTexteExit));
        this.texteExit.setCouleur(Couleur.BLEU);
        this.f.ajouter(this.texteExit);

        this.f.rafraichir();
    }

    public int prochaineFrame(int direction, int boutonEnfonce){
        int res=this.STATUTMENU;

        //Gestion de la direction enfoncée
        if(direction!=0){
            switch(direction){
                case 1:
                    if(choix>this.BOUTON1JOUEUR)
                        choix--;
                    break;
                case -1:
                    if(choix<this.BOUTONEXIT)
                        choix++;
                    break;
            }

            switch(choix){
                case 1:
                    this.texte1P.setCouleur(Couleur.ORANGE);
                    this.texte2P.setCouleur(Couleur.BLEU);
                    this.texteExit.setCouleur(Couleur.BLEU);
                    break;
                case 2:
                    this.texte1P.setCouleur(Couleur.BLEU);
                    this.texte2P.setCouleur(Couleur.ORANGE);
                    this.texteExit.setCouleur(Couleur.BLEU);
                    break;

                case 3:
                    this.texte1P.setCouleur(Couleur.BLEU);
                    this.texte2P.setCouleur(Couleur.BLEU);
                    this.texteExit.setCouleur(Couleur.ORANGE);
                    break;
            }
        }

        //Si le bouton d'action est enfoncé, on envoie au menu l'information du bouton enfoncé
        if(boutonEnfonce==1){
            res=choix;
        }

        f.rafraichir();
        return res;
    }

}
