package ruukas.infinity.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import ruukas.infinity.gui.action.ActionButtons;
import ruukas.infinity.gui.action.GuiActionButton;

public class GuiSpecialButtons extends GuiInfinity
{
    
    protected GuiSpecialButtons(GuiScreen lastScreen, ItemStack stack) {
        super( lastScreen, stack );
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        
        int specialID = 500;
        int added = 0;
        GuiActionButton[] array = ActionButtons.getActionButtons();
        List<GuiActionButton> buttons = new ArrayList<>();
        
        for(GuiActionButton b : array){
            buttons.add( b );
        }
        
        buttons.sort( new ActionButtons.SorterGuiActionButton() );
        
        int maxInColumn = (height - 90) / 30;
        int columns = (int) Math.ceil( ((double) array.length) / ((double) maxInColumn) );
        
        int xUnits = width / columns;
        
        for ( GuiActionButton b : buttons )
        {
            
            b.add( specialID+added, buttonList, stack, (xUnits * (added/maxInColumn)) + xUnits/2 - 50, 30 + (30 * (added%maxInColumn)), 100, 20 );
            added++;
        }
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "specialbutton";
    }
    
}
