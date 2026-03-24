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
import java.util.ArrayList;

public class Fighter {

    // Position (nombres décimaux pour les mouvements fluides)
    private double x, y;
    // Vitesse
    private double vx, vy;
    // Angle de direction en radians
    private double angle;

    // Caractéristiques
    private int radius;
    private double baseSpeed;
    private double weight;
    private Couleur baseColor;
    private Couleur brightColor;
    private boolean isAI;
    private int playerId; // 1 or 2, 0 for AI

    // État
    public static final int IDLE = 0;
    public static final int MOVING = 1;
    public static final int WINDUP = 2;
    public static final int DASHING = 3;
    public static final int DODGING = 4;
    public static final int STUNNED = 5;
    public static final int FALLING = 6;
    public static final int DEAD = 7;

    private int state;
    private int stateTimer;
    private int chargeCooldown;
    private int dodgeCooldown;
    private int invulnFrames;
    private int lives;
    private boolean eliminated;
    private int flashTimer;
    private int speedBoostTimer;

    // Système de points de vie (pour le mode vague de dégâts)
    private int hp;
    private int maxHp;

    // Variables pour l'IA
    private double aggression;
    private double aiSpeedMult;
    private int aiDecisionTimer;
    private int aiDecisionInterval;
    private double aiDodgeChance;
    private double aiMoveAngle;
    private boolean aiWantsCharge;

    // Éléments visuels
    private Cercle body;
    private Cercle dirDot;
    private Cercle shadow;
    private Cercle hpRing; // anneau visible pour les ennemis avec PV

    // Constantes
    private static final double FRICTION = 0.88;
    private static final double VEL_THRESHOLD = 0.25;
    private static final int WINDUP_FRAMES = 18;
    private static final int DASH_FRAMES = 10;
    private static final double DASH_SPEED = 17.0;
    private static final int CHARGE_CD = 55;
    private static final int DODGE_FRAMES = 9;
    private static final double DODGE_SPEED = 14.0;
    private static final int DODGE_CD = 42;
    private static final int STUN_FRAMES = 12;
    private static final int FALL_FRAMES = 35;
    private static final double KNOCKBACK_BASE = 22.0;
    private static final int INVULN_ON_SPAWN = 90;

    // Constructeur pour un joueur
    public Fighter(double x, double y, int radius, Couleur color, Couleur bright, int playerId) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.angle = 0;
        this.radius = radius;
        this.baseSpeed = 4.5;
        this.weight = 1.0;
        this.baseColor = color;
        this.brightColor = bright;
        this.isAI = false;
        this.playerId = playerId;
        this.state = IDLE;
        this.stateTimer = 0;
        this.chargeCooldown = 0;
        this.dodgeCooldown = 0;
        this.invulnFrames = INVULN_ON_SPAWN;
        this.lives = 3;
        this.eliminated = false;
        this.flashTimer = 0;
        this.speedBoostTimer = 0;
        this.hp = 0;
        this.maxHp = 0;
        createVisuals();
    }

    // Constructeur pour l'IA
    public Fighter(double x, double y, int radius, Couleur color, Couleur bright,
                   double aggression, double speedMult, double dodgeChance, double weight, int decisionInterval) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.angle = Math.random() * Math.PI * 2;
        this.radius = radius;
        this.baseSpeed = 4.5;
        this.weight = weight;
        this.baseColor = color;
        this.brightColor = bright;
        this.isAI = true;
        this.playerId = 0;
        this.state = IDLE;
        this.stateTimer = 0;
        this.chargeCooldown = 30;
        this.dodgeCooldown = 0;
        this.invulnFrames = 30;
        this.lives = 1;
        this.eliminated = false;
        this.flashTimer = 0;
        this.speedBoostTimer = 0;
        this.hp = 0;
        this.maxHp = 0;
        this.aggression = aggression;
        this.aiSpeedMult = speedMult;
        this.aiDodgeChance = dodgeChance;
        this.aiDecisionTimer = (int)(Math.random() * 20);
        this.aiDecisionInterval = decisionInterval;
        this.aiMoveAngle = 0;
        this.aiWantsCharge = false;
        createVisuals();
    }

    private void createVisuals() {
        shadow = new Cercle(new Couleur(20, 20, 30), new Point((int)x - 4, (int)y - 6), radius - 2, true);
        body = new Cercle(baseColor, new Point((int)x, (int)y), radius, true);
        int dotDist = radius - 6;
        int dotX = (int)(x + Math.cos(angle) * dotDist);
        int dotY = (int)(y + Math.sin(angle) * dotDist);
        dirDot = new Cercle(Couleur.BLANC, new Point(dotX, dotY), 5, true);
        hpRing = null;
    }

    public void addToWindow(Fenetre f) {
        f.ajouter(shadow);
        f.ajouter(body);
        f.ajouter(dirDot);
        if (hpRing != null) f.ajouter(hpRing);
    }

    public void removeFromWindow(Fenetre f) {
        f.supprimer(shadow);
        f.supprimer(body);
        f.supprimer(dirDot);
        if (hpRing != null) f.supprimer(hpRing);
    }

    // Mise à jour principale appelée à chaque image
    public void update() {
        if (eliminated) return;

        // Temps d'attente
        if (chargeCooldown > 0) chargeCooldown--;
        if (dodgeCooldown > 0) dodgeCooldown--;
        if (invulnFrames > 0) invulnFrames--;
        if (flashTimer > 0) flashTimer--;
        if (speedBoostTimer > 0) speedBoostTimer--;

        switch (state) {
            case IDLE:
            case MOVING:
                // Applique la friction
                vx *= FRICTION;
                vy *= FRICTION;
                if (Math.abs(vx) < VEL_THRESHOLD) vx = 0;
                if (Math.abs(vy) < VEL_THRESHOLD) vy = 0;
                break;

            case WINDUP:
                // Ralentit pendant la préparation
                vx *= 0.7;
                vy *= 0.7;
                stateTimer--;
                if (stateTimer <= 0) {
                    // Lance le dash
                    state = DASHING;
                    stateTimer = DASH_FRAMES;
                    vx = Math.cos(angle) * DASH_SPEED;
                    vy = Math.sin(angle) * DASH_SPEED;
                }
                break;

            case DASHING:
                stateTimer--;
                // Légère friction pendant le dash
                vx *= 0.97;
                vy *= 0.97;
                if (stateTimer <= 0) {
                    state = IDLE;
                    chargeCooldown = CHARGE_CD;
                }
                break;

            case DODGING:
                stateTimer--;
                vx *= 0.95;
                vy *= 0.95;
                if (stateTimer <= 0) {
                    state = IDLE;
                    dodgeCooldown = DODGE_CD;
                }
                break;

            case STUNNED:
                vx *= FRICTION;
                vy *= FRICTION;
                stateTimer--;
                if (stateTimer <= 0) {
                    state = IDLE;
                }
                break;

            case FALLING:
                stateTimer--;
                // S'éloigne du centre de l'arène
                double speed = 3.0 + (FALL_FRAMES - stateTimer) * 0.5;
                vx = Math.cos(angle) * speed;
                vy = Math.sin(angle) * speed;
                if (stateTimer <= 0) {
                    state = DEAD;
                    eliminated = true;
                }
                break;

            case DEAD:
                return;
        }

        // Applique la vitesse à la position
        x += vx;
        y += vy;

        // Met à jour les éléments visuels
        updateVisuals();
    }

    public void handlePlayerInput(ClavierBorneArcade clavier) {
        if (eliminated || state == FALLING || state == DEAD || state == STUNNED) return;

        double moveX = 0, moveY = 0;

        if (playerId == 1) {
            if (clavier.getJoyJ1GaucheEnfoncee()) moveX -= 1;
            if (clavier.getJoyJ1DroiteEnfoncee()) moveX += 1;
            if (clavier.getJoyJ1HautEnfoncee()) moveY += 1;
            if (clavier.getJoyJ1BasEnfoncee()) moveY -= 1;

            if (clavier.getBoutonJ1AEnfoncee() && state != WINDUP && state != DASHING && state != DODGING) {
                startCharge();
            }
            if (clavier.getBoutonJ1BEnfoncee() && state != WINDUP && state != DASHING && state != DODGING) {
                startDodge();
            }
        } else if (playerId == 2) {
            if (clavier.getJoyJ2GaucheEnfoncee()) moveX -= 1;
            if (clavier.getJoyJ2DroiteEnfoncee()) moveX += 1;
            if (clavier.getJoyJ2HautEnfoncee()) moveY += 1;
            if (clavier.getJoyJ2BasEnfoncee()) moveY -= 1;

            if (clavier.getBoutonJ2AEnfoncee() && state != WINDUP && state != DASHING && state != DODGING) {
                startCharge();
            }
            if (clavier.getBoutonJ2BEnfoncee() && state != WINDUP && state != DASHING && state != DODGING) {
                startDodge();
            }
        }

        // Applique le mouvement si pas dans un état spécial
        if (state == IDLE || state == MOVING) {
            if (moveX != 0 || moveY != 0) {
                double len = Math.sqrt(moveX * moveX + moveY * moveY);
                moveX /= len;
                moveY /= len;
                double spd = baseSpeed * (speedBoostTimer > 0 ? 1.5 : 1.0);
                vx = moveX * spd;
                vy = moveY * spd;
                angle = Math.atan2(moveY, moveX);
                state = MOVING;
            } else {
                state = IDLE;
            }
        }
    }

    public void updateAI(ArrayList<Fighter> targets, double arenaX, double arenaY, double arenaRadius) {
        if (eliminated || state == FALLING || state == DEAD) return;

        aiDecisionTimer--;
        if (aiDecisionTimer <= 0) {
            aiDecisionTimer = aiDecisionInterval + (int)(Math.random() * 10);
            makeAIDecision(targets, arenaX, arenaY, arenaRadius);
        }

        // Exécute le mouvement de l'IA
        if (state == IDLE || state == MOVING) {
            if (aiWantsCharge && state != WINDUP && state != DASHING && state != DODGING) {
                startCharge();
                aiWantsCharge = false;
            } else {
                double moveX = Math.cos(aiMoveAngle);
                double moveY = Math.sin(aiMoveAngle);
                double spd = baseSpeed * aiSpeedMult;
                vx += moveX * spd * 0.35;
                vy += moveY * spd * 0.35;
                double vel = Math.sqrt(vx * vx + vy * vy);
                if (vel > spd) {
                    vx = (vx / vel) * spd;
                    vy = (vy / vel) * spd;
                }
                angle = Math.atan2(vy, vx);
                state = MOVING;
            }
        }
    }

    private void makeAIDecision(ArrayList<Fighter> targets, double arenaX, double arenaY, double arenaRadius) {
        // Cherche la cible la plus proche
        Fighter nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Fighter t : targets) {
            if (t == this || t.isEliminated()) continue;
            double dist = distanceTo(t);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = t;
            }
        }

        if (nearest == null) return;

        double angleToTarget = Math.atan2(nearest.y - y, nearest.x - x);

        // Vérifie si on doit esquiver (la cible dash vers nous)
        if (nearest.isDashing() && nearestDist < 180) {
            if (Math.random() < aiDodgeChance && state != DODGING && state != DASHING) {
                // Définit l'angle loin du dasher, puis esquive
                angle = angleToTarget + Math.PI * 0.5 * (Math.random() > 0.5 ? 1 : -1);
                startDodge();
                return;
            }
        }

        // Vérifie la distance au centre de l'arène (évite de tomber)
        double distToCenter = Math.sqrt((x - arenaX) * (x - arenaX) + (y - arenaY) * (y - arenaY));
        if (distToCenter > arenaRadius * 0.75) {
            // Retourne vers le centre
            aiMoveAngle = Math.atan2(arenaY - y, arenaX - x) + (Math.random() - 0.5) * 0.5;
            aiWantsCharge = false;
            return;
        }

        // Courte distance : envisage de charger
        if (nearestDist < 160) {
            if (Math.random() < aggression && chargeCooldown <= 0) {
                angle = angleToTarget + (Math.random() - 0.5) * 0.3;
                aiWantsCharge = true;
                return;
            }
        }

        // Par défaut : se déplace vers la cible avec aléatoire
        aiMoveAngle = angleToTarget + (Math.random() - 0.5) * 1.2;
        aiWantsCharge = false;
    }

    public void startCharge() {
        if (chargeCooldown > 0 || state == DASHING || state == WINDUP || state == FALLING) return;
        state = WINDUP;
        stateTimer = WINDUP_FRAMES;
    }

    public void startDodge() {
        if (dodgeCooldown > 0 || state == DASHING || state == DODGING || state == FALLING) return;
        state = DODGING;
        stateTimer = DODGE_FRAMES;
        // Esquive perpendiculaire à la direction
        double dodgeAngle = angle + Math.PI * 0.5;
        vx = Math.cos(dodgeAngle) * DODGE_SPEED;
        vy = Math.sin(dodgeAngle) * DODGE_SPEED;
    }

    public void applyKnockback(double fromX, double fromY, double force) {
        if (invulnFrames > 0 || state == DODGING || state == FALLING || eliminated) return;
        double ang = Math.atan2(y - fromY, x - fromX);
        double actualForce = force / weight;
        vx += Math.cos(ang) * actualForce;
        vy += Math.sin(ang) * actualForce;
        state = STUNNED;
        stateTimer = STUN_FRAMES;
        flashTimer = 15;
    }

    // Appelé sur le DASHER quand il touche quelqu'un - rebondit
    public void onDashHit(double targetX, double targetY) {
        double ang = Math.atan2(y - targetY, x - targetX);
        vx = Math.cos(ang) * 8.0;
        vy = Math.sin(ang) * 8.0;
        state = IDLE;
        stateTimer = 0;
        chargeCooldown = CHARGE_CD;
    }

    public void pushAway(double px, double py) {
        if (state == FALLING || state == DEAD || eliminated) return;
        x += px;
        y += py;
    }

    // Vérifie si le combattant est hors de l'arène, retourne vrai si commence à tomber
    public boolean checkBoundary(double arenaX, double arenaY, double arenaRadius) {
        if (state == FALLING || state == DEAD || eliminated) return false;
        double dist = Math.sqrt((x - arenaX) * (x - arenaX) + (y - arenaY) * (y - arenaY));
        if (dist > arenaRadius) {
            state = FALLING;
            stateTimer = FALL_FRAMES;
            angle = Math.atan2(y - arenaY, x - arenaX);
            return true;
        }
        return false;
    }

    public boolean collidesWith(Fighter other) {
        if (eliminated || other.eliminated) return false;
        if (state == FALLING || state == DEAD) return false;
        if (other.state == FALLING || other.state == DEAD) return false;
        double dist = distanceTo(other);
        return dist < (radius + other.radius);
    }

    public double distanceTo(Fighter other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void updateVisuals() {
        if (eliminated) return;

        int ix = (int) x;
        int iy = (int) y;

        // Ombre
        shadow.setO(new Point(ix - 4, iy - 6));

        // Corps
        body.setO(new Point(ix, iy));

        // L'anneau de PV suit le combattant
        if (hpRing != null) {
            if (eliminated || state == DEAD) {
                hpRing.setRayon(0);
            } else if (state == FALLING) {
                int shrink = Math.max(2, (radius + 4) * stateTimer / FALL_FRAMES);
                hpRing.setO(new Point(ix, iy));
                hpRing.setRayon(shrink);
            } else {
                hpRing.setO(new Point(ix, iy));
                double hpRatio = maxHp > 0 ? (double)hp / maxHp : 1.0;
                int rr = (int)(brightColor.getRed() * hpRatio + 255 * (1.0 - hpRatio));
                int gg = (int)(brightColor.getGreen() * hpRatio);
                int bb = (int)(brightColor.getBlue() * hpRatio * 0.5);
                hpRing.setCouleur(new Couleur(
                    Math.min(255, rr), Math.max(0, gg), Math.max(0, bb)));
                hpRing.setRayon(radius + 4);
            }
        }

        // Point de direction
        int dotDist = radius - 5;
        int dotX = (int)(x + Math.cos(angle) * dotDist);
        int dotY = (int)(y + Math.sin(angle) * dotDist);
        dirDot.setO(new Point(dotX, dotY));

        // Feedback visuel de l'état
        if (state == FALLING) {
            int shrinkRadius = Math.max(2, radius * stateTimer / FALL_FRAMES);
            body.setRayon(shrinkRadius);
            shadow.setRayon(Math.max(1, shrinkRadius - 2));
            dirDot.setRayon(Math.max(1, 5 * stateTimer / FALL_FRAMES));
        } else if (state == WINDUP) {
            double chargeProgress = 1.0 - (double)stateTimer / WINDUP_FRAMES;
            int pulseRadius = radius + (int)(6 * chargeProgress) + (stateTimer % 3 == 0 ? 2 : 0);
            body.setRayon(pulseRadius);
            int r2 = Math.min(255, brightColor.getRed() + (int)(60 * chargeProgress));
            int g2 = Math.min(255, brightColor.getGreen() + (int)(40 * chargeProgress));
            int b2 = Math.min(255, brightColor.getBlue() + (int)(40 * chargeProgress));
            body.setCouleur(new Couleur(r2, g2, b2));
            dirDot.setRayon(7);
        } else if (state == DASHING) {
            body.setRayon(radius + 6);
            body.setCouleur(brightColor);
            dirDot.setRayon(7);
        } else if (state == STUNNED) {
            int shakeX = (int)((Math.random() - 0.5) * 6);
            int shakeY = (int)((Math.random() - 0.5) * 6);
            body.setO(new Point(ix + shakeX, iy + shakeY));
            body.setRayon(radius);
            body.setCouleur(Couleur.BLANC);
        } else if (state == DODGING) {
            body.setRayon(radius - 4);
            body.setCouleur(new Couleur(
                Math.min(255, baseColor.getRed() + 100),
                Math.min(255, baseColor.getGreen() + 100),
                Math.min(255, baseColor.getBlue() + 100)));
            dirDot.setRayon(3);
        } else {
            body.setRayon(radius);
            dirDot.setRayon(5);
            if (flashTimer > 0 && flashTimer % 4 < 2) {
                body.setCouleur(Couleur.BLANC);
            } else if (maxHp > 0 && hp < maxHp && hp > 0) {
                // Teinte le corps selon le ratio de PV (devient plus rouge quand les PV baissent)
                double hpRatio = (double)hp / maxHp;
                int rr = (int)(baseColor.getRed() + (255 - baseColor.getRed()) * (1.0 - hpRatio) * 0.5);
                int gg = (int)(baseColor.getGreen() * hpRatio);
                int bb = (int)(baseColor.getBlue() * hpRatio);
                body.setCouleur(new Couleur(Math.min(255, rr), Math.max(0, gg), Math.max(0, bb)));
            } else if (speedBoostTimer > 0 && speedBoostTimer % 6 < 3) {
                body.setCouleur(new Couleur(
                    Math.min(255, baseColor.getRed() + 30),
                    Math.min(255, baseColor.getGreen() + 80),
                    Math.min(255, baseColor.getBlue() + 100)));
            } else if (invulnFrames > 0 && invulnFrames % 6 < 3) {
                body.setCouleur(new Couleur(
                    Math.min(255, baseColor.getRed() + 50),
                    Math.min(255, baseColor.getGreen() + 50),
                    Math.min(255, baseColor.getBlue() + 50)));
            } else {
                body.setCouleur(baseColor);
            }
        }
    }

    public void respawn(double nx, double ny) {
        x = nx;
        y = ny;
        vx = 0;
        vy = 0;
        state = IDLE;
        stateTimer = 0;
        chargeCooldown = 0;
        dodgeCooldown = 0;
        invulnFrames = INVULN_ON_SPAWN;
        eliminated = false;
        flashTimer = 0;
        body.setRayon(radius);
        body.setCouleur(baseColor);
        updateVisuals();
    }

    public void loseLife() {
        lives--;
        if (lives <= 0) {
            eliminated = true;
        }
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public double getAngle() { return angle; }
    public int getRadius() { return radius; }
    public int getState() { return state; }
    public boolean isAI() { return isAI; }
    public int getPlayerId() { return playerId; }
    public boolean isDashing() { return state == DASHING; }
    public boolean isDodging() { return state == DODGING; }
    public boolean isFalling() { return state == FALLING; }
    public boolean isEliminated() { return eliminated; }
    public int getLives() { return lives; }
    public int getInvulnFrames() { return invulnFrames; }
    public int getChargeCooldown() { return chargeCooldown; }
    public int getDodgeCooldown() { return dodgeCooldown; }
    public Cercle getBody() { return body; }
    public Cercle getDirDot() { return dirDot; }
    public Cercle getShadow() { return shadow; }
    public Couleur getBaseColor() { return baseColor; }
    public double getWeight() { return weight; }

    public void setLives(int l) { this.lives = l; }
    public void setEliminated(boolean e) { this.eliminated = e; }
    public int getSpeedBoostTimer() { return speedBoostTimer; }

    public void setHp(int hp) {
        this.hp = hp;
        this.maxHp = hp;
        // Crée un anneau visible pour distinguer les ennemis avec PV
        hpRing = new Cercle(brightColor, new Point((int)x, (int)y), radius + 4, false);
    }

    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public boolean hasHp() { return maxHp > 0; }

    public boolean takeDamage(int dmg) {
        if (hp <= 0 || maxHp <= 0) return false;
        hp -= dmg;
        flashTimer = 12;
        if (hp <= 0) {
            hp = 0;
            eliminated = true;
            return true;
        }
        return false;
    }

    public void stunFromObstacle() {
        if (state == FALLING || state == DEAD || eliminated) return;
        vx *= -0.5;
        vy *= -0.5;
        state = STUNNED;
        stateTimer = 6;
        flashTimer = 8;
    }

    public void applySpeedBoost(int frames) {
        speedBoostTimer = frames;
    }

    public void resetCooldowns() {
        chargeCooldown = 0;
        dodgeCooldown = 0;
    }

    public double distToCenter(double cx, double cy) {
        return Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
    }

    // Force de recul du dash (plus élevée pour les combattants plus lourds/rapides)
    public double getDashForce() {
        double vel = Math.sqrt(vx * vx + vy * vy);
        return KNOCKBACK_BASE + vel * 0.6 + (weight - 1.0) * 5.0;
    }
}
