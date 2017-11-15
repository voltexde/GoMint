package io.gomint.server.util;

import io.gomint.server.world.GamemodeMagicNumbers;
import io.gomint.server.world.ParticleMagicNumbers;
import io.gomint.server.world.SoundMagicNumbers;
import io.gomint.world.Gamemode;
import io.gomint.world.Particle;
import io.gomint.world.Sound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EnumConnectors {

    public static final EnumConnector<Gamemode, GamemodeMagicNumbers> GAMEMODE_CONNECTOR = new EnumConnector<>( Gamemode.class, GamemodeMagicNumbers.class );
    public static final EnumConnector<Sound, SoundMagicNumbers> SOUND_CONNECTOR = new EnumConnector<>( Sound.class, SoundMagicNumbers.class );
    public static final EnumConnector<Particle, ParticleMagicNumbers> PARTICLE_CONNECTOR = new EnumConnector<>( Particle.class, ParticleMagicNumbers.class );

}
