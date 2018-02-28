package io.gomint.server.world.block;

import io.gomint.world.block.BlockType;

import io.gomint.event.entity.EntityDamageEvent;
import io.gomint.server.entity.EntityLiving;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.world.block.BlockFlowingLava;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 10 )
public class FlowingLava extends Liquid implements BlockFlowingLava {

    @Override
    public byte getBlockId() {
        return 10;
    }

    @Override
    public long getBreakTime() {
        return 150000;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public int getTickDiff() {
        return 1500; // Depends on the world, in nether its 10 ticks / otherwise its 30
    }

    @Override
    public boolean isFlowing() {
        return true;
    }

    @Override
    public void onEntityStanding( EntityLiving entityLiving ) {
        EntityDamageEvent damageEvent = new EntityDamageEvent( entityLiving, EntityDamageEvent.DamageSource.LAVA, 4.0f );
        entityLiving.damage( damageEvent );
        entityLiving.setBurning( 15, TimeUnit.SECONDS );
        entityLiving.multiplyFallDistance( 0.5f );
    }

    @Override
    public float getBlastResistance() {
        return 500.0f;
    }

    @Override
    public BlockType getType() {
        return BlockType.FLOWING_LAVA;
    }

}
