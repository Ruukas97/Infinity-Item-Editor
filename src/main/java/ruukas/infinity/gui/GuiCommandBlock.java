package ruukas.infinity.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;

public class GuiCommandBlock extends GuiInfinity
{
    public GuiTextField commandField;
    
    protected GuiCommandBlock(GuiScreen lastScreen, ItemStack stack) {
        super( lastScreen, stack );
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
    }   
    
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents( false );
    }
    
    @Override
    public void updateScreen()
    {
        super.updateScreen();
        commandField.updateCursorCounter();
    }
    
    @Override
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        commandField.mouseClicked( mouseX, mouseY, mouseButton );
    }
    
    @Override
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        super.keyTyped( typedChar, keyCode );
        commandField.textboxKeyTyped( typedChar, keyCode );
    }
    
   
    
    @Override
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        super.drawScreen( mouseX, mouseY, partialTicks );
    }

    @Override
    protected String getNameUnlocalized()
    {
        return "commandblock";
    }
}
