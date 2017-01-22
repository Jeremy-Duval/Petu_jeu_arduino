/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genuini.handlers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import genuini.screens.GameScreen;

/**
 *
 * @author Adrien
 */
public class ContactHandler implements ContactListener{
    
    private int numFootContacts;
    private Array<Body> bodiesToRemove;
    private boolean bounce;
    private boolean dangerous;
    
    public ContactHandler(){
        super();
        bodiesToRemove = new Array<Body>();
    }
    //Called when 2 fixtures collide
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa == null || fb == null) return;

        if(fa.getUserData() != null && fa.getUserData().equals("foot")) {
                numFootContacts++;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")) {
                numFootContacts++;
        }
        
        if((fa.getUserData() != null && fa.getUserData().equals("foot") && fb.getUserData() != null && fb.getUserData().equals("bounce"))
                || (fa.getUserData() != null && fa.getUserData().equals("bounce") && fb.getUserData() != null && fb.getUserData().equals("foot"))) {
            bounce=true;
        }
        if(fa.getUserData() != null && fa.getUserData().equals("foot") && fb.getUserData() != null && fb.getUserData().equals("spike")
                || fa.getUserData() != null && fa.getUserData().equals("spike") && fb.getUserData() != null && fb.getUserData().equals("foot")) {
            bounce=true;    
            dangerous=true;                  
        }
        
        
        if(fa.getUserData() != null && fa.getUserData().equals("fireball")){
            if(fb.getUserData() != null && fb.getUserData().equals("player")){
                dangerous=true;
            }
        }
        
        if(fb.getUserData() != null && fb.getUserData().equals("fireball")){
            if(fa.getUserData() != null && fa.getUserData().equals("player")){
                dangerous=true;
            }
        }

}
    
    //Called when 2 fixtures no longer collide
    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa == null || fb == null) return;

        if(fa.getUserData() != null && fa.getUserData().equals("foot")) {
                numFootContacts--;       
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")) {
                numFootContacts--;
        }
        
        if(fa.getUserData() != null && fa.getUserData().equals("foot") && fb.getUserData() != null && fb.getUserData().equals("bounce")
                || fa.getUserData() != null && fa.getUserData().equals("bounce") && fb.getUserData() != null && fb.getUserData().equals("foot")) {
                bounce=false;
        }
        if(fa.getUserData() != null && fa.getUserData().equals("foot") && fb.getUserData() != null && fb.getUserData().equals("spike")
                || fa.getUserData() != null && fa.getUserData().equals("spike") && fb.getUserData() != null && fb.getUserData().equals("foot")) {
                bounce=false;
                dangerous=false;
        }

        if(fa.getUserData() != null && fa.getUserData().equals("fireball")){
            if(fb.getUserData() != null && fb.getUserData().equals("player")){
                dangerous=false;
            }
            bodiesToRemove.add(fa.getBody());
        }
        
        if(fb.getUserData() != null && fb.getUserData().equals("fireball")){
            if(fa.getUserData() != null && fa.getUserData().equals("player")){
                dangerous=false;
            }
            bodiesToRemove.add(fb.getBody());
        }
    }
    
    //Before collision handling
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
       
    }
    
    
    //After collision handling
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse){
        
    }
    
    
    public boolean playerCanJump() { return numFootContacts > 0; }
    public Array<Body> getBodies() { return bodiesToRemove; }
    public boolean isBouncy() { return bounce; }
    public boolean isDangerous() { return dangerous; }


    
}
