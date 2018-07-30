package io.gomint.server.inventory.item.generator;

import io.gomint.server.inventory.item.ItemTurtleShell;
import io.gomint.taglib.NBTTagCompound;

public class ItemTurtleShellGenerator implements ItemGenerator {

    @Override
    public ItemTurtleShell generate( short data, byte amount, NBTTagCompound nbt ) {
        return new ItemTurtleShell( data, amount, nbt );
    }

    @Override
    public ItemTurtleShell generate( short data, byte amount ) {
        return new ItemTurtleShell( data, amount );
    }
}
