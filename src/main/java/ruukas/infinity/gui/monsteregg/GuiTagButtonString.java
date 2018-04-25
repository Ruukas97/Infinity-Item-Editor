package ruukas.infinity.gui.monsteregg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTagButtonString extends GuiButton {

	private final MobTagString enumTag;
	private final ItemStack stack;
	
	private final GuiScreen returnGui;

	public GuiTagButtonString(int buttonId, int x, int y, MobTagString enumTag, GuiScreen guiToReturnTo, ItemStack stack) {
		super(buttonId, x, y, 150, 20, MonsterPlacerUtils.getButtonText(enumTag, stack));
		this.enumTag = enumTag;
		this.stack = stack;
		this.returnGui = guiToReturnTo;
	}

	public MobTag returnMobTag() {
		return this.enumTag;
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		if(isMouseOver()){
			Minecraft.getMinecraft().displayGuiScreen(new GuiEditString(returnGui, enumTag, stack));
		}
	}
}
