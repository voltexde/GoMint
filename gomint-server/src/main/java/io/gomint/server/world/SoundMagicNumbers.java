package io.gomint.server.world;

import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
public enum SoundMagicNumbers {

    ITEM_USE_ON( 0 ),
    HIT( 1 ),
    STEP( 2 ),
    JUMP( 3 ),
    BREAK( 4 ),
    PLACE( 5 ),
    HEAVY_STEP( 6 ),
    GALLOP( 7 ),
    FALL( 8 ),
    AMBIENT( 9 ),
    AMBIENT_BABY( 10 ),
    AMBIENT_IN_WATER( 11 ),
    BREATHE( 12 ),
    DEATH( 13 ),
    DEATH_IN_WATER( 14 ),
    DEATH_TO_ZOMBIE( 15 ),
    HURT( 16 ),
    HURT_IN_WATER( 17 ),
    MAD( 18 ),
    BOOST( 19 ),
    BOW( 20 ),
    SQUISH_BIG( 21 ),
    SQUISH_SMALL( 22 ),
    FALL_BIG( 23 ),
    FALL_SMALL( 24 ),
    SPLASH( 25 ),
    FIZZ( 26 ),
    FLAP( 27 ),
    SWIM( 28 ),
    DRINK( 29 ),
    EAT( 30 ),
    TAKEOFF( 31 ),
    SHAKE( 32 ),
    PLOP( 33 ),
    LAND( 34 ),
    SADDLE( 35 ),
    ARMOR( 36 ),
    ADD_CHEST( 37 ),
    THROW( 38 ),
    ATTACK( 39 ),
    ATTACK_NODAMAGE( 40 ),
    WARN( 41 ),
    SHEAR( 42 ),
    MILK( 43 ),
    THUNDER( 44 ),
    EXPLODE( 45 ),
    FIRE( 46 ),
    IGNITE( 47 ),
    FUSE( 48 ),
    STARE( 49 ),
    SPAWN( 50 ),
    SHOOT( 51 ),
    BREAK_BLOCK( 52 ),
    REMEDY( 53 ),
    UNFECT( 54 ),
    LEVELUP( 55 ),
    BOW_HIT( 56 ),
    BULLET_HIT( 57 ),
    EXTINGUISH_FIRE( 58 ),
    ITEM_FIZZ( 59 ),
    CHEST_OPEN( 60 ),
    CHEST_CLOSED( 61 ),
    POWER_ON( 62 ),
    POWER_OFF( 63 ),
    ATTACH( 64 ),
    DETACH( 65 ),
    DENY( 66 ),
    TRIPOD( 67 ),
    POP( 68 ),
    DROP_SLOT( 69 ),
    NOTE( 70 ),
    THORNS( 71 ),
    PISTON_IN( 72 ),
    PISTON_OUT( 73 ),
    PORTAL( 74 ),
    WATER( 75 ),
    LAVA_POP( 76 ),
    LAVA( 77 ),
    BURP( 78 ),
    BUCKET_FILL_WATER( 79 ),
    BUCKET_FILL_LAVA( 80 ),
    BUCKET_EMPTY_WATER( 81 ),
    BUCKET_EMPTY_LAVA( 82 ),
    GUARDIAN_FLOP( 83 ),
    ELDERGUARDIAN_CURSE( 84 ),
    MOB_WARNING( 85 ),
    MOB_WARNING_BABY( 86 ),
    TELEPORT( 87 ),
    SHULKER_OPEN( 88 ),
    SHULKER_CLOSE( 89 ),
    HAGGLE( 90 ),
    HAGGLE_YES( 91 ),
    HAGGLE_NO( 92 ),
    HAGGLE_IDLE( 93 ),
    DEFAULT( 94 ),
    UNDEFINED( 95 );

    @Getter
    private final byte soundId;

    SoundMagicNumbers( int soundId ) {
        this.soundId = (byte) soundId;
    }

    public static SoundMagicNumbers valueOf( byte id ) {
        switch ( id ) {
            case 0:
                return ITEM_USE_ON;
            case 1:
                return HIT;
            case 2:
                return STEP;
            case 3:
                return JUMP;
            case 4:
                return BREAK;
            case 5:
                return PLACE;
            case 6:
                return HEAVY_STEP;
            case 7:
                return GALLOP;
            case 8:
                return FALL;
            case 9:
                return AMBIENT;
            case 10:
                return AMBIENT_BABY;
            case 11:
                return AMBIENT_IN_WATER;
            case 12:
                return BREATHE;
            case 13:
                return DEATH;
            case 14:
                return DEATH_IN_WATER;
            case 15:
                return DEATH_TO_ZOMBIE;
            case 16:
                return HURT;
            case 17:
                return HURT_IN_WATER;
            case 18:
                return MAD;
            case 19:
                return BOOST;
            case 20:
                return BOW;
            case 21:
                return SQUISH_BIG;
            case 22:
                return SQUISH_SMALL;
            case 23:
                return FALL_BIG;
            case 24:
                return FALL_SMALL;
            case 25:
                return SPLASH;
            case 26:
                return FIZZ;
            case 27:
                return FLAP;
            case 28:
                return SWIM;
            case 29:
                return DRINK;
            case 30:
                return EAT;
            case 31:
                return TAKEOFF;
            case 32:
                return SHAKE;
            case 33:
                return PLOP;
            case 34:
                return LAND;
            case 35:
                return SADDLE;
            case 36:
                return ARMOR;
            case 37:
                return ADD_CHEST;
            case 38:
                return THROW;
            case 39:
                return ATTACK;
            case 40:
                return ATTACK_NODAMAGE;
            case 41:
                return WARN;
            case 42:
                return SHEAR;
            case 43:
                return MILK;
            case 44:
                return THUNDER;
            case 45:
                return EXPLODE;
            case 46:
                return FIRE;
            case 47:
                return IGNITE;
            case 48:
                return FUSE;
            case 49:
                return STARE;
            case 50:
                return SPAWN;
            case 51:
                return SHOOT;
            case 52:
                return BREAK_BLOCK;
            case 53:
                return REMEDY;
            case 54:
                return UNFECT;
            case 55:
                return LEVELUP;
            case 56:
                return BOW_HIT;
            case 57:
                return BULLET_HIT;
            case 58:
                return EXTINGUISH_FIRE;
            case 59:
                return ITEM_FIZZ;
            case 60:
                return CHEST_OPEN;
            case 61:
                return CHEST_CLOSED;
            case 62:
                return POWER_ON;
            case 63:
                return POWER_OFF;
            case 64:
                return ATTACH;
            case 65:
                return DETACH;
            case 66:
                return DENY;
            case 67:
                return TRIPOD;
            case 68:
                return POP;
            case 69:
                return DROP_SLOT;
            case 70:
                return NOTE;
            case 71:
                return THORNS;
            case 72:
                return PISTON_IN;
            case 73:
                return PISTON_OUT;
            case 74:
                return PORTAL;
            case 75:
                return WATER;
            case 76:
                return LAVA_POP;
            case 77:
                return LAVA;
            case 78:
                return BURP;
            case 79:
                return BUCKET_FILL_WATER;
            case 80:
                return BUCKET_FILL_LAVA;
            case 81:
                return BUCKET_EMPTY_WATER;
            case 82:
                return BUCKET_EMPTY_LAVA;
            case 83:
                return GUARDIAN_FLOP;
            case 84:
                return ELDERGUARDIAN_CURSE;
            case 85:
                return MOB_WARNING;
            case 86:
                return MOB_WARNING_BABY;
            case 87:
                return TELEPORT;
            case 88:
                return SHULKER_OPEN;
            case 89:
                return SHULKER_CLOSE;
            case 90:
                return HAGGLE;
            case 91:
                return HAGGLE_YES;
            case 92:
                return HAGGLE_NO;
            case 93:
                return HAGGLE_IDLE;
            case 94:
                return DEFAULT;
            case 95:
                return UNDEFINED;
        }

        return null;
    }

}
