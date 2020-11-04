package ruukas.infinityeditor.gui.nbt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ruukas.infinityeditor.gui.GuiNBTAdvanced;
import ruukas.infinityeditor.gui.HelperGui;

public class NBTListRoot extends NBTListCompound
{
    private NBTListCompound tagElement;
    
    private NBTListElement focus = null;
    
    private NBTListElement selected = null;
    private int selX = 0;
    private int selY = 0;
    private int selWidth = 0;
    private int selHeight = 0;
    private NBTOption[] options = null;
    
    public NBTListRoot(ItemStack stack) {
        super( stack.getDisplayName(), null, stack, 30, 50 );
        if ( icon.hasTagCompound() )
        {
            tagElement = new NBTListCompound( "tag", icon.getTagCompound(), false, getX() + 15, getY() + 20 );
            tagElement.parent = this;
        }
    }
    
    public NBTListElement getSelected()
    {
        return selected;
    }
    
    public void setSelected( NBTListElement e, int x, int y )
    {
        selected = e;
        selX = x;
        selY = y;
        
        options = selected.getOptions();
        selWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth( selected.getTypeName() );
        for ( NBTOption o : options )
        {
            selWidth = Math.max( Minecraft.getMinecraft().fontRenderer.getStringWidth( o.getText() ), selWidth );
        }
        
        selHeight = 12 * options.length + 15;
    }
    
    public void clearSelected()
    {
        selected = null;
        selX = 0;
        selY = 0;
        selWidth = 0;
        selHeight = 0;
        options = null;
    }
    
    public void setFocus( NBTListElement e )
    {
        if ( e == this )
            return;
        focus = e;
    }
    
    @Override
    public void redoPositions()
    {
        tagElement.redoPositions();
    }
    
    @Override
    public void drawIcon( RenderItem itemRender )
    {
        super.drawIcon( itemRender );
        
        if ( tagElement != null )
            tagElement.drawIcon( itemRender );
    }
    
    @Override
    public void draw( Minecraft mc, int mouseX, int mouseY )
    {
        drawString( mc.fontRenderer, getText(), getX() + 15, getY() - 5, 0xffffff );
        
        if ( tagElement != null )
        {
            drawVerticalStructureLine( getX(), getY(), 20 );
            drawHorizontalStructureLine( getX() + 2, getY() + 20, 12 );
            tagElement.draw( mc, mouseX, mouseY );
        }
        
        /*
         * if(focus != null){ int width = GuiNBTAdvanced.windowWidth; int height = GuiNBTAdvanced.windowHeight;
         * 
         * int focX = GuiNBTAdvanced.windowX + width/3*2; int focY = GuiNBTAdvanced.windowY + 10;
         * 
         * int focXEnd = width-10; int focYEnd = GuiNBTAdvanced.windowX + height/2 - 10;
         * 
         * drawRect(focX, focY, focXEnd, focYEnd, GuiHelper.getColorFromRGB(180, 50, 50, 50));
         * 
         * int focWidth = focXEnd-focX;
         * 
         * mc.currentScreen.drawCenteredString(mc.fontRenderer, mc.fontRenderer.trimStringToWidth(focus.getTypeName(), focWidth), focX + focWidth/2, 60, GuiHelper.getColorFromRGB(255, 230, 115, 30)); mc.currentScreen.drawString(mc.fontRenderer, mc.fontRenderer.trimStringToWidth("Key: " + focus.getKey(), focWidth), focX + 10, 75, GuiHelper.getColorFromRGB(255, 230, 115, 30)); if(!(focus.tag instanceof NBTTagCompound)){ mc.currentScreen.drawString(mc.fontRenderer, mc.fontRenderer.trimStringToWidth("Value: " + focus.tag.toString(), focWidth), focX + 10, 90, GuiHelper.getColorFromRGB(255, 230, 115, 30)); } }
         */
        
        if ( selected != null )
        {
            drawRect( selX, selY, selX + selWidth + 6, selY + 13, HelperGui.getColorFromRGB( 180, 0, 120, 120 ) );
            mc.currentScreen.drawString( mc.fontRenderer, selected.getTypeName(), selX + 3, selY + 3, HelperGui.getColorFromRGB( 180, 180, 180, 180 ) );
            
            int i = 1;
            for ( NBTOption o : options )
            {
                boolean over = HelperGui.isMouseInRegion( mouseX, mouseY, selX, selY + 13 * i, selWidth + 6, 13 );
                
                drawRect( selX, selY + 13 * i, selX + selWidth + 6, selY + 13 + 13 * i, HelperGui.getColorFromRGB( 180, 30, 30, 30 ) );
                mc.currentScreen.drawString( mc.fontRenderer, o.getText(), selX + 3, selY + 3 + i * 13, over ? HelperGui.getColorFromRGB( 180, 230, 115, 30 ) : HelperGui.getColorFromRGB( 255, 180, 180, 180 ) );
                
                i++;
            }
        }
        
        else if ( focus != null )
        {
            int width = GuiNBTAdvanced.windowWidth;
            int height = GuiNBTAdvanced.windowHeight;
            
            int focX = GuiNBTAdvanced.windowX + width / 3 * 2;
            int focY = GuiNBTAdvanced.windowY + 10;
            
            int focXEnd = width - 10;
            int focYEnd = GuiNBTAdvanced.windowX + height / 2 - 10;
            
            drawRect( focX, focY, focXEnd, focYEnd, HelperGui.getColorFromRGB( 180, 50, 50, 50 ) );
            
            int focWidth = focXEnd - focX;
            
            mc.currentScreen.drawCenteredString( mc.fontRenderer, mc.fontRenderer.trimStringToWidth( focus.getTypeName(), focWidth ), focX + focWidth / 2, 60, HelperGui.getColorFromRGB( 255, 230, 115, 30 ) );
            mc.currentScreen.drawString( mc.fontRenderer, mc.fontRenderer.trimStringToWidth( "Key: " + focus.getKey(), focWidth ), focX + 10, 75, HelperGui.getColorFromRGB( 255, 230, 115, 30 ) );
            if ( !(focus.tag instanceof NBTTagCompound) )
            {
                mc.currentScreen.drawString( mc.fontRenderer, mc.fontRenderer.trimStringToWidth( "Value: " + focus.tag.toString(), focWidth ), focX + 10, 90, HelperGui.getColorFromRGB( 255, 230, 115, 30 ) );
            }
        }
    }
    
    public boolean mouseOverSelected( int mouseX, int mouseY )
    {
        return selected != null && HelperGui.isMouseInRegion( mouseX, mouseY, selX, selY, selWidth, selHeight );
    }
    
    @Override
    public void mouseClicked( int mouseX, int mouseY, int mouseButton )
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        if ( selected != null )
        {
            if ( mouseOverSelected( mouseX, mouseY ) )
            {
                if ( mouseButton == 0 )
                {
                    int y = (mouseY - (selY + 13));
                    if ( y >= 0 )
                        options[y / 13].action();
                }
            }
            else
            {
                clearSelected();
            }
            
            return;
        }
        
        if ( tagElement != null )
            tagElement.mouseClicked( mouseX, mouseY, mouseButton );
    }
    
    @Override
    public NBTOption[] getOptions()
    {
        return new NBTOption[] { getIconStack().hasTagCompound() ? new NBTOption() {
            @Override
            public String getText()
            {
                return "Clear tag";
            }
            
            @Override
            public void action()
            {
                getIconStack().setTagCompound( null );
                tagElement = null;
                clearSelected();
            }
        } : new NBTOption() {
            @Override
            public String getText()
            {
                return "Create tag";
            }
            
            @Override
            public void action()
            {
                getIconStack().setTagCompound( new NBTTagCompound() );
                tagElement = new NBTListCompound( "tag", icon.getTagCompound(), false, getX() + 15, getY() + 20 );
                tagElement.parent = NBTListRoot.this;
                clearSelected();
            }
        } };
    }
}
