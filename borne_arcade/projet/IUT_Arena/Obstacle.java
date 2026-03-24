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

public class Obstacle {

    private double x, y;
    private int radius;

    private Cercle shadow;
    private Cercle body;
    private Cercle outline;
    private Cercle highlight;

    public Obstacle(double x, double y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;

        shadow = new Cercle(new Couleur(10, 10, 16),
            new Point((int)x + 3, (int)y - 4), radius + 2, true);
        body = new Cercle(new Couleur(24, 26, 36),
            new Point((int)x, (int)y), radius, true);
        outline = new Cercle(new Couleur(80, 85, 100),
            new Point((int)x, (int)y), radius, false);
        highlight = new Cercle(new Couleur(50, 54, 68),
            new Point((int)x, (int)y), radius - 4, true);
    }

    public void addToWindow(Fenetre f) {
        f.ajouter(shadow);
        f.ajouter(body);
        f.ajouter(highlight);
        f.ajouter(outline);
    }

    public void removeFromWindow(Fenetre f) {
        f.supprimer(shadow);
        f.supprimer(body);
        f.supprimer(highlight);
        f.supprimer(outline);
    }

    public void setThemeColors(Couleur bodyCol, Couleur outlineCol, Couleur highlightCol) {
        body.setCouleur(bodyCol);
        outline.setCouleur(outlineCol);
        highlight.setCouleur(highlightCol);
    }

    public boolean collidesWithFighter(double fx, double fy, int fRadius) {
        double dx = fx - x;
        double dy = fy - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < (radius + fRadius);
    }

    public double getOverlap(double fx, double fy, int fRadius) {
        double dx = fx - x;
        double dy = fy - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return (radius + fRadius) - dist;
    }

    public double angleTo(double fx, double fy) {
        return Math.atan2(fy - y, fx - x);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getRadius() { return radius; }
}
