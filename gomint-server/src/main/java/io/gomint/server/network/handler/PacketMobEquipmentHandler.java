package io.gomint.server.network.handler;

import com.koloboke.collect.ObjCursor;
import io.gomint.entity.Entity;
import io.gomint.inventory.item.ItemStack;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketMobEquipment;

import java.util.function.Predicate;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketMobEquipmentHandler implements PacketHandler<PacketMobEquipment> {

    @Override
    public void handle( PacketMobEquipment packet, long currentTimeMillis, PlayerConnection connection ) {
        // Anti crash checks
        if ( packet.getSelectedSlot() > 8 ) {
            return;
        }

        // Ok the client wants to switch hotbar slot (itemInHand)
        ItemStack wanted = connection.getEntity().getInventory().getItem( packet.getSelectedSlot() );
        if ( wanted != null && wanted.equals( packet.getStack() ) && wanted.getAmount() == packet.getStack().getAmount() ) {
            connection.getEntity().getInventory().updateItemInHandWithItem( packet.getSelectedSlot() );
        }
    }

}
