/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_jeu.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import static java.lang.Math.round;
import static java.lang.Thread.sleep;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.java.games.input.Component;
import test_jeu.Test_Jeu; 

/**
 * Classe servant à definir notre ecran. Implemente la classe Screen de LibGDX
 *
 * @since 1.0
 * @author jeremy
 */
public class TestGameScreen implements Screen {

    /*écran*/
    private SpriteBatch batch;

    /*Perso*/
    private Texture texture_perso;
    private Map<String, Texture> textures_perso = new TreeMap();
    private Vector2 perso_pos;
    private Rectangle hitbox_perso;
    private int largeur_perso = 20;
    private int hauteur_perso = 70;
    boolean pied = true;
    private enum Direction {

        debut,
        gauche,
        droit,
        stop_gauche,
        stop_droit,
    }
    Direction direction = Direction.debut;
    
    /*méchant*/
    private Texture texture_perso_mechant;
    private Rectangle hitbox_perso_mechant;
    private int nb_collision = 0;
    
    /*saut*/
    private int vitesse_horizontale = 9;
    private int vitesse_verticale = 7;
    private int hauteur_saut = 30;
    boolean phase_montante = true;
    private Vector2 perso_pos_init_saut;
    private boolean saut_active = false;

    /*fond*/
    private Texture texture_fond;
    
    /*écriture*/
    BitmapFont font;
    
    /*Temps*/
    private float temps_prec = 0;
    private float temps = 0;
    
    /*Général*/
    private float movement_time = 100f;

    /**
     * Constructeur de l'ecran.
     *
     * @param application : Test_Jeu
     * @since 1.0
     * @author jeremy
     */
    public TestGameScreen(Test_Jeu application) {
        batch = new SpriteBatch();
        texture_fond = new Texture("img/landscape.png");
        texture_perso = new Texture("img/gd.jpg");
        textures_perso.put("haut", new Texture("img/gd.jpg"));
        textures_perso.put("droit", new Texture("img/gpd.jpg"));
        textures_perso.put("droit_droit", new Texture("img/gpdd.jpg"));
        textures_perso.put("droit_gauche", new Texture("img/gpdg.jpg"));
        textures_perso.put("gauche", new Texture("img/gpg.jpg"));
        textures_perso.put("gauche_droit", new Texture("img/gpgd.jpg"));
        textures_perso.put("gauche_gauche", new Texture("img/gpgg.png"));
        perso_pos = new Vector2(100, 100);
        hitbox_perso = new Rectangle(perso_pos.x, perso_pos.y, largeur_perso, hauteur_perso);
        perso_pos_init_saut = new Vector2(100, 100);
        font = new BitmapFont();//initialisation police d'écriture
        /*méchant*/
        texture_perso_mechant = new Texture("img/gfm.png");
        hitbox_perso_mechant = new Rectangle(500, 100, 10, 16);
    }

    /**
     * methode override servant à afficher l'ecran
     *
     * @since 1.0
     * @author jeremy
     */
    @Override
    public void show() {

    }

    /**
     * methode override servant au rendu de l'ecran
     *
     * @since 1.0
     * @author jeremy
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor((float) 0.5, 1, 0, 1);//couleur de fond
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);//netoie l'écran en recoloriant

        skin();

        batch.begin();//début la zone de dessin

        //dessine le fond
        batch.draw(texture_fond, 0, 0, 1024, 624);

        //batch.draw(texture_perso, 100, 100, 64, 64);//dessine le perso à la position 100,100 de taille 64x64
        batch.draw(texture_perso, perso_pos.x, perso_pos.y);
        
        //dessine le méchant
        batch.draw(texture_perso_mechant, 500, 100, 10, 16);
        
        //dessine le texte
        font.draw(batch, "Test de mouvements :)", 50, Gdx.graphics.getHeight() - 50);
        font.draw(batch, "Collision : "+nb_collision, 50, Gdx.graphics.getHeight() - 75);
            //font.draw(batch, "Temps écoulé : "+temps, 50, Gdx.graphics.getHeight() - 100);
        
        batch.end();//termine la zone de dessin
        
        testHitBoxPerso();
        
        temps = TimeUtils.nanosToMillis(TimeUtils.nanoTime());
        
        if(temps-temps_prec >= movement_time){
            processInput();//gestion des touches
            if(saut_active){
                saut();//gestion du saut
            }
            //à chaque passage dans la boucle, on met à jour le temps du dernier passage
            temps_prec = temps;
        }
        
    }


    /**
     * methode override servant au redimentionnement l'ecran
     *
     * @since 1.0
     * @author jeremy
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * methode override servant à mettre en pause l'ecran
     *
     * @since 1.0
     * @author jeremy
     */
    @Override
    public void pause() {

    }

    /**
     * methode override servant à mettre fin à une pause
     *
     * @since 1.0
     * @author jeremy
     */
    @Override
    public void resume() {

    }

    /**
     * methode override servant à cacher l'ecran
     *
     * @since 1.0
     * @author jeremy
     */
    @Override
    public void hide() {

    }

    /**
     * methode override servant à quitter l'ecran
     *
     * @since 1.0
     * @author jeremy
     */
    @Override
    public void dispose() {

    }
    
    
    /**
     * Gère les skins à afficher.
     *
     * @since 1.0
     * @author jeremy
     */
    private void skin() {

        if (direction == Direction.debut) {
            texture_perso = textures_perso.get("haut");
        } else if (direction == Direction.gauche) {
            if (pied) {
                texture_perso = textures_perso.get("gauche_gauche");
            } else {
                texture_perso = textures_perso.get("gauche_droit");
            }
            pied = !pied;
        } else if (direction == Direction.droit) {
            if (pied) {
                texture_perso = textures_perso.get("droit_gauche");
            } else {
                texture_perso = textures_perso.get("droit_droit");
            }
            pied = !pied;
        } else if (direction == Direction.stop_droit) {
            texture_perso = textures_perso.get("droit");
        } else if (direction == Direction.stop_gauche) {
            texture_perso = textures_perso.get("gauche");
        } else {
            System.out.println("erreur");
        }
    }

    /**
     * Gère les évènements (claviers, souris...)
     *
     * @since 1.0
     * @author jeremy
     */
    private void processInput(){

        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            //on va à droite seulement si l'on est pas en train de sauter
            if(!saut_active){
                direction = Direction.droit;
                perso_pos.x = perso_pos.x + 10;
            }
            
            //teste si saut (apppui de 2 touches en même temps)
            if (Gdx.input.isKeyPressed(Keys.UP)) {
                //mettre deux état : saut droit, saut gauche : direction = Direction.?;
                saut_active = true;
            }
            
        } else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            //on va à gauche seulement si l'on est pas en train de sauter
            if(!saut_active){
                direction = Direction.gauche;
                perso_pos.x = perso_pos.x - 10;
            }
                
            //teste si saut (apppui de 2 touches en même temps)
            if (Gdx.input.isKeyPressed(Keys.UP)) {
                //mettre deux état : saut droit, saut gauche : direction = Direction.?;
                saut_active = true;
            } 
        } else if (Gdx.input.isKeyPressed(Keys.UP)) {
            //mettre deux état : saut droit, saut gauche : direction = Direction.?;
            saut_active = true;
        } else {//si on s'arrête
            //evite de freiner un saut
            if(!saut_active){
                if (direction == Direction.gauche) {
                    direction = Direction.stop_gauche;
                } else if (direction == Direction.droit) {
                    direction = Direction.stop_droit;
                }
            }
        }
        hitbox_perso.setPosition(perso_pos);//met à jour la position de la hitbox
    }
    
    /**
     * Effectue les saut du perso
     *
     * @since 1.0
     * @author jeremy
     */
    private void saut(){
        double coef_acceleration;
        
        coef_acceleration = calcul_acceleration_saut();
        
        
        if(perso_pos.y >99){//à modif (hitbox sol)
            if(phase_montante){
                if(direction == Direction.droit){//à changer avec saut droit
                    perso_pos.x = perso_pos.x + vitesse_horizontale;
                } else if(direction == Direction.gauche){
                    perso_pos.x = perso_pos.x - vitesse_horizontale;
                } else {
                    //on reste sur place
                }
                perso_pos.y = round(perso_pos.y + vitesse_verticale*coef_acceleration);
                
                if(perso_pos.y >= hauteur_saut+perso_pos_init_saut.y){
                    phase_montante = !phase_montante;
                }
            } else {
                if(direction == Direction.droit){//à changer avec saut droit
                    perso_pos.x = perso_pos.x + vitesse_horizontale;
                } else if(direction == Direction.gauche){
                    perso_pos.x = perso_pos.x - vitesse_horizontale;
                } else {
                    //on reste sur place
                }
                perso_pos.y = round(perso_pos.y - vitesse_verticale*coef_acceleration);
            }
        } else {
            phase_montante = true;
            
            //tant que pas de gestion de hitbox
            perso_pos.y = 100;
            
            perso_pos_init_saut.x = perso_pos.x;
            perso_pos_init_saut.y = perso_pos.y;
            saut_active = false;
        }
    }
    
    /**
     * Cette fonction sert à calculer la variation de vitesse durant le saut
     * @return coef : int : coefficient d'accélération lors du saut
     * @since 1.0
     * @author jeremy
     */
    private double calcul_acceleration_saut(){
        double coef = 1;
        
        //on calcule l'acceleration en fonction de la phase de saut
        
        //si on se trouve en-dessous de 1/5e de la hauteur max de saut
        if(perso_pos.y < perso_pos_init_saut.y + hauteur_saut/5){
            coef = 1.2;
        } else if((perso_pos.y < perso_pos_init_saut.y + 2*hauteur_saut/5)&&(perso_pos.y > perso_pos_init_saut.y + hauteur_saut/5)){
            //si on se trouve entre 1/5e et 2/5e
            coef = 1;
        } else if((perso_pos.y < perso_pos_init_saut.y + 3*hauteur_saut/5)&&(perso_pos.y > perso_pos_init_saut.y + 2*hauteur_saut/5)){
            //si on se trouve entre 2/5e et 3/5e
            coef = 0.8;
        } else if((perso_pos.y < perso_pos_init_saut.y + 4*hauteur_saut/5)&&(perso_pos.y > perso_pos_init_saut.y + 3*hauteur_saut/5)){
            //si on se trouve entre 3/5e et 4/5e
            coef = 0.6;
        }
        
        return coef;
    }
    
    /**
     * test les collision avec le personnage
     * @since 1.0
     * @author jeremy
     */
    private void testHitBoxPerso(){
        if(hitbox_perso.overlaps(hitbox_perso_mechant)){
            nb_collision++;
            perso_pos.x = 100;
            perso_pos.y = 100;
            hitbox_perso.setPosition(perso_pos);
            phase_montante = true;
            perso_pos_init_saut.x = 100;
            perso_pos_init_saut.y = 100;
            saut_active = false;
        }
    }
}