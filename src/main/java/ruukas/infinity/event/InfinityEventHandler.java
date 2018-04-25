package ruukas.infinity.event;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import ruukas.infinity.Infinity;
import ruukas.infinity.gui.HelperGui;
import ruukas.infinity.gui.GuiItem;

@Mod.EventBusSubscriber(modid = Infinity.MODID)
public class InfinityEventHandler {

	
	@SubscribeEvent()
	public static void onKeyPress(KeyInputEvent event) {
		if (Infinity.keybind.isPressed() && Minecraft.getMinecraft().world != null) {
			ItemStack currentStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
			
			if(currentStack == null || currentStack == ItemStack.EMPTY){
				return;
			}
			
			Minecraft.getMinecraft().displayGuiScreen(new GuiItem(Minecraft.getMinecraft().currentScreen, currentStack.copy()));
		}
	}
	
	/**
	 * This registers the background that's used for the main hand slot in the Equipment GUI of armor stands
	 * @param event
	 */
	@SubscribeEvent
	public static void textureStich(TextureStitchEvent.Pre event) {
	    event.getMap().registerSprite(HelperGui.EMPTY_ARMOR_SLOT_SWORD);
	}
}
