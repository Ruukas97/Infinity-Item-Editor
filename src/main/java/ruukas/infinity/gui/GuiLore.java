package ruukas.infinity.gui;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import ruukas.infinity.gui.action.GuiActionTextField;
import ruukas.infinity.gui.action.GuiInfinityButton;
import ruukas.infinity.nbt.NBTHelper;

public class GuiLore extends GuiInfinity
{
    private ArrayList<GuiTextField> loreFields = new ArrayList<>();
    private ArrayList<GuiInfinityButton> loreButtons = new ArrayList<>();
    
    private GuiInfinityButton[] colorButtons;
    
    protected GuiLore(GuiScreen lastScreen, ItemStack stack) {
        super( lastScreen, stack );
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        Keyboard.enableRepeatEvents( true );
        setRenderStack( true, midX, 35, 1 );
        
        // COLOR BUTTONS
        TextFormatting[] formats = TextFormatting.values();
        int colorAmount = 2 + formats.length;
        colorButtons = new GuiInfinityButton[ colorAmount ];
        colorButtons[0] = addButton( new GuiInfinityButton( 130, width - 1 - 13 * ((colorAmount + 2) / 2) + (13 * 1), height - 30, 13, 15, formats[0].toString().substring( 0, 1 ) ) );
        colorButtons[1] = addButton( new GuiInfinityButton( 131, width - 1 - 13 * ((colorAmount + 2) / 2) + (13 * 2), height - 30, 13, 15, TextFormatting.DARK_RED + "%" ) );
        for ( int i = 2 ; i < colorAmount ; i++ )
        {
            TextFormatting f = formats[i - 2];
            colorButtons[i] = addButton( new GuiInfinityButton( 130 + i, width - 1 - 13 * ((colorAmount + 2) / 2) + (13 * ((i % (colorAmount / 2)) + 1)), height - 30 + (15 * (i / (colorAmount / 2))), 13, 15, f.toString() + f.toString().substring( 1 ) ) );
        }
        
        // LORE
        addLoreStuff();
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
        for ( GuiTextField f : loreFields )
        {
            f.updateCursorCounter();
        }
    }
    
    @Override
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        for ( GuiTextField f : loreFields )
        {
            f.mouseClicked( mouseX, mouseY, mouseButton );
        }
    }
    
    @Override
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        super.keyTyped( typedChar, keyCode );
        for ( int i = 0 ; i < loreFields.size() ; i++ )
        {
            loreFields.get( i ).textboxKeyTyped( typedChar, keyCode );
        }
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        super.actionPerformed( button );
        
        if ( button.id >= 130 && button.id < 130 + colorButtons.length )
        {
            for ( GuiTextField f : loreFields )
            {
                if ( f.isFocused() )
                {
                    if ( button.id == 130 )
                    {
                        f.setText( f.getText().substring( 0, f.getCursorPosition() ) + TextFormatting.values()[0].toString().substring( 0, 1 ) + f.getText().substring( f.getCursorPosition(), f.getText().length() ) );
                    }
                    
                    else if ( button.id == 131 )
                    {
                        f.setText( TextFormatting.getTextWithoutFormattingCodes( f.getText() ) );
                    }
                    
                    else
                    {
                        f.setText( f.getText().substring( 0, f.getCursorPosition() ) + TextFormatting.values()[button.id - 132] + f.getText().substring( f.getCursorPosition(), f.getText().length() ) );
                    }
                    
                    break;
                }
            }
        }
        
        else if ( button.id >= 750 && button.id <= 771 )
        {
            NBTHelper.removeLoreLine( stack, button.id - 751 );
            addLoreStuff();
        }
    }
    
    @Override
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        for ( GuiTextField f : loreFields )
        {
            f.drawTextBox();
        }
        
        if(HelperGui.isMouseInRegion( mouseX, mouseY, midX-8, 27, 16, 16 )){
            drawHoveringText( stack.getTooltip( mc.player, TooltipFlags.NORMAL ), mouseX, mouseY );
        }
    }
    
    public void addLoreStuff()
    {
        for ( GuiButton b : loreButtons )
        {
            if ( (b.id >= 750 && b.id <= 771) || b.id == 260 )
            {
                buttonList.remove( b );
            }
        }
        
        loreButtons.clear();
        loreFields.clear();
        int id = 500;
        
        for ( int i = 0 ; i < 21 ; i++ )
        {
            if ( NBTHelper.getLoreLine( stack, i ) != null )
                addLoreTextField( id++, i, true );
            else
            {
                addLoreTextField( id++, i, false ); // Adds one extra line before breaking so there's a field to potentially add an extra line.
                break;
            }
        }
    }
    
    public void addLoreTextField( int id, int line, boolean active )
    {
        int sliceW = width / 4;
        int x = sliceW * ((line % 3) + 1) - 60;
        int y = 50 + (30 * (line/3));
        
        GuiActionTextField lore = new GuiActionTextField( id, fontRenderer, x, y, 120, 20 );
        lore.setMaxStringLength( 100 );
        lore.setText( NBTHelper.getLoreLine( stack, line ) != null ? NBTHelper.getLoreLine( stack, line ) : "Lore" + (line + 1) );
        lore.action = () -> {
            NBTHelper.editLoreLine( stack, line, lore.getText() );
            if ( line < 20 && loreFields.size() - 1 == line )
            {
                addLoreTextField( id + 1, line + 1, false );
            }
            else{
                int xPos = sliceW * (((line) % 3) + 1) - 60;
                int yPos = 50 + (30 * ((line)/3));
                addIfNotIn( new GuiInfinityButton( 750 + line + 1, xPos-15, yPos, 14, 20, TextFormatting.DARK_RED + "X" ), loreButtons  );
            }
        };
        loreFields.add( lore );
        
        if ( loreFields.size() > 1 )
        {
            x = sliceW * (((line-1) % 3) + 1) - 60;
            y = 50 + (30 * ((line-1)/3));
            addIfNotIn( new GuiInfinityButton( 750 + line, x-15, y, 14, 20, TextFormatting.DARK_RED + "X" ), loreButtons ) ;
        }
    }
    
    private void addIfNotIn( GuiInfinityButton button, ArrayList<GuiInfinityButton> list )
    {
        boolean exists = false;
        for ( GuiInfinityButton b : loreButtons )
        {
            if ( b.id == button.id )
            {
                exists = true;
                break;
            }
        }
        
        if ( !exists )
        {
            list.add( addButton( button ) );
        }
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "lore";
    }
    
}
