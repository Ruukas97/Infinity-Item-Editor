package ruukas.infinity.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import ruukas.infinity.gui.action.GuiInfinityButton;

public abstract class GuiInfinity extends GuiScreen{
	
	protected final GuiScreen lastScreen;
	protected ItemStack stack = ItemStack.EMPTY;

	protected GuiInfinityButton backButton, resetButton, dropButton;
	
	protected GuiInfinity(GuiScreen lastScreen, ItemStack stack) {
		this.lastScreen = lastScreen;
		this.stack = stack;
	}
}
