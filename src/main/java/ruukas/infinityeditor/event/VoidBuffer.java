package ruukas.infinityeditor.event;

import net.minecraft.network.play.server.SPacketEntityEquipment;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class VoidBuffer {
    int maxSize = 300;

    SPacketEntityEquipment sPacketEntity;
    BlockingDeque<SPacketEntityEquipment> voidConsumerQueue = new LinkedBlockingDeque<SPacketEntityEquipment>(maxSize);

    public SPacketEntityEquipment get() {
        try {
            return voidConsumerQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void put(SPacketEntityEquipment sPacketEntity) {
        this.sPacketEntity = sPacketEntity;
        voidConsumerQueue.offer(sPacketEntity);
    }
}
