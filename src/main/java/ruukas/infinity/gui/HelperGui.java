package ruukas.infinity.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import ruukas.infinity.Infinity;

public class HelperGui {
    public static final ResourceLocation EMPTY_ARMOR_SLOT_SWORD = new ResourceLocation(Infinity.MODID, "items/empty_armor_slot_sword");
    public static final int TITLE_PURPLE = 0xed36bf;
    public static final int MAIN_PURPLE = getColorFromRGB(255, 150, 0, 200);
    public static final int ALT_PURPLE = HelperGui.getColorFromRGB(255, 50, 20, 75);
    public static final int MAIN_BLUE = HelperGui.getColorFromRGB(255, 0, 100, 255);
    public static final int GOOD_GREEN = 0x52b738;
    public static final int BAD_RED = 0xf44262;
    
	
	@Nullable
	public static GuiScreen getCurrentScreen(){
		return Minecraft.getMinecraft().currentScreen;
	}
	
	public static boolean isMouseInRegion(int mouseX, int mouseY, int xPos, int yPos, int width, int height){
		return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
	}
	
	public static void addTooltipTranslated(GuiButton button, int mouseX, int mouseY, String str){
		if(button != null && button.enabled && button.visible){
			addTooltipTranslated(button.x, button.y, button.width, button.height, mouseX, mouseY, str);
		}
	}
	
	public static void addTooltip(GuiButton button, int mouseX, int mouseY, String... str){
		if(button != null && button.enabled && button.visible){
			addToolTip(button.x, button.y, button.width, button.height, mouseX, mouseY, str);
		}
	}
	
	public static void addTooltipTranslated(int xPos, int yPos, int width, int height, int mouseX, int mouseY, String str){
		List<String> strings = new ArrayList<>();

		for(int i=1;i<10;i++){
			String s = (str+"."+i);
			if(I18n.hasKey(s)){
				strings.add(I18n.format(s));
			}
			else{
				break;
			}
		}
		
		if(!strings.isEmpty()){
			addToolTip(xPos, yPos, width, height, mouseX, mouseY, strings.toArray(new String[strings.size()]));
		}else{
			addToolTip(xPos, yPos, width, height, mouseX, mouseY, "missing localization: " + str);
		}
	}
	
	public static void addToolTip(int xPos, int yPos, int width, int height, int mouseX, int mouseY, String... str){
		if(isMouseInRegion(mouseX, mouseY, xPos, yPos, width, height)){
			if(str.length == 1){
				getCurrentScreen().drawHoveringText(str[0], mouseX, mouseY);
			}
			else{
				List<String> strings = new ArrayList<>();
				
				for(String s : str){
					strings.add(s);
				}
				
				getCurrentScreen().drawHoveringText(strings, mouseX, mouseY);
			}
		}
	}

    public static InventoryPlayer getInventoryPlayerCopy(InventoryPlayer source)
    {
    	InventoryPlayer dest = new InventoryPlayer(source.player);
    	
        for (int i = 0; i < source.getSizeInventory(); ++i)
        {
        	dest.setInventorySlotContents(i, source.getStackInSlot(i).copy());
        }

        dest.currentItem = source.currentItem;
        
        return dest;
    }
    
    public static void dropStack(ItemStack stack){
		if (!stack.isEmpty()) {
			Minecraft.getMinecraft().player.inventory.player.dropItem(stack, true);
			Minecraft.getMinecraft().playerController.sendPacketDropItem(stack);
		}
    }
    
    public static int getColorFromRGB(int alpha, int red, int green, int blue){
		int color = alpha << 24;
		color += red << 16;
		color += green << 8;
		color += blue;
		return color;
    }
}
