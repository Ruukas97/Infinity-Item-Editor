package ruukas.infinity.gui.monsteregg;

import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTagButton extends GuiButton {

	private final MobTag enumTag;

	public GuiTagButton(int buttonId, int x, int y, MobTag enumTag, String buttonText) {
		super(buttonId, x, y, 150, 20, buttonText);
		this.enumTag = enumTag;
	}

	public MobTag returnMobTag() {
		return this.enumTag;
	}
}
