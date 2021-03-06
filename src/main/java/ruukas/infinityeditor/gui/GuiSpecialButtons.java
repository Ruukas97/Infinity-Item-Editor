package ruukas.infinityeditor.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import ruukas.infinityeditor.gui.action.ActionButtons;
import ruukas.infinityeditor.gui.action.GuiActionButton;

public class GuiSpecialButtons extends GuiInfinity
{
    
    protected GuiSpecialButtons(GuiScreen lastScreen, ItemStackHolder stackHolder) {
        super( lastScreen, stackHolder );
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        
        int specialID = 500;
        int added = 0;
        GuiActionButton[] array = ActionButtons.getActionButtons();
        List<GuiActionButton> buttons = new ArrayList<>();

        Collections.addAll(buttons, array);
        
        buttons.sort( new ActionButtons.SorterGuiActionButton() );
        
        int maxInColumn = (height - 90) / 30;
        int columns = (int) Math.ceil( ((double) array.length) / ((double) maxInColumn) );
        
        int xUnits = width / columns;
        
        for ( GuiActionButton b : buttons )
        {
            
            b.add( specialID + added, buttonList, stackHolder, (xUnits * (added / maxInColumn)) + xUnits / 2 - 50, 30 + (30 * (added % maxInColumn)), 100, 20 );
            added++;
        }
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "specialbutton";
    }
    
}
