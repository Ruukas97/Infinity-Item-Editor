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
import ruukas.infinity.nbt.itemstack.tag.blockentitytag.InfinitySignTag;

public class GuiSign extends GuiInfinity
{
    private ArrayList<GuiTextField> signFields = new ArrayList<>();
    
    private GuiInfinityButton[] colorButtons;
    
    public GuiSign(GuiScreen lastScreen, ItemStack stack) {
        super( lastScreen, stack );
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        Keyboard.enableRepeatEvents( true );
        setRenderStack( true, midX, 35, 1 );
        renderTag = true;
        
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
        
        InfinitySignTag tag = new InfinitySignTag( stack );
        
        for ( int i = 0 ; i < 4 ; i++ )
        {
            GuiActionTextField line = new GuiActionTextField( 500 + i, fontRenderer, midX-60, 60 + 30 * i, 120, 20 );
            line.setMaxStringLength( 100 );
            line.setText( tag.hasLine( i ) && tag.getLine( i ) != null ? tag.getLineFormatted( i ) : "Line" + (i + 1) );
            line.action = () -> {
                tag.setLineUnformatted( line.getId() - 500, line.getText() );
            };
            signFields.add( line );
        }
        
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
        for ( GuiTextField f : signFields )
        {
            f.updateCursorCounter();
        }
    }
    
    @Override
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        for ( GuiTextField f : signFields )
        {
            f.mouseClicked( mouseX, mouseY, mouseButton );
        }
    }
    
    @Override
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        super.keyTyped( typedChar, keyCode );
        for ( int i = 0 ; i < signFields.size() ; i++ )
        {
            signFields.get( i ).textboxKeyTyped( typedChar, keyCode );
        }
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        super.actionPerformed( button );
        
        if ( button.id >= 130 && button.id < 130 + colorButtons.length )
        {
            for ( GuiTextField f : signFields )
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
    }
    
    @Override
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        for ( GuiTextField f : signFields )
        {
            f.drawTextBox();
        }
        
        if ( HelperGui.isMouseInRegion( mouseX, mouseY, midX - 8, 27, 16, 16 ) )
        {
            drawHoveringText( stack.getTooltip( mc.player, TooltipFlags.NORMAL ), mouseX, mouseY );
        }
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "sign";
    }
    
}
