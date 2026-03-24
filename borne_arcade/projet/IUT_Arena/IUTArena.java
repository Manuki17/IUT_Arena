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
import java.awt.Font;
import java.util.ArrayList;

public class IUTArena {

    // Fenêtre et entrées
    private FenetrePleinEcran f;
    private ClavierBorneArcade clavier;

    // Dimensions de l'écran
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 1024;

    // Arène
    private static final double ARENA_X = 640;
    private static final double ARENA_Y = 460;
    private static final double ARENA_BASE_RADIUS = 330;
    private double arenaRadius;
    private double arenaTargetRadius;

    // Éléments visuels de l'arène
    private Cercle arenaShadow;
    private Cercle arenaSurface;
    private Cercle arenaEdge;
    private Cercle arenaEdge2;
    private Cercle arenaEdge3;
    private Cercle arenaRing1;
    private Cercle arenaRing2;
    private Cercle arenaRing3;
    private Cercle arenaCenterDot;
    private Ligne arenaCrossH;
    private Ligne arenaCrossV;
    private Ligne[] radialLines;
    private Cercle dangerGlow;
    private Cercle arenaInnerGlow;

    // Texture du sol de l'arène et arrière-plans des niveaux
    private Texture arenaFloorTex;
    private Texture[] levelBgs;
    private int currentBgIndex;
    private static final String[] BG_FILES = {
        "img/bg/space.png", "img/bg/forest.png", "img/bg/desert.png",
        "img/bg/volcano.png", "img/bg/ice.png", "img/bg/neon.png"
    };

    // États du jeu
    public static final int MENU = 0;
    public static final int COUNTDOWN = 1;
    public static final int PLAYING = 2;
    public static final int ROUND_END = 3;
    public static final int WAVE_CLEAR = 4;
    public static final int GAME_OVER = 5;
    public static final int HIGHSCORE = 6;

    private int state;
    private int stateTimer;
    private boolean isSolo;
    private int globalTimer;

    // Combattants
    private Fighter player1;
    private Fighter player2;
    private ArrayList<Fighter> enemies;
    private ArrayList<Fighter> allFighters;

    // Score / état du jeu
    private int score;
    private int displayScore;
    private int wave;
    private boolean isDamageWave;
    private int round;
    private int p1Wins;
    private int p2Wins;
    private int bestOf;
    private int comboKills;

    // Secousse d'écran
    private int shakeTimer;
    private int shakeIntensity;

    // Éléments du menu
    private Texture menuBg;
    private Texture menuTitleImg;
    private Texte titleText;
    private Texte subtitleText;
    private Texte[] menuOptions;
    private Cercle menuSelector;
    private Cercle menuSelectorGlow;
    private int menuSelection;
    private Font fontMenu;
    private Texte scoreText;
    private Texte scoreLabelText;
    private Texte livesText;
    private Texte waveText;
    private Texte waveLabelText;
    private Texte centerMessage;
    private Texte centerSubMessage;
    private Texte controlsHint;
    private Texte roundText;

    // Affichage des cœurs de vies
    private Texture[] hearts;
    private Texture[] heartsEmpty;
    private static final int MAX_LIVES = 3;
    private static final int HEART_SIZE = 36;

    // Barres de temps d'attente
    private Rectangle chargeBar;
    private Rectangle chargeBg;
    private Rectangle dodgeBar;
    private Rectangle dodgeBg;
    private Texte chargeLabel;
    private Texte dodgeLabel;
    private Rectangle chargeBarBorder;
    private Rectangle dodgeBarBorder;

    // Particules
    private ArrayList<Cercle> particles;
    private ArrayList<double[]> particleData;
    private static final int MAX_PARTICLES = 150;

    // Textes de dégâts flottants
    private ArrayList<Texte> dmgTexts;
    private ArrayList<double[]> dmgTextData; // [x, y, vy, life]
    private static final int MAX_DMG_TEXTS = 10;

    // Obstacles
    private ArrayList<Obstacle> obstacles;

    // Thèmes de l'arène
    private static final int THEME_ABYSS = 0;
    private static final int THEME_NETHER = 1;
    private static final int THEME_INFERNO = 2;
    private static final int THEME_GOLD = 3;
    private int currentTheme;
    private int targetTheme;
    private double themeTransition; // 0.0 = actuel, 1.0 = cible atteinte

    // Ensembles de couleurs de thèmes : [arène, bord, anneau1, anneau2, anneau3, pointCentre]
    private static final int[][] THEME_COLORS = {
        // ABYSS (bleu-gris)
        {32,34,48,  160,165,185,  50,54,68,  42,45,60,  38,40,55,  70,74,90},
        // NETHER (violet)
        {45,22,55,  180,90,220,  65,35,80,  55,28,70,  48,24,62,  90,50,110},
        // INFERNO (rouge-orange)
        {50,22,18,  220,110,50,  72,38,30,  62,32,25,  55,28,22,  100,60,40},
        // OR (boss)
        {48,42,22,  255,210,50,  70,62,35,  60,54,28,  52,48,24,  100,90,45}
    };

    // Power-ups
    private PowerUp activePowerUp;
    private int powerUpSpawnTimer;
    private static final int POWERUP_SPAWN_MIN = 480;  // 8 secondes
    private static final int POWERUP_SPAWN_MAX = 900;  // 15 secondes
    private static final int POWERUP_FIRST_DELAY = 600; // 10 secondes avant le premier spawn

    // Étoiles d'arrière-plan
    private Cercle[] bgStars;
    private static final int NUM_STARS = 40;

    // Couleurs
    private static final Couleur COL_BG = new Couleur(8, 8, 14);
    private static final Couleur COL_ARENA = new Couleur(32, 34, 48);
    private static final Couleur COL_ARENA_EDGE = new Couleur(160, 165, 185);
    private static final Couleur COL_ARENA_RING = new Couleur(50, 54, 68);
    private static final Couleur COL_ARENA_RING2 = new Couleur(42, 45, 60);
    private static final Couleur COL_ARENA_RING3 = new Couleur(38, 40, 55);
    private static final Couleur COL_SHADOW = new Couleur(14, 14, 22);
    private static final Couleur COL_P1 = new Couleur(50, 120, 230);
    private static final Couleur COL_P1_BRIGHT = new Couleur(100, 170, 255);
    private static final Couleur COL_P2 = new Couleur(230, 55, 55);
    private static final Couleur COL_P2_BRIGHT = new Couleur(255, 110, 110);
    private static final Couleur COL_ENEMY_BASIC = new Couleur(220, 145, 35);
    private static final Couleur COL_ENEMY_BASIC_B = new Couleur(255, 190, 80);
    private static final Couleur COL_ENEMY_MEDIUM = new Couleur(155, 55, 200);
    private static final Couleur COL_ENEMY_MEDIUM_B = new Couleur(200, 110, 255);
    private static final Couleur COL_ENEMY_HARD = new Couleur(200, 35, 75);
    private static final Couleur COL_ENEMY_HARD_B = new Couleur(255, 80, 120);
    private static final Couleur COL_BOSS = new Couleur(140, 15, 15);
    private static final Couleur COL_BOSS_B = new Couleur(200, 50, 50);
    private static final Couleur COL_UI = Couleur.BLANC;
    private static final Couleur COL_UI_DIM = new Couleur(100, 105, 130);
    private static final Couleur COL_GOLD = new Couleur(255, 210, 50);
    private static final Couleur COL_DANGER = new Couleur(200, 40, 40);
    private static final Couleur COL_BAR_BG = new Couleur(22, 22, 35);
    private static final Couleur COL_BAR_BORDER = new Couleur(70, 72, 90);

    // Polices
    private Font fontTitle;
    private Font fontLarge;
    private Font fontMedium;
    private Font fontSmall;
    private Font fontTiny;

    // Statut pour Main
    private int status;

    public IUTArena() {
        f = new FenetrePleinEcran("IUT Arena");
        clavier = new ClavierBorneArcade();
        f.addKeyListener(clavier);
        f.getP().addKeyListener(clavier);

        fontTitle = new Font("Arial", Font.BOLD, 72);
        fontLarge = new Font("Arial", Font.BOLD, 48);
        fontMedium = new Font("Arial", Font.BOLD, 28);
        fontSmall = new Font("Arial", Font.PLAIN, 20);
        fontTiny = new Font("Arial", Font.BOLD, 14);

        particles = new ArrayList<Cercle>();
        particleData = new ArrayList<double[]>();
        dmgTexts = new ArrayList<Texte>();
        dmgTextData = new ArrayList<double[]>();
        enemies = new ArrayList<Fighter>();
        allFighters = new ArrayList<Fighter>();
        obstacles = new ArrayList<Obstacle>();
        currentTheme = THEME_ABYSS;
        targetTheme = THEME_ABYSS;
        themeTransition = 1.0;

        arenaRadius = ARENA_BASE_RADIUS;
        arenaTargetRadius = ARENA_BASE_RADIUS;
        globalTimer = 0;
        displayScore = 0;
        shakeTimer = 0;
        shakeIntensity = 0;

        createBackground();
        createArena();
        createUI();
        setupMenu();

        state = MENU;
        stateTimer = 0;
        status = 0;
    }

    // ========== BACKGROUND ==========

    private void createBackground() {
        Rectangle bg = new Rectangle(COL_BG, new Point(0, 0), new Point(WIDTH, HEIGHT), true);
        f.ajouter(bg);

        bgStars = new Cercle[NUM_STARS];
        for (int i = 0; i < NUM_STARS; i++) {
            int sx = (int)(Math.random() * WIDTH);
            int sy = (int)(Math.random() * HEIGHT);
            int brightness = 20 + (int)(Math.random() * 40);
            int size = Math.random() < 0.3 ? 2 : 1;
            bgStars[i] = new Cercle(new Couleur(brightness, brightness, brightness + 10),
                new Point(sx, sy), size, true);
            f.ajouter(bgStars[i]);
        }
    }

    // ========== CRÉATION DE L'ARÈNE ==========

    private void createArena() {
        // Arrière-plans des niveaux (tous préchargés, cachés hors écran sauf le premier)
        currentBgIndex = 0;
        levelBgs = new Texture[BG_FILES.length];
        for (int i = 0; i < BG_FILES.length; i++) {
            levelBgs[i] = new Texture(BG_FILES[i], new Point(i == 0 ? 0 : -9999, 0));
            levelBgs[i].setLargeur(WIDTH);
            levelBgs[i].setHauteur(HEIGHT);
            f.ajouter(levelBgs[i]);
        }

        // Ombre de l'arène (plus grande, décalée)
        arenaShadow = new Cercle(COL_SHADOW, new Point((int)ARENA_X + 8, (int)ARENA_Y - 10),
                                 (int)arenaRadius + 12, true);
        f.ajouter(arenaShadow);

        // Anneau de lueur de danger (derrière l'arène, rouge quand près du bord)
        dangerGlow = new Cercle(COL_BG, new Point((int)ARENA_X, (int)ARENA_Y),
                                (int)arenaRadius + 4, false);
        f.ajouter(dangerGlow);

        // Surface de l'arène (base sombre)
        arenaSurface = new Cercle(COL_ARENA, new Point((int)ARENA_X, (int)ARENA_Y),
                                  (int)arenaRadius, true);
        f.ajouter(arenaSurface);

        // Texture du sol de l'arène (l'image de la plateforme sci-fi)
        // Image 1280x1167, cercle 1128x1115, ratios : L=0.881 H=0.955
        try {
            int texW = (int)(arenaRadius * 2 / 0.881);
            int texH = (int)(arenaRadius * 2 / 0.955);
            // Centre du cercle dans l'image à (639/1280, 591/1167) = (49,9%, 50,6%)
            int offX = (int)(texW * 639.0 / 1280.0);
            int offY = (int)(texH * 591.0 / 1167.0);
            arenaFloorTex = new Texture("img/arena/floor.png",
                new Point((int)ARENA_X - offX, (int)ARENA_Y - offY));
            arenaFloorTex.setLargeur(texW);
            arenaFloorTex.setHauteur(texH);
            f.ajouter(arenaFloorTex);
        } catch (Exception e) {
            arenaFloorTex = null;
        }

        // Lueur intérieure (anneau clair subtil à l'intérieur du bord)
        arenaInnerGlow = new Cercle(COL_ARENA_RING, new Point((int)ARENA_X, (int)ARENA_Y),
                                    (int)(arenaRadius * 0.95), false);
        f.ajouter(arenaInnerGlow);

        // Lignes radiales (cachées derrière la texture mais gardées pour secours)
        radialLines = new Ligne[8];
        for (int i = 0; i < 8; i++) {
            double ang = Math.PI * 2 * i / 8;
            int x1 = (int)(ARENA_X + Math.cos(ang) * 30);
            int y1 = (int)(ARENA_Y + Math.sin(ang) * 30);
            int x2 = (int)(ARENA_X + Math.cos(ang) * arenaRadius * 0.85);
            int y2 = (int)(ARENA_Y + Math.sin(ang) * arenaRadius * 0.85);
            radialLines[i] = new Ligne(COL_ARENA_RING3, new Point(x1, y1), new Point(x2, y2));
            if (arenaFloorTex == null) f.ajouter(radialLines[i]);
        }

        // Anneaux intérieurs
        arenaRing1 = new Cercle(COL_ARENA_RING, new Point((int)ARENA_X, (int)ARENA_Y),
                                (int)(arenaRadius * 0.7), false);
        if (arenaFloorTex == null) f.ajouter(arenaRing1);

        arenaRing2 = new Cercle(COL_ARENA_RING2, new Point((int)ARENA_X, (int)ARENA_Y),
                                (int)(arenaRadius * 0.45), false);
        if (arenaFloorTex == null) f.ajouter(arenaRing2);

        arenaRing3 = new Cercle(COL_ARENA_RING3, new Point((int)ARENA_X, (int)ARENA_Y),
                                (int)(arenaRadius * 0.2), false);
        if (arenaFloorTex == null) f.ajouter(arenaRing3);

        // Croix centrale
        int crossSize = 25;
        arenaCrossH = new Ligne(COL_ARENA_RING,
            new Point((int)ARENA_X - crossSize, (int)ARENA_Y),
            new Point((int)ARENA_X + crossSize, (int)ARENA_Y));
        if (arenaFloorTex == null) f.ajouter(arenaCrossH);

        arenaCrossV = new Ligne(COL_ARENA_RING,
            new Point((int)ARENA_X, (int)ARENA_Y - crossSize),
            new Point((int)ARENA_X, (int)ARENA_Y + crossSize));
        if (arenaFloorTex == null) f.ajouter(arenaCrossV);

        // Point central
        arenaCenterDot = new Cercle(new Couleur(70, 74, 90), new Point((int)ARENA_X, (int)ARENA_Y), 5, true);
        if (arenaFloorTex == null) f.ajouter(arenaCenterDot);

        // Contour du bord - bordure rouge épaisse (3 cercles concentriques pour l'épaisseur)
        Couleur edgeRed = new Couleur(200, 40, 40);
        arenaEdge = new Cercle(edgeRed, new Point((int)ARENA_X, (int)ARENA_Y),
                               (int)arenaRadius, false);
        arenaEdge2 = new Cercle(edgeRed, new Point((int)ARENA_X, (int)ARENA_Y),
                               (int)arenaRadius - 1, false);
        arenaEdge3 = new Cercle(edgeRed, new Point((int)ARENA_X, (int)ARENA_Y),
                               (int)arenaRadius - 2, false);
        f.ajouter(arenaEdge);
        f.ajouter(arenaEdge2);
        f.ajouter(arenaEdge3);
    }

    private void updateArenaVisuals() {
        int r = (int) arenaRadius;
        arenaShadow.setRayon(r + 12);
        arenaSurface.setRayon(r);
        arenaEdge.setRayon(r);
        arenaEdge2.setRayon(r - 1);
        arenaEdge3.setRayon(r - 2);
        arenaRing1.setRayon((int)(r * 0.7));
        arenaRing2.setRayon((int)(r * 0.45));
        arenaRing3.setRayon((int)(r * 0.2));
        arenaInnerGlow.setRayon((int)(r * 0.95));
        dangerGlow.setRayon(r + 4);

        // Mettre à jour la texture du sol pour correspondre à la taille de l'arène
        if (arenaFloorTex != null) {
            int texW = (int)(r * 2 / 0.881);
            int texH = (int)(r * 2 / 0.955);
            int offX = (int)(texW * 639.0 / 1280.0);
            int offY = (int)(texH * 591.0 / 1167.0);
            arenaFloorTex.setA(new Point((int)ARENA_X - offX, (int)ARENA_Y - offY));
            arenaFloorTex.setLargeur(texW);
            arenaFloorTex.setHauteur(texH);
        }

        // Mettre à jour les lignes radiales
        for (int i = 0; i < 8; i++) {
            double ang = Math.PI * 2 * i / 8;
            int x2 = (int)(ARENA_X + Math.cos(ang) * r * 0.85);
            int y2 = (int)(ARENA_Y + Math.sin(ang) * r * 0.85);
            radialLines[i].setB(new Point(x2, y2));
        }

        // Rétrécissement fluide de l'arène
        if (Math.abs(arenaRadius - arenaTargetRadius) > 1) {
            arenaRadius += (arenaTargetRadius - arenaRadius) * 0.02;
        } else {
            arenaRadius = arenaTargetRadius;
        }

        // Lueur de danger du bord - pulse en rouge quand le joueur est près du bord
        int[] tc = THEME_COLORS[currentTheme];
        if (state == PLAYING && player1 != null && !player1.isEliminated()) {
            double distToEdge = arenaRadius - player1.distToCenter(ARENA_X, ARENA_Y);
            if (distToEdge < 90) {
                double dangerLevel = 1.0 - distToEdge / 90.0;
                double pulse = 0.7 + 0.3 * Math.sin(globalTimer * 0.15);
                int danger = (int)(200 * dangerLevel * pulse);
                Couleur dangerEdge = new Couleur(
                    Math.min(255, tc[3] + danger),
                    Math.max(0, tc[4] - danger * 2),
                    Math.max(0, tc[5] - danger * 2));
                arenaEdge.setCouleur(dangerEdge);
                arenaEdge2.setCouleur(dangerEdge);
                arenaEdge3.setCouleur(dangerEdge);
                dangerGlow.setCouleur(new Couleur(
                    Math.min(255, (int)(danger * 0.4)), 0, 0));
            } else if (themeTransition >= 1.0) {
                Couleur ec = new Couleur(tc[3], tc[4], tc[5]);
                arenaEdge.setCouleur(ec);
                arenaEdge2.setCouleur(ec);
                arenaEdge3.setCouleur(ec);
                dangerGlow.setCouleur(COL_BG);
            }
        } else if (themeTransition >= 1.0) {
            Couleur ec = new Couleur(tc[3], tc[4], tc[5]);
            arenaEdge.setCouleur(ec);
            arenaEdge2.setCouleur(ec);
            arenaEdge3.setCouleur(ec);
            dangerGlow.setCouleur(COL_BG);
        }

        // Theme color transitions
        if (themeTransition < 1.0) {
            themeTransition = Math.min(1.0, themeTransition + 0.016); // ~60 frames
            int[] from = THEME_COLORS[currentTheme];
            int[] to = THEME_COLORS[targetTheme];
            double t = themeTransition;

            arenaSurface.setCouleur(lerpColor(from[0],from[1],from[2], to[0],to[1],to[2], t));
            arenaRing1.setCouleur(lerpColor(from[6],from[7],from[8], to[6],to[7],to[8], t));
            arenaRing2.setCouleur(lerpColor(from[9],from[10],from[11], to[9],to[10],to[11], t));
            arenaRing3.setCouleur(lerpColor(from[12],from[13],from[14], to[12],to[13],to[14], t));
            arenaInnerGlow.setCouleur(lerpColor(from[6],from[7],from[8], to[6],to[7],to[8], t));
            arenaCenterDot.setCouleur(lerpColor(from[15],from[16],from[17], to[15],to[16],to[17], t));
            arenaCrossH.setCouleur(lerpColor(from[6],from[7],from[8], to[6],to[7],to[8], t));
            arenaCrossV.setCouleur(lerpColor(from[6],from[7],from[8], to[6],to[7],to[8], t));
            for (Ligne rl : radialLines) {
                rl.setCouleur(lerpColor(from[12],from[13],from[14], to[12],to[13],to[14], t));
            }

            // Update obstacle colors to match theme
            Couleur obsBody = lerpColor(from[0]-8,from[1]-8,from[2]-8, to[0]-8,to[1]-8,to[2]-8, t);
            Couleur obsOutline = lerpColor(from[3],from[4],from[5], to[3],to[4],to[5], t);
            Couleur obsHighlight = lerpColor(from[6],from[7],from[8], to[6],to[7],to[8], t);
            for (Obstacle obs : obstacles) {
                obs.setThemeColors(obsBody, obsOutline, obsHighlight);
            }

            if (themeTransition >= 1.0) {
                currentTheme = targetTheme;
            }
        }

        // Twinkling stars
        if (globalTimer % 15 == 0) {
            int idx = (int)(Math.random() * NUM_STARS);
            int b = 15 + (int)(Math.random() * 55);
            bgStars[idx].setCouleur(new Couleur(b, b, b + 10));
        }
    }

    // ========== UI CREATION ==========

    private void createUI() {
        scoreLabelText = new Texte(COL_UI_DIM, "SCORE", fontTiny, new Point(130, 985));
        scoreText = new Texte(COL_UI, "0", fontMedium, new Point(130, 960));
        livesText = new Texte(COL_UI, "", fontMedium, new Point(1100, 970));

        // Hearts
        hearts = new Texture[MAX_LIVES];
        heartsEmpty = new Texture[MAX_LIVES];
        for (int i = 0; i < MAX_LIVES; i++) {
            int hx = 1050 + i * (HEART_SIZE + 10);
            hearts[i] = new Texture("img/game/heart.png", new Point(hx, 955), HEART_SIZE, HEART_SIZE);
            heartsEmpty[i] = new Texture("img/game/heart_empty.png", new Point(hx, 955), HEART_SIZE, HEART_SIZE);
        }

        waveLabelText = new Texte(COL_UI_DIM, "VAGUE", fontTiny, new Point(640, 990));
        waveText = new Texte(COL_GOLD, "", fontMedium, new Point(640, 965));
        centerMessage = new Texte(COL_UI, "", fontLarge, new Point(640, 530));
        centerSubMessage = new Texte(COL_UI_DIM, "", fontSmall, new Point(640, 490));
        roundText = new Texte(COL_UI, "", fontMedium, new Point(640, 975));

        // Charge bar
        chargeBg = new Rectangle(COL_BAR_BG, new Point(30, 18), new Point(232, 40), true);
        chargeBarBorder = new Rectangle(COL_BAR_BORDER, new Point(30, 18), new Point(232, 40), false);
        chargeBar = new Rectangle(COL_P1_BRIGHT, new Point(31, 19), new Point(231, 39), true);
        chargeLabel = new Texte(COL_UI_DIM, "CHARGE", fontTiny, new Point(131, 11));

        // Dodge bar
        dodgeBg = new Rectangle(COL_BAR_BG, new Point(30, 44), new Point(232, 62), true);
        dodgeBarBorder = new Rectangle(COL_BAR_BORDER, new Point(30, 44), new Point(232, 62), false);
        dodgeBar = new Rectangle(new Couleur(80, 200, 80), new Point(31, 45), new Point(231, 61), true);
        dodgeLabel = new Texte(COL_UI_DIM, "ESQUIVE", fontTiny, new Point(131, 37));

        controlsHint = new Texte(COL_UI_DIM, "", fontSmall, new Point(640, 30));
    }

    private void showGameUI(boolean solo) {
        f.ajouter(scoreLabelText);
        f.ajouter(scoreText);
        if (solo) {
            f.ajouter(waveLabelText);
            f.ajouter(waveText);
            for (int i = 0; i < MAX_LIVES; i++) {
                f.ajouter(heartsEmpty[i]);
                f.ajouter(hearts[i]);
            }
        } else {
            f.ajouter(livesText);
            f.ajouter(roundText);
        }
        f.ajouter(centerMessage);
        f.ajouter(centerSubMessage);
        f.ajouter(chargeBg);
        f.ajouter(chargeBarBorder);
        f.ajouter(chargeBar);
        f.ajouter(chargeLabel);
        f.ajouter(dodgeBg);
        f.ajouter(dodgeBarBorder);
        f.ajouter(dodgeBar);
        f.ajouter(dodgeLabel);
        f.ajouter(controlsHint);
        controlsHint.setTexte("[F] Charger   [G] Esquiver   [Y] Quitter");
    }

    private void hideGameUI() {
        f.supprimer(scoreLabelText);
        f.supprimer(scoreText);
        f.supprimer(livesText);
        f.supprimer(waveLabelText);
        f.supprimer(waveText);
        f.supprimer(roundText);
        f.supprimer(centerMessage);
        f.supprimer(centerSubMessage);
        f.supprimer(chargeBg);
        f.supprimer(chargeBarBorder);
        f.supprimer(chargeBar);
        f.supprimer(chargeLabel);
        f.supprimer(dodgeBg);
        f.supprimer(dodgeBarBorder);
        f.supprimer(dodgeBar);
        f.supprimer(dodgeLabel);
        f.supprimer(controlsHint);
        for (int i = 0; i < MAX_LIVES; i++) {
            f.supprimer(hearts[i]);
            f.supprimer(heartsEmpty[i]);
        }
    }

    private void updateGameUI() {
        // Animated score counter
        if (displayScore < score) {
            int diff = score - displayScore;
            displayScore += Math.max(1, diff / 8);
            if (displayScore > score) displayScore = score;
        }

        if (isSolo) {
            scoreText.setTexte("" + displayScore);
            // Heart display
            int lives = player1.getLives();
            for (int i = 0; i < MAX_LIVES; i++) {
                if (i < lives) {
                    hearts[i].setA(new Point(1050 + i * (HEART_SIZE + 10), 955));
                } else {
                    hearts[i].setA(new Point(-100, -100));
                }
            }
            waveText.setTexte("" + wave);
        } else {
            scoreText.setTexte("J1: " + p1Wins);
            livesText.setTexte("J2: " + p2Wins);
            roundText.setTexte("ROUND " + round + "/" + bestOf);
        }

        // Charge bar
        int chargeCd = player1.getChargeCooldown();
        double chargeRatio = 1.0 - (double)chargeCd / 55;
        int barWidth = (int)(200 * chargeRatio);
        chargeBar.setB(new Point(31 + Math.max(1, barWidth), 39));
        if (chargeRatio >= 1.0) {
            // Pulse when ready
            double pulse = 0.8 + 0.2 * Math.sin(globalTimer * 0.1);
            int cr = (int)(COL_P1_BRIGHT.getRed() * pulse);
            int cg = (int)(COL_P1_BRIGHT.getGreen() * pulse);
            int cb = (int)(COL_P1_BRIGHT.getBlue() * pulse);
            chargeBar.setCouleur(new Couleur(
                Math.min(255, cr), Math.min(255, cg), Math.min(255, cb)));
        } else {
            chargeBar.setCouleur(new Couleur(50, 55, 75));
        }

        // Dodge bar
        int dodgeCd = player1.getDodgeCooldown();
        double dodgeRatio = 1.0 - (double)dodgeCd / 42;
        int dodgeWidth = (int)(200 * dodgeRatio);
        dodgeBar.setB(new Point(31 + Math.max(1, dodgeWidth), 61));
        if (dodgeRatio >= 1.0) {
            double pulse = 0.8 + 0.2 * Math.sin(globalTimer * 0.12);
            dodgeBar.setCouleur(new Couleur(
                (int)(80 * pulse), (int)(200 * pulse), (int)(80 * pulse)));
        } else {
            dodgeBar.setCouleur(new Couleur(50, 55, 75));
        }
    }

    // ========== MENU ==========

    private void setupMenu() {
        // Background image (fullscreen)
        menuBg = new Texture("img/menu/bg.png", new Point(0, 0));
        menuBg.setLargeur(WIDTH);
        menuBg.setHauteur(HEIGHT);

        // Title image - centered at top
        menuTitleImg = new Texture("img/menu/title.png", new Point(0, 0));
        int titleW = (int)(WIDTH * 0.35);
        int titleH = (int)(titleW * ((double)menuTitleImg.getHauteur() / menuTitleImg.getLargeur()));
        menuTitleImg.setLargeur(titleW);
        menuTitleImg.setHauteur(titleH);
        menuTitleImg.setA(new Point(WIDTH / 2 - titleW / 2, (int)(HEIGHT * 0.62)));

        // Load Spencer font for menu
        fontMenu = null;
        try {
            java.io.File fontFile = new java.io.File("fonts/Spencer.ttf");
            fontMenu = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            fontMenu = fontMenu.deriveFont((float)(HEIGHT * 0.045));
        } catch (Exception e) {
            fontMenu = new Font("Arial", Font.BOLD, 30);
        }

        subtitleText = new Texte(COL_UI_DIM, "", fontSmall, new Point(640, 690));

        menuOptions = new Texte[3];
        menuOptions[0] = new Texte(Couleur.ORANGE, "1 PLAYER", fontMenu, new Point(640, 420));
        menuOptions[1] = new Texte(Couleur.BLEU, "2 PLAYERS", fontMenu, new Point(640, 340));
        menuOptions[2] = new Texte(Couleur.BLEU, "EXIT", fontMenu, new Point(640, 260));

        menuSelectorGlow = new Cercle(new Couleur(255, 165, 0), new Point(430, 420), 14, false);
        menuSelector = new Cercle(Couleur.ORANGE, new Point(430, 420), 8, true);
        menuSelection = 0;

        // Keep titleText for compatibility but hide it
        titleText = new Texte(COL_GOLD, "", fontSmall, new Point(-100, -100));

        f.ajouter(menuBg);
        f.ajouter(menuTitleImg);
        for (Texte t : menuOptions) f.ajouter(t);
        f.ajouter(menuSelectorGlow);
        f.ajouter(menuSelector);
    }

    private void showMenu() {
        f.ajouter(menuBg);
        f.ajouter(menuTitleImg);
        for (Texte t : menuOptions) f.ajouter(t);
        f.ajouter(menuSelectorGlow);
        f.ajouter(menuSelector);
        menuSelection = 0;
        updateMenuVisuals();
    }

    private void hideMenu() {
        f.supprimer(menuBg);
        f.supprimer(menuTitleImg);
        for (Texte t : menuOptions) f.supprimer(t);
        f.supprimer(menuSelectorGlow);
        f.supprimer(menuSelector);
    }

    private void updateMenuVisuals() {
        int[] yPositions = {420, 340, 260};
        menuSelector.setO(new Point(430, yPositions[menuSelection]));
        menuSelectorGlow.setO(new Point(430, yPositions[menuSelection]));

        // Pulsing selector glow
        double pulse = 10 + 4 * Math.sin(globalTimer * 0.08);
        menuSelectorGlow.setRayon((int)pulse);

        for (int i = 0; i < 3; i++) {
            if (i == menuSelection) {
                menuOptions[i].setCouleur(Couleur.ORANGE);
            } else {
                menuOptions[i].setCouleur(Couleur.BLEU);
            }
        }
    }

    // ========== GAME INITIALIZATION ==========

    private void initSoloGame() {
        isSolo = true;
        score = 0;
        displayScore = 0;
        wave = 0;
        comboKills = 0;

        player1 = new Fighter(ARENA_X, ARENA_Y, 22, COL_P1, COL_P1_BRIGHT, 1);
        player1.setLives(3);
        player1.addToWindow(f);

        allFighters.clear();
        allFighters.add(player1);

        arenaRadius = ARENA_BASE_RADIUS;
        arenaTargetRadius = ARENA_BASE_RADIUS;

        activePowerUp = null;
        powerUpSpawnTimer = POWERUP_FIRST_DELAY;

        showGameUI(true);
        startNextWave();
    }

    private void initVersusGame() {
        isSolo = false;
        round = 1;
        p1Wins = 0;
        p2Wins = 0;
        bestOf = 3;

        spawnVersusRound();
        showGameUI(false);

        state = COUNTDOWN;
        stateTimer = 180;
        centerMessage.setTexte("ROUND 1");
    }

    private void spawnVersusRound() {
        clearFighters();

        player1 = new Fighter(ARENA_X - 150, ARENA_Y, 22, COL_P1, COL_P1_BRIGHT, 1);
        player1.setLives(1);
        player1.addToWindow(f);

        player2 = new Fighter(ARENA_X + 150, ARENA_Y, 22, COL_P2, COL_P2_BRIGHT, 2);
        player2.setLives(1);
        player2.addToWindow(f);

        allFighters.clear();
        allFighters.add(player1);
        allFighters.add(player2);

        arenaRadius = ARENA_BASE_RADIUS;
        arenaTargetRadius = ARENA_BASE_RADIUS;
    }

    private void startNextWave() {
        wave++;
        comboKills = 0;

        if (wave > 3) {
            arenaTargetRadius = Math.max(200, ARENA_BASE_RADIUS - (wave / 3) * 25);
        }

        // Update theme based on wave
        int newTheme;
        if (wave % 5 == 0) {
            newTheme = THEME_GOLD; // Boss waves
        } else if (wave <= 4) {
            newTheme = THEME_ABYSS;
        } else if (wave <= 8) {
            newTheme = THEME_NETHER;
        } else {
            newTheme = THEME_INFERNO;
        }

        if (newTheme != targetTheme) {
            targetTheme = newTheme;
            themeTransition = 0.0;
            String[] themeNames = {"~ ABYSS ~", "~ NETHER ~", "~ INFERNO ~", "~ GOLDEN ARENA ~"};
            centerSubMessage.setTexte(themeNames[newTheme]);
            centerSubMessage.setCouleur(new Couleur(
                THEME_COLORS[newTheme][3], THEME_COLORS[newTheme][4], THEME_COLORS[newTheme][5]));
        }

        // Change background every 2 waves (just move textures, no z-order issues)
        int newBgIndex = Math.min(((wave - 1) / 2), BG_FILES.length - 1);
        if (newBgIndex != currentBgIndex) {
            levelBgs[currentBgIndex].setA(new Point(-9999, 0));
            currentBgIndex = newBgIndex;
            levelBgs[currentBgIndex].setA(new Point(0, 0));
        }

        // Determine wave type: damage waves every 3rd non-boss wave (3, 6, 8, 9, 11...)
        isDamageWave = (wave >= 3) && (wave % 3 == 0) && (wave % 5 != 0);

        // Update obstacles for this wave
        updateObstaclesForWave(wave);

        spawnWave(wave);
        state = COUNTDOWN;
        stateTimer = 150;
        if (isDamageWave) {
            centerMessage.setTexte("VAGUE " + wave + " - COMBAT!");
            centerMessage.setCouleur(new Couleur(255, 120, 60));
        } else {
            centerMessage.setTexte("VAGUE " + wave);
            centerMessage.setCouleur(COL_GOLD);
        }
    }

    private void spawnWave(int waveNum) {
        removePowerUpIfActive();
        powerUpSpawnTimer = POWERUP_FIRST_DELAY;

        for (Fighter e : enemies) {
            e.removeFromWindow(f);
        }
        enemies.clear();

        int numEnemies;
        if (waveNum % 5 == 0) {
            numEnemies = 1 + waveNum / 10;
            spawnBoss(Math.PI);
            for (int i = 1; i < numEnemies; i++) {
                double ang = Math.PI + (i * 0.8 - (numEnemies - 1) * 0.4);
                spawnEnemy(getEnemyTypeForWave(waveNum - 1), ang);
            }
        } else {
            numEnemies = Math.min(waveNum, 5);
            for (int i = 0; i < numEnemies; i++) {
                double ang = (2 * Math.PI * i / numEnemies) + Math.random() * 0.3;
                spawnEnemy(getEnemyTypeForWave(waveNum), ang);
            }
        }

        allFighters.clear();
        allFighters.add(player1);
        allFighters.addAll(enemies);
    }

    private int getEnemyTypeForWave(int w) {
        if (w <= 2) return 0;
        if (w <= 4) return Math.random() < 0.5 ? 0 : 1;
        if (w <= 7) return Math.random() < 0.3 ? 1 : 2;
        return Math.random() < 0.2 ? 1 : 2;
    }

    private void spawnEnemy(int type, double angle) {
        double spawnDist = arenaRadius * 0.7;
        double ex = ARENA_X + Math.cos(angle) * spawnDist;
        double ey = ARENA_Y + Math.sin(angle) * spawnDist;

        Fighter enemy;
        switch (type) {
            case 0:
                enemy = new Fighter(ex, ey, 20, COL_ENEMY_BASIC, COL_ENEMY_BASIC_B,
                    0.3, 0.65, 0.08, 1.0, 45);
                break;
            case 1:
                enemy = new Fighter(ex, ey, 22, COL_ENEMY_MEDIUM, COL_ENEMY_MEDIUM_B,
                    0.55, 0.85, 0.25, 1.0, 32);
                break;
            case 2:
                enemy = new Fighter(ex, ey, 22, COL_ENEMY_HARD, COL_ENEMY_HARD_B,
                    0.75, 1.0, 0.42, 1.0, 22);
                break;
            default:
                enemy = new Fighter(ex, ey, 20, COL_ENEMY_BASIC, COL_ENEMY_BASIC_B,
                    0.3, 0.65, 0.08, 1.0, 45);
        }
        if (isDamageWave) {
            int hpVal;
            switch (type) {
                case 0: hpVal = 3; break;
                case 1: hpVal = 4; break;
                case 2: hpVal = 5; break;
                default: hpVal = 3;
            }
            enemy.setHp(hpVal);
        }
        enemy.addToWindow(f);
        enemies.add(enemy);
    }

    private void spawnBoss(double angle) {
        double spawnDist = arenaRadius * 0.6;
        double ex = ARENA_X + Math.cos(angle) * spawnDist;
        double ey = ARENA_Y + Math.sin(angle) * spawnDist;

        Fighter boss = new Fighter(ex, ey, 36, COL_BOSS, COL_BOSS_B,
            0.8, 0.55, 0.3, 2.2, 28);
        boss.setHp(8 + wave / 5 * 2);
        boss.addToWindow(f);
        enemies.add(boss);
    }

    private void clearFighters() {
        if (player1 != null) {
            player1.removeFromWindow(f);
            player1 = null;
        }
        if (player2 != null) {
            player2.removeFromWindow(f);
            player2 = null;
        }
        for (Fighter e : enemies) {
            e.removeFromWindow(f);
        }
        enemies.clear();
        allFighters.clear();
    }

    // ========== SCREEN SHAKE ==========

    private void triggerShake(int intensity, int duration) {
        shakeIntensity = intensity;
        shakeTimer = duration;
    }

    private void applyScreenShake() {
        if (shakeTimer > 0) {
            shakeTimer--;
            int ox = (int)((Math.random() - 0.5) * shakeIntensity * 2);
            int oy = (int)((Math.random() - 0.5) * shakeIntensity * 2);
            arenaSurface.setO(new Point((int)ARENA_X + ox, (int)ARENA_Y + oy));
            arenaEdge.setO(new Point((int)ARENA_X + ox, (int)ARENA_Y + oy));
            if (shakeTimer <= 0) {
                arenaSurface.setO(new Point((int)ARENA_X, (int)ARENA_Y));
                arenaEdge.setO(new Point((int)ARENA_X, (int)ARENA_Y));
            }
        }
    }

    // ========== MAIN UPDATE ==========

    public void update() {
        globalTimer++;

        switch (state) {
            case MENU:
                updateMenu();
                break;
            case COUNTDOWN:
                updateCountdown();
                break;
            case PLAYING:
                updatePlaying();
                break;
            case ROUND_END:
                updateRoundEnd();
                break;
            case WAVE_CLEAR:
                updateWaveClear();
                break;
            case GAME_OVER:
                updateGameOver();
                break;
        }

        updateParticles();
        updateDmgTexts();
        updateArenaVisuals();
        applyScreenShake();
        f.rafraichir();
    }

    // ========== STATE UPDATES ==========

    private void updateMenu() {
        updateMenuVisuals();

        if (clavier.getJoyJ1HautTape()) {
            if (menuSelection > 0) {
                menuSelection--;
                updateMenuVisuals();
            }
        }
        if (clavier.getJoyJ1BasTape()) {
            if (menuSelection < 2) {
                menuSelection++;
                updateMenuVisuals();
            }
        }

        if (clavier.getBoutonJ1ATape()) {
            switch (menuSelection) {
                case 0:
                    hideMenu();
                    initSoloGame();
                    break;
                case 1:
                    hideMenu();
                    initVersusGame();
                    break;
                case 2:
                    System.exit(0);
                    break;
            }
        }

        if (clavier.getBoutonJ1ZTape()) {
            System.exit(0);
        }
    }

    private void updateCountdown() {
        stateTimer--;

        if (stateTimer > 120) {
            centerMessage.setTexte("3");
            centerMessage.setCouleur(COL_UI);
            centerSubMessage.setTexte("");
        } else if (stateTimer > 80) {
            centerMessage.setTexte("2");
        } else if (stateTimer > 40) {
            centerMessage.setTexte("1");
        } else if (stateTimer > 0) {
            centerMessage.setTexte("GO!");
            centerMessage.setCouleur(COL_GOLD);
        } else {
            centerMessage.setTexte("");
            centerSubMessage.setTexte("");
            state = PLAYING;
        }
    }

    private void updatePlaying() {
        player1.handlePlayerInput(clavier);
        if (!isSolo && player2 != null) {
            player2.handlePlayerInput(clavier);
        }

        ArrayList<Fighter> aiTargets = new ArrayList<Fighter>();
        aiTargets.add(player1);
        if (!isSolo && player2 != null) aiTargets.add(player2);

        for (Fighter e : enemies) {
            if (!e.isEliminated()) {
                e.updateAI(aiTargets, ARENA_X, ARENA_Y, arenaRadius);
            }
        }

        for (Fighter fighter : allFighters) {
            fighter.update();
        }

        checkCollisions();
        checkObstacleCollisions();
        checkBoundaries();

        // Power-ups
        updatePowerUps();

        // Speed boost trail particles
        if (player1 != null && !player1.isEliminated() && player1.getSpeedBoostTimer() > 0) {
            if (particles.size() < MAX_PARTICLES - 2) {
                double ox = player1.getX() + (Math.random() - 0.5) * 10;
                double oy = player1.getY() + (Math.random() - 0.5) * 10;
                addParticle(ox, oy, (Math.random() - 0.5) * 1.5, (Math.random() - 0.5) * 1.5,
                            10, 50, 200, 240, 3);
            }
        }

        // Particle trails
        for (Fighter fighter : allFighters) {
            if (fighter.isEliminated()) continue;
            if (fighter.isDashing()) {
                spawnDashTrail(fighter);
                spawnDashTrail(fighter);
                spawnDashTrail(fighter);
            } else if (fighter.getState() == Fighter.WINDUP) {
                spawnWindupEffect(fighter);
                spawnWindupEffect(fighter);
            }
        }

        if (isSolo) {
            checkSoloWinCondition();
        } else {
            checkVersusWinCondition();
        }

        updateGameUI();

        if (clavier.getBoutonJ1ZTape()) {
            cleanupAndReturnToMenu();
        }
    }

    private void updateRoundEnd() {
        stateTimer--;
        if (stateTimer <= 0) {
            if (isSolo) {
                if (player1.getLives() <= 0) {
                    state = GAME_OVER;
                    stateTimer = 0;
                    centerMessage.setTexte("GAME OVER");
                    centerMessage.setCouleur(COL_P2);
                    centerSubMessage.setTexte("Score: " + score);
                    centerSubMessage.setCouleur(COL_UI_DIM);
                } else {
                    player1.respawn(ARENA_X, ARENA_Y);
                    boolean waveCleared = true;
                    for (Fighter e : enemies) {
                        if (!e.isEliminated()) {
                            waveCleared = false;
                            break;
                        }
                    }
                    if (waveCleared) {
                        state = WAVE_CLEAR;
                        stateTimer = 100;
                        centerMessage.setTexte("VAGUE " + wave + " TERMINEE!");
                        centerMessage.setCouleur(COL_GOLD);
                    } else {
                        state = COUNTDOWN;
                        stateTimer = 90;
                        centerMessage.setTexte("PRET...");
                    }
                }
            } else {
                if (p1Wins >= 2 || p2Wins >= 2) {
                    state = GAME_OVER;
                    stateTimer = 0;
                    if (p1Wins >= 2) {
                        centerMessage.setTexte("JOUEUR 1 GAGNE!");
                        centerMessage.setCouleur(COL_P1_BRIGHT);
                    } else {
                        centerMessage.setTexte("JOUEUR 2 GAGNE!");
                        centerMessage.setCouleur(COL_P2_BRIGHT);
                    }
                } else {
                    round++;
                    spawnVersusRound();
                    state = COUNTDOWN;
                    stateTimer = 150;
                    centerMessage.setTexte("ROUND " + round);
                    centerMessage.setCouleur(COL_UI);
                }
            }
        }
    }

    private void updateWaveClear() {
        stateTimer--;
        if (stateTimer <= 0) {
            int bonus = wave * 200;
            score += bonus;
            startNextWave();
        }
    }

    private void updateGameOver() {
        stateTimer++;
        if (stateTimer % 60 < 30) {
            controlsHint.setTexte("[F] Continuer");
        } else {
            controlsHint.setTexte("");
        }

        if (clavier.getBoutonJ1AEnfoncee()) {
            if (isSolo && score > 0) {
                centerMessage.setTexte("");
                centerSubMessage.setTexte("");
                controlsHint.setTexte("");
                hideGameUI();
                clearFighters();
                clearAllParticles();
                saveHighScore(score);
            } else {
                cleanupAndReturnToMenu();
            }
        }

        if (clavier.getBoutonJ1ZTape()) {
            System.exit(0);
        }
    }

    private void cleanupAndReturnToMenu() {
        hideGameUI();
        clearFighters();
        clearAllParticles();
        clearDmgTexts();
        removePowerUpIfActive();
        clearObstacles();
        centerMessage.setTexte("");
        centerSubMessage.setTexte("");
        targetTheme = THEME_ABYSS;
        currentTheme = THEME_ABYSS;
        themeTransition = 1.0;
        // Reset arena colors to ABYSS
        arenaSurface.setCouleur(COL_ARENA);
        arenaRing1.setCouleur(COL_ARENA_RING);
        arenaRing2.setCouleur(COL_ARENA_RING2);
        arenaRing3.setCouleur(COL_ARENA_RING3);
        Couleur edgeRed = new Couleur(200, 40, 40);
        arenaEdge.setCouleur(edgeRed);
        arenaEdge2.setCouleur(edgeRed);
        arenaEdge3.setCouleur(edgeRed);
        arenaInnerGlow.setCouleur(COL_ARENA_RING);
        arenaCenterDot.setCouleur(new Couleur(70, 74, 90));
        // Reset background to space
        if (currentBgIndex != 0) {
            levelBgs[currentBgIndex].setA(new Point(-9999, 0));
            currentBgIndex = 0;
            levelBgs[0].setA(new Point(0, 0));
        }
        arenaRadius = ARENA_BASE_RADIUS;
        arenaTargetRadius = ARENA_BASE_RADIUS;
        state = MENU;
        showMenu();
    }

    private void saveHighScore(int playerScore) {
        try {
            HighScore.demanderEnregistrerNom(f, clavier, null, playerScore, "highscore");
        } catch (Exception e) {
            System.err.println("HighScore UI error: " + e.getMessage());
            ArrayList<LigneHighScore> list = HighScore.lireFichier("highscore");
            HighScore.enregistrerFichier("highscore", list, "AAA", playerScore);
            System.exit(0);
        }
    }

    // ========== OBSTACLES ==========

    private void updateObstaclesForWave(int waveNum) {
        // Remove existing obstacles
        for (Obstacle obs : obstacles) {
            obs.removeFromWindow(f);
        }
        obstacles.clear();

        // Boss waves: no obstacles (clear arena for boss fight)
        if (waveNum % 5 == 0) return;

        // Progressive obstacle placement
        double r = arenaTargetRadius;
        if (waveNum >= 4 && waveNum <= 5) {
            // 1 obstacle slightly off-center
            addObstacle(ARENA_X + 60, ARENA_Y - 40, 20);
        } else if (waveNum >= 6 && waveNum <= 8) {
            // 2 obstacles symmetric
            addObstacle(ARENA_X - r * 0.35, ARENA_Y - r * 0.2, 22);
            addObstacle(ARENA_X + r * 0.35, ARENA_Y + r * 0.2, 22);
        } else if (waveNum >= 9) {
            // 3 obstacles in triangle
            for (int i = 0; i < 3; i++) {
                double ang = Math.PI * 2 * i / 3 + Math.PI / 6;
                double dist = r * 0.4;
                addObstacle(ARENA_X + Math.cos(ang) * dist, ARENA_Y + Math.sin(ang) * dist, 20);
            }
        }
    }

    private void addObstacle(double ox, double oy, int oRadius) {
        Obstacle obs = new Obstacle(ox, oy, oRadius);
        obs.addToWindow(f);
        obstacles.add(obs);
    }

    private void clearObstacles() {
        for (Obstacle obs : obstacles) {
            obs.removeFromWindow(f);
        }
        obstacles.clear();
    }

    private void checkObstacleCollisions() {
        for (Fighter fighter : allFighters) {
            if (fighter.isEliminated() || fighter.isFalling()) continue;
            for (Obstacle obs : obstacles) {
                if (obs.collidesWithFighter(fighter.getX(), fighter.getY(), fighter.getRadius())) {
                    double overlap = obs.getOverlap(fighter.getX(), fighter.getY(), fighter.getRadius());
                    double ang = obs.angleTo(fighter.getX(), fighter.getY());

                    // Push fighter out of obstacle
                    double pushX = Math.cos(ang) * (overlap + 1);
                    double pushY = Math.sin(ang) * (overlap + 1);
                    fighter.pushAway(pushX, pushY);

                    // If dashing, stun and create impact
                    if (fighter.isDashing()) {
                        fighter.stunFromObstacle();
                        spawnHitEffect(
                            obs.getX() + Math.cos(ang) * obs.getRadius(),
                            obs.getY() + Math.sin(ang) * obs.getRadius(),
                            COL_ARENA_EDGE);
                        triggerShake(3, 6);
                    }
                }
            }
        }
    }

    private static Couleur lerpColor(int r1, int g1, int b1, int r2, int g2, int b2, double t) {
        return new Couleur(
            Math.max(0, Math.min(255, (int)(r1 + (r2 - r1) * t))),
            Math.max(0, Math.min(255, (int)(g1 + (g2 - g1) * t))),
            Math.max(0, Math.min(255, (int)(b1 + (b2 - b1) * t)))
        );
    }

    // ========== POWER-UPS ==========

    private void updatePowerUps() {
        if (!isSolo) return;

        // Spawn timer
        if (activePowerUp == null) {
            powerUpSpawnTimer--;
            if (powerUpSpawnTimer <= 0) {
                spawnPowerUp();
                powerUpSpawnTimer = POWERUP_SPAWN_MIN +
                    (int)(Math.random() * (POWERUP_SPAWN_MAX - POWERUP_SPAWN_MIN));
            }
        } else {
            activePowerUp.update();

            // Check expiration
            if (activePowerUp.isExpired()) {
                activePowerUp.removeFromWindow(f);
                activePowerUp = null;
                return;
            }

            // Check player pickup (player only, not AI)
            if (player1 != null && !player1.isEliminated() && !player1.isFalling()) {
                if (activePowerUp.isPickedUpBy(player1.getX(), player1.getY(), player1.getRadius())) {
                    applyPowerUp(activePowerUp.getType());
                    activePowerUp.removeFromWindow(f);
                    activePowerUp = null;
                }
            }
        }
    }

    private void spawnPowerUp() {
        // Random position inside arena (not too close to edge)
        double ang = Math.random() * Math.PI * 2;
        double dist = Math.random() * arenaRadius * 0.6;
        double px = ARENA_X + Math.cos(ang) * dist;
        double py = ARENA_Y + Math.sin(ang) * dist;

        // Random type: 25% heal, 40% speed, 35% charge
        double roll = Math.random();
        int type;
        if (roll < 0.25) {
            type = PowerUp.HEAL;
        } else if (roll < 0.65) {
            type = PowerUp.SPEED;
        } else {
            type = PowerUp.CHARGE;
        }

        // Don't spawn HEAL if player already has full lives
        if (type == PowerUp.HEAL && player1 != null && player1.getLives() >= MAX_LIVES) {
            type = Math.random() < 0.5 ? PowerUp.SPEED : PowerUp.CHARGE;
        }

        activePowerUp = new PowerUp(px, py, type);
        activePowerUp.addToWindow(f);
    }

    private void applyPowerUp(int type) {
        switch (type) {
            case PowerUp.HEAL:
                if (player1.getLives() < MAX_LIVES) {
                    player1.setLives(player1.getLives() + 1);
                }
                spawnPickupEffect(activePowerUp.getX(), activePowerUp.getY(),
                    60, 220, 80);
                centerSubMessage.setTexte("+1 VIE");
                centerSubMessage.setCouleur(new Couleur(60, 220, 80));
                break;
            case PowerUp.SPEED:
                player1.applySpeedBoost(240); // 4 seconds
                spawnPickupEffect(activePowerUp.getX(), activePowerUp.getY(),
                    50, 200, 240);
                centerSubMessage.setTexte("VITESSE!");
                centerSubMessage.setCouleur(new Couleur(50, 200, 240));
                break;
            case PowerUp.CHARGE:
                player1.resetCooldowns();
                spawnPickupEffect(activePowerUp.getX(), activePowerUp.getY(),
                    240, 210, 50);
                centerSubMessage.setTexte("RECHARGE!");
                centerSubMessage.setCouleur(COL_GOLD);
                break;
        }
        score += 25;
    }

    private void spawnPickupEffect(double px, double py, int r, int g, int b) {
        int count = Math.min(12, MAX_PARTICLES - particles.size());
        for (int i = 0; i < count; i++) {
            double angle = Math.random() * Math.PI * 2;
            double speed = 3 + Math.random() * 5;
            addParticle(px, py, Math.cos(angle) * speed, Math.sin(angle) * speed,
                        16, r, g, b, 4 + (int)(Math.random() * 4));
        }
    }

    private void removePowerUpIfActive() {
        if (activePowerUp != null) {
            activePowerUp.removeFromWindow(f);
            activePowerUp = null;
        }
    }

    // ========== COLLISION DETECTION ==========

    private void checkCollisions() {
        for (int i = 0; i < allFighters.size(); i++) {
            Fighter a = allFighters.get(i);
            if (a.isEliminated() || a.isFalling()) continue;

            for (int j = i + 1; j < allFighters.size(); j++) {
                Fighter b = allFighters.get(j);
                if (b.isEliminated() || b.isFalling()) continue;

                if (a.collidesWith(b)) {
                    handleCollision(a, b);
                }
            }
        }
    }

    private void handleCollision(Fighter a, Fighter b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1) { dx = 1; dy = 0; dist = 1; }
        double overlap = (a.getRadius() + b.getRadius()) - dist;

        if (overlap > 0) {
            double pushX = (dx / dist) * overlap * 0.6;
            double pushY = (dy / dist) * overlap * 0.6;
            a.pushAway(-pushX, -pushY);
            b.pushAway(pushX, pushY);
        }

        boolean aWasDashing = a.isDashing();
        boolean bWasDashing = b.isDashing();

        if (aWasDashing && bWasDashing) {
            a.onDashHit(b.getX(), b.getY());
            b.onDashHit(a.getX(), a.getY());
            double midX = (a.getX() + b.getX()) / 2;
            double midY = (a.getY() + b.getY()) / 2;
            spawnHitEffect(midX, midY, COL_GOLD);
            triggerShake(6, 10);
        } else if (aWasDashing && !b.isDodging()) {
            // Apply damage if target has HP
            if (b.hasHp() && b.getHp() > 0) {
                boolean killed = b.takeDamage(1);
                spawnDmgText(b.getX(), b.getY() + 30, "-1",
                    new Couleur(255, 80, 80));
                b.applyKnockback(a.getX(), a.getY(), a.getDashForce() * 0.4);
                if (killed) {
                    b.setEliminated(true);
                    spawnFallEffect(b.getX(), b.getY(), b.getBaseColor());
                    spawnDmgText(b.getX(), b.getY() + 40, "K.O.!", COL_GOLD);
                    score += isSolo ? wave * 150 : 0;
                    comboKills++;
                    if (comboKills > 1) {
                        score += comboKills * 50;
                        centerSubMessage.setTexte("COMBO x" + comboKills + "!");
                        centerSubMessage.setCouleur(COL_GOLD);
                    }
                    triggerShake(6, 12);
                }
            } else {
                b.applyKnockback(a.getX(), a.getY(), a.getDashForce());
            }
            a.onDashHit(b.getX(), b.getY());
            spawnHitEffect(b.getX(), b.getY(), a.getBaseColor());
            triggerShake(4, 8);
            score += isSolo ? 50 : 0;
        } else if (bWasDashing && !a.isDodging()) {
            if (a.hasHp() && a.getHp() > 0) {
                boolean killed = a.takeDamage(1);
                spawnDmgText(a.getX(), a.getY() + 30, "-1",
                    new Couleur(255, 80, 80));
                a.applyKnockback(b.getX(), b.getY(), b.getDashForce() * 0.4);
                if (killed) {
                    a.setEliminated(true);
                    spawnFallEffect(a.getX(), a.getY(), a.getBaseColor());
                    spawnDmgText(a.getX(), a.getY() + 40, "K.O.!", COL_GOLD);
                    score += isSolo ? wave * 150 : 0;
                    comboKills++;
                    if (comboKills > 1) {
                        score += comboKills * 50;
                        centerSubMessage.setTexte("COMBO x" + comboKills + "!");
                        centerSubMessage.setCouleur(COL_GOLD);
                    }
                    triggerShake(6, 12);
                }
            } else {
                a.applyKnockback(b.getX(), b.getY(), b.getDashForce());
            }
            b.onDashHit(a.getX(), a.getY());
            spawnHitEffect(a.getX(), a.getY(), b.getBaseColor());
            triggerShake(4, 8);
            score += isSolo ? 50 : 0;
        }
    }

    private void checkBoundaries() {
        for (Fighter fighter : allFighters) {
            if (fighter.checkBoundary(ARENA_X, ARENA_Y, arenaRadius)) {
                handleFall(fighter);
            }
        }
    }

    private void handleFall(Fighter fighter) {
        spawnFallEffect(fighter.getX(), fighter.getY(), fighter.getBaseColor());
        triggerShake(8, 15);

        if (isSolo) {
            if (fighter.isAI()) {
                score += wave * 100;
                comboKills++;
                if (comboKills > 1) {
                    score += comboKills * 50;
                    centerSubMessage.setTexte("COMBO x" + comboKills + "!");
                    centerSubMessage.setCouleur(COL_GOLD);
                }
            } else {
                fighter.loseLife();
                state = ROUND_END;
                stateTimer = 90;
                centerMessage.setTexte("TOMBE!");
                centerMessage.setCouleur(COL_P2);
            }
        } else {
            if (fighter.getPlayerId() == 1) {
                p2Wins++;
                state = ROUND_END;
                stateTimer = 120;
                centerMessage.setTexte("JOUEUR 2 MARQUE!");
                centerMessage.setCouleur(COL_P2_BRIGHT);
            } else if (fighter.getPlayerId() == 2) {
                p1Wins++;
                state = ROUND_END;
                stateTimer = 120;
                centerMessage.setTexte("JOUEUR 1 MARQUE!");
                centerMessage.setCouleur(COL_P1_BRIGHT);
            }
        }
    }

    private void checkSoloWinCondition() {
        boolean allEliminated = true;
        for (Fighter e : enemies) {
            if (!e.isEliminated() && !e.isFalling()) {
                // Also check HP-killed enemies
                if (e.hasHp() && e.getHp() <= 0) {
                    e.setEliminated(true);
                    continue;
                }
                allEliminated = false;
                break;
            }
        }

        if (allEliminated && state == PLAYING) {
            state = WAVE_CLEAR;
            stateTimer = 120;
            centerMessage.setTexte("VAGUE " + wave + " TERMINEE!");
            centerMessage.setCouleur(COL_GOLD);
            centerSubMessage.setTexte("+" + (wave * 200) + " bonus");
            centerSubMessage.setCouleur(COL_UI_DIM);
        }

        if (player1.isEliminated() && player1.getLives() <= 0 && state == PLAYING) {
            state = GAME_OVER;
            stateTimer = 0;
            centerMessage.setTexte("GAME OVER");
            centerMessage.setCouleur(COL_P2);
            centerSubMessage.setTexte("Score final: " + score);
            centerSubMessage.setCouleur(COL_UI_DIM);
        }
    }

    private void checkVersusWinCondition() {
    }

    // ========== PARTICLE EFFECTS ==========

    private void spawnDashTrail(Fighter fighter) {
        if (particles.size() >= MAX_PARTICLES) return;
        double ox = fighter.getX() + (Math.random() - 0.5) * 12;
        double oy = fighter.getY() + (Math.random() - 0.5) * 12;
        Couleur col = fighter.getBaseColor();
        addParticle(ox, oy, (Math.random() - 0.5) * 2, (Math.random() - 0.5) * 2,
                    15, col.getRed(), col.getGreen(), col.getBlue(),
                    4 + (int)(Math.random() * 5));
    }

    private void spawnWindupEffect(Fighter fighter) {
        if (particles.size() >= MAX_PARTICLES - 5) return;
        double ang = Math.random() * Math.PI * 2;
        double dist = fighter.getRadius() + 12 + Math.random() * 18;
        double ox = fighter.getX() + Math.cos(ang) * dist;
        double oy = fighter.getY() + Math.sin(ang) * dist;
        double speed = 2.5 + Math.random() * 2.5;
        double vx = Math.cos(ang + Math.PI) * speed;
        double vy = Math.sin(ang + Math.PI) * speed;
        Couleur col = fighter.getBaseColor();
        addParticle(ox, oy, vx, vy, 10,
                    Math.min(255, col.getRed() + 80),
                    Math.min(255, col.getGreen() + 80),
                    Math.min(255, col.getBlue() + 80), 3);
    }

    private void spawnHitEffect(double px, double py, Couleur baseCol) {
        int count = Math.min(16, MAX_PARTICLES - particles.size());
        for (int i = 0; i < count; i++) {
            double angle = Math.random() * Math.PI * 2;
            double speed = 4 + Math.random() * 8;
            int size = 3 + (int)(Math.random() * 6);
            addParticle(px, py, Math.cos(angle) * speed, Math.sin(angle) * speed,
                        18 + (int)(Math.random() * 10), 255, 230, 100, size);
        }
    }

    private void spawnFallEffect(double px, double py, Couleur baseCol) {
        int count = Math.min(20, MAX_PARTICLES - particles.size());
        for (int i = 0; i < count; i++) {
            double angle = Math.random() * Math.PI * 2;
            double speed = 2 + Math.random() * 5;
            addParticle(px, py, Math.cos(angle) * speed, Math.sin(angle) * speed,
                        25 + (int)(Math.random() * 12),
                        baseCol.getRed(), baseCol.getGreen(), baseCol.getBlue(),
                        4 + (int)(Math.random() * 6));
        }
    }

    private void addParticle(double x, double y, double vx, double vy,
                              int life, int r, int g, int b, int size) {
        if (particles.size() >= MAX_PARTICLES) return;
        Cercle c = new Cercle(new Couleur(r, g, b), new Point((int)x, (int)y), size, true);
        f.ajouter(c);
        particles.add(c);
        particleData.add(new double[]{vx, vy, life, life, r, g, b, x, y});
    }

    private void updateParticles() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            double[] data = particleData.get(i);
            data[2]--;
            data[7] += data[0];
            data[8] += data[1];
            data[0] *= 0.91;
            data[1] *= 0.91;

            if (data[2] <= 0) {
                f.supprimer(particles.get(i));
                particles.remove(i);
                particleData.remove(i);
            } else {
                double ratio = data[2] / data[3];
                int cr = Math.max(0, Math.min(255, (int)(data[4] * ratio)));
                int cg = Math.max(0, Math.min(255, (int)(data[5] * ratio)));
                int cb = Math.max(0, Math.min(255, (int)(data[6] * ratio)));

                Cercle c = particles.get(i);
                c.setO(new Point((int)data[7], (int)data[8]));
                c.setCouleur(new Couleur(cr, cg, cb));
                int newRadius = Math.max(1, (int)(c.getRayon() * 0.95));
                c.setRayon(newRadius);
            }
        }
    }

    private void spawnDmgText(double px, double py, String text, Couleur col) {
        if (dmgTexts.size() >= MAX_DMG_TEXTS) {
            // Remove oldest
            f.supprimer(dmgTexts.get(0));
            dmgTexts.remove(0);
            dmgTextData.remove(0);
        }
        Texte t = new Texte(col, text, fontMedium, new Point((int)px, (int)py));
        f.ajouter(t);
        dmgTexts.add(t);
        dmgTextData.add(new double[]{px, py, 2.5, 40});
    }

    private void updateDmgTexts() {
        for (int i = dmgTexts.size() - 1; i >= 0; i--) {
            double[] data = dmgTextData.get(i);
            data[1] += data[2];
            data[3]--;
            if (data[3] <= 0) {
                f.supprimer(dmgTexts.get(i));
                dmgTexts.remove(i);
                dmgTextData.remove(i);
            } else {
                dmgTexts.get(i).setA(new Point((int)data[0], (int)data[1]));
                // Fade: change color alpha simulation via dimming
                double fade = data[3] / 40.0;
                Couleur c = dmgTexts.get(i).getCouleur();
                dmgTexts.get(i).setCouleur(new Couleur(
                    (int)(c.getRed() * fade),
                    (int)(c.getGreen() * fade),
                    (int)(c.getBlue() * fade)));
            }
        }
    }

    private void clearDmgTexts() {
        for (Texte t : dmgTexts) f.supprimer(t);
        dmgTexts.clear();
        dmgTextData.clear();
    }

    private void clearAllParticles() {
        for (Cercle c : particles) {
            f.supprimer(c);
        }
        particles.clear();
        particleData.clear();
    }

    // ========== STATUS ==========

    public int getStatus() {
        return status;
    }
}
