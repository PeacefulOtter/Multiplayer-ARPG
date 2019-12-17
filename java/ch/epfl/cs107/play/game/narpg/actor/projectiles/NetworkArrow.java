package ch.epfl.cs107.play.game.narpg.actor.projectiles;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.narpg.actor.NetworkBomb;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.handler.NARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.HashMap;

public class NetworkArrow extends Arrow implements NetworkEntity, NetworkProjectile {
    private final int spawnedBy;
    private Connection connection;
    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param speed
     * @param maxDistance
     */

    public NetworkArrow(Area area, Orientation orientation, DiscreteCoordinates position, Connection connection, int speed, int maxDistance, int spawnedBy) {
        super(area, orientation, position, speed, maxDistance);
        handler = new NetworkArrowHandler();
        this.spawnedBy = spawnedBy;
        this.connection=connection;
    }

    public NetworkArrow(Area area, Orientation orientation, DiscreteCoordinates position, Connection connection, HashMap<String, String> initialState) {
        this(area, orientation, position, connection, Integer.parseInt(initialState.get(stateProperties.SPEED.toString())), Integer.parseInt(initialState.get(stateProperties.MAX_DISTANCE.toString())),
                Integer.parseInt(initialState.get(stateProperties.SPAWNED_BY.toString())));
    }

    @Override
    public int getId() {
        return NetworkEntities.BOW.getClassId();
    }

    @Override
    public Packet00Spawn getSpawnPacket() {
        var initialState = new HashMap<String, String>();
        initialState.put(stateProperties.SPAWNED_BY.toString(),String.valueOf(spawnedBy));
        initialState.put(stateProperties.MAX_DISTANCE.toString(),String.valueOf(getMaxDistance()));
        initialState.put(stateProperties.SPEED.toString(),String.valueOf(getSpeed()));

        return new Packet00Spawn(getId(), NetworkEntities.BOW, getOrientation(), getCurrentMainCellCoordinates(), initialState);
    }

    @Override
    public void updateState(HashMap<String, String> updateMap) {

    }

    @Override
    public void interactWith(Interactable other) {
        if (connection.isServer()) {
            other.acceptInteraction(handler);
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((NARPGInteractionVisitor)v).interactWith(this);
    }

    @Override
    public int getSpawnerId() {
        return spawnedBy;
    }

    private enum stateProperties {
        MAX_DISTANCE("maxDistance"),
        SPEED("speed"),
        SPAWNED_BY("spawnedBy");

        private final String property;

        stateProperties(String spawnedBy) {
            this.property = spawnedBy;
        }

        public String getProperty() {
            return property;
        }
    }

    class NetworkArrowHandler implements NARPGInteractionVisitor {
        @Override
        public void interactWith(NetworkARPGPlayer player) {
            if(player.getId()==spawnedBy) {
                System.out.println("touched owner");
                return;
            }
            player.giveDamage(1f);
            stopProjectile();
        }

        @Override
        public void interactWith(NetworkBomb bomb) {
            stopProjectile();
            bomb.explode();
        }
    }
}
