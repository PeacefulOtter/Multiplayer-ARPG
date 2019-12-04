package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

public class CastleDoor extends Door {
    private Sprite[] sprites;
    public CastleDoor(Area area) {
        super("zelda/Chateau", new DiscreteCoordinates(7,1), Logic.FALSE, area, Orientation.UP, new DiscreteCoordinates(9,13),new DiscreteCoordinates(10,13));
        sprites = new Sprite[2];
        sprites[0]= new Sprite("zelda/castleDoor.close",2,2,this, new RegionOfInterest(0,0,32,32), Vector.ZERO,1,-100f);
        sprites[1]= new Sprite("zelda/castleDoor.open",2,2,this, new RegionOfInterest(0,0,32,32), Vector.ZERO,1,-100f);
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    public void passDoor(){
        setSignal(Logic.FALSE);
    }
    public void openDoor(){
        setSignal(Logic.TRUE);
    }
    @Override
    public void draw(Canvas canvas) {
        if(isOpen()){
            sprites[1].draw(canvas);
        }else{
            sprites[0].draw(canvas);
        }
    }

}
