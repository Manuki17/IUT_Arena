/**
 * IUT Arena - Jeu de combat en arène
 * R6.06 - Maintenance applicative
 * 
 * @author CHAGOT - DOUILLY
 * Date: 24/03/26
 * Classe: BUT 3 APP
 */

import MG2D.*;
import MG2D.geometrie.*;

public class PowerUp {

    public static final int HEAL = 0;
    public static final int SPEED = 1;
    public static final int CHARGE = 2;

    private double x, y;
    private int type;
    private int lifetime;
    private int timer;

    private Cercle glow;
    private Cercle body;
    private Cercle icon;

    private static final int RADIUS = 14;
    private static final int MAX_LIFETIME = 720; // ~12 secondes à 60fps

    public PowerUp(double x, double y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.lifetime = MAX_LIFETIME;
        this.timer = 0;

        Couleur glowCol, bodyCol, iconCol;
        switch (type) {
            case HEAL:
                glowCol = new Couleur(40, 180, 60);
                bodyCol = new Couleur(60, 220, 80);
                iconCol = new Couleur(200, 255, 200);
                break;
            case SPEED:
                glowCol = new Couleur(30, 160, 200);
                bodyCol = new Couleur(50, 200, 240);
                iconCol = new Couleur(180, 240, 255);
                break;
            case CHARGE:
                glowCol = new Couleur(200, 170, 30);
                bodyCol = new Couleur(240, 210, 50);
                iconCol = new Couleur(255, 245, 180);
                break;
            default:
                glowCol = new Couleur(150, 150, 150);
                bodyCol = new Couleur(200, 200, 200);
                iconCol = Couleur.BLANC;
        }

        glow = new Cercle(glowCol, new Point((int) x, (int) y), RADIUS + 6, true);
        body = new Cercle(bodyCol, new Point((int) x, (int) y), RADIUS, true);
        icon = new Cercle(iconCol, new Point((int) x, (int) y), 5, true);
    }

    public void addToWindow(Fenetre f) {
        f.ajouter(glow);
        f.ajouter(body);
        f.ajouter(icon);
    }

    public void removeFromWindow(Fenetre f) {
        f.supprimer(glow);
        f.supprimer(body);
        f.supprimer(icon);
    }

    public void update() {
        timer++;
        lifetime--;

        // Lueur pulsante
        double pulse = 0.6 + 0.4 * Math.sin(timer * 0.1);
        int glowRadius = RADIUS + 4 + (int)(6 * pulse);
        glow.setRayon(glowRadius);

        // Corps flottant
        int bobY = (int)(y + Math.sin(timer * 0.08) * 3);
        body.setO(new Point((int) x, bobY));
        icon.setO(new Point((int) x, bobY));

        // Estompe la lueur quand va expirer
        if (lifetime < 120) {
            // Clignote plus vite quand va disparaître
            if ((lifetime / 8) % 2 == 0) {
                glow.setRayon(0);
                body.setRayon(0);
                icon.setRayon(0);
            } else {
                body.setRayon(RADIUS);
                icon.setRayon(5);
            }
        }
    }

    public boolean isExpired() {
        return lifetime <= 0;
    }

    public boolean isPickedUpBy(double fx, double fy, int fRadius) {
        double dx = x - fx;
        double dy = y - fy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < (RADIUS + fRadius);
    }

    public int getType() { return type; }
    public double getX() { return x; }
    public double getY() { return y; }
}
