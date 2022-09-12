package ruukas.infinityeditor.event;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import ruukas.infinityeditor.data.thevoid.VoidController;

public class VoidConsumer implements Runnable {
    VoidBuffer buffer;

    public VoidConsumer(VoidBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {

        while (true) {
            SPacketEntityEquipment packet = buffer.get();
            if (packet==null ) continue;
            if (Minecraft.getMinecraft().world != null) {
                ItemStack stack = packet.getItemStack().copy();
                Entity ent = Minecraft.getMinecraft().world.getEntityByID(packet.getEntityID());
                String uuid = null;
                if (ent instanceof EntityPlayer) {
                    uuid = ent.getUniqueID().toString().replace("-", "");
                }
                new VoidController(stack).addItemStack(Minecraft.getMinecraft().player, stack, uuid);
            }
        }
    }
}
