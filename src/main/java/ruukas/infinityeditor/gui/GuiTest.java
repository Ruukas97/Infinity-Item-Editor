package ruukas.infinityeditor.gui;

import net.minecraft.client.gui.GuiScreen;

public class GuiTest extends GuiInfinity {

    protected GuiTest(GuiScreen lastScreen, ItemStackHolder itemStackHolder) {
        super(lastScreen, itemStackHolder);
    }

    @Override
    protected String getNameUnlocalized() {
        return "test";
    }

    @Override
    public void initGui() {
        super.initGui();


    }



}
