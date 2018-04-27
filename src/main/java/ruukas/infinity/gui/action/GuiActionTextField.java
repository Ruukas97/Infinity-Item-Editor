package ruukas.infinity.gui.action;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import ruukas.infinity.gui.HelperGui;

public class GuiActionTextField extends GuiTextField
{
    public Runnable action;
    
    public GuiActionTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super( componentId, fontrendererObj, x, y, par5Width, par6Height );
        
        setDisabledTextColour( HelperGui.MAIN_BLUE );
        setTextColor( HelperGui.MAIN_PURPLE );
    }
    
    @Override
    public void setResponderEntryValue( int idIn, String textIn )
    {
        super.setResponderEntryValue( idIn, textIn );
        
        if ( action != null )
        {
            action.run();
        }
    }
    
    @Override
    public void setText( String textIn )
    {
        super.setText( textIn );
        
        if ( action != null )
        {
            action.run();
        }
    }
}
