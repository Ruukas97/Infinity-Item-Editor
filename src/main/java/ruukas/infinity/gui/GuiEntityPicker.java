package ruukas.infinity.gui;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLiving;

public class GuiEntityPicker extends GuiScreen
{
    private final GuiScreen lastScreen;
    private final List<EntityLiving> entities;
    
    private GuiEntityList entityList;
    private GuiButton buttonCancel, buttonSelect;
    
    protected String title = I18n.format( "gui.entityselect" );
    
    public GuiEntityPicker(GuiScreen last, List<EntityLiving> entities) {
        this.lastScreen = last;
        this.entities = entities;
    }
    
    @Override
    public void initGui()
    {
        entityList = new GuiEntityList( this, mc, eventButton, eventButton, eventButton, eventButton, eventButton );
        buttonCancel = addButton( new GuiButton( 200, width / 2 - 155, height - 29, 150, 20, I18n.format( "gui.cancel" ) ) );
        buttonSelect = addButton( new GuiButton( 201, width / 2 - 155 + 160, height - 29, 150, 20, I18n.format( "gui.select" ) ) );
    }
    
    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        entityList.handleMouseInput();
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 200)
        {
            mc.displayGuiScreen(lastScreen);
        }
        else if (button.id == 201)
        {
            
        }
    }
    
    public List<EntityLiving> getEntities()
    {
        return entities;
    }
    
    public void selectEntity(@Nullable GuiEntityListEntry entry)
    {
        boolean flag = entry != null;
        buttonSelect.enabled = flag;
    }
}
