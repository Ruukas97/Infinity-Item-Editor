package ruukas.infinity.gui;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.gui.action.ActionButtons;
import ruukas.infinity.gui.action.GuiActionButton;
import ruukas.infinity.gui.action.GuiActionTextField;
import ruukas.infinity.gui.action.GuiInfinityButton;
import ruukas.infinity.gui.action.GuiNumberField;
import ruukas.infinity.nbt.NBTHelper;

@SideOnly( Side.CLIENT )
public class GuiItem extends GuiInfinity
{
    private GuiInfinityButton nbtButton, nbtAdvButton;
    
    private ArrayList<GuiTextField> textFields = new ArrayList<>();
    
    private ArrayList<GuiNumberField> numberFields = new ArrayList<>();
    
    private ArrayList<GuiTextField> loreFields = new ArrayList<>();
    
    private ArrayList<CenterString> centerStrings = new ArrayList<>();
    private ArrayList<DrawString> drawStrings = new ArrayList<>();
    
    private GuiInfinityButton[] colorButtons;
    
    private ArrayList<GuiInfinityButton> specialButtons = new ArrayList<>();
    
    public GuiItem(GuiScreen lastScreen, ItemStack stack) {
        super( lastScreen, stack );
        hasSave = true;
        renderTag = true;
        renderTooltip = true;
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "item";
    }
    
    @Override
    protected void reset()
    {
        super.reset();
        clearCustomName();
    }
    
    private static class CenterString
    {
        private final String string;
        public int yPos;
        
        public CenterString(String string, int y) {
            this.string = string;
            this.yPos = y;
        }
    }
    
    // TODO use GuiLabel instead
    private static class DrawString
    {
        private final String string;
        public int xPos, yPos;
        
        public DrawString(String string, int x, int y) {
            this.string = string;
            this.xPos = x;
            this.yPos = y;
        }
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        setRenderStack( true, midX, 40, 1.0f );
        
        Keyboard.enableRepeatEvents( true );
        
        centerStrings.clear();
        drawStrings.clear();
        
        numberFields.clear();
        textFields.clear();
        specialButtons.clear();
        
        int fieldsAmount = 0;
        
        // ID
        GuiActionTextField itemID = new GuiActionTextField( 250, fontRenderer, width / 2, 25 + (30 * ++fieldsAmount), 75, 20 );
        String registryName = stack.getItem().getRegistryName().toString();
        itemID.setText( registryName.toLowerCase().startsWith( "minecraft:" ) ? registryName.replaceFirst( "minecraft:", "" ) : registryName );
        itemID.setTextColor( HelperGui.MAIN_PURPLE );
        itemID.setMaxStringLength( 100 );
        itemID.action = () -> {
            Item item = Item.getByNameOrId( itemID.getText() );
            if ( item != null )
            {
                NBTTagCompound tag = stack.getTagCompound();
                stack = new ItemStack( item, stack.getCount() == 0 ? 1 : stack.getCount(), stack.getMetadata() );
                stack.setTagCompound( tag );
                itemID.setTextColor( HelperGui.MAIN_PURPLE );
                buttonList.clear();
                initGui();
            }
            else
            {
                itemID.setTextColor( HelperGui.MAIN_BLUE );
            }
        };
        
        textFields.add( itemID );
        centerStrings.add( new CenterString( "Item ID", 31 + (30 * fieldsAmount) ) );
        
        // COUNT
        GuiNumberField count = new GuiNumberField( 300 + fieldsAmount, fontRenderer, width / 2, 25 + (30 * ++fieldsAmount), 20, 20, 2 );
        count.minValue = 1;
        count.maxValue = 64;
        count.setValue( stack.getCount() );
        count.action = () -> stack.setCount( count.getIntValue() );
        numberFields.add( count );
        centerStrings.add( new CenterString( "Count", 31 + (30 * fieldsAmount) ) );
        
        // META/DAMAGE
        int maxDamage = stack.getItem() instanceof ItemBlock || stack.getMaxDamage() == 0 ? 15 : stack.getMaxDamage();
        int digits = ("" + maxDamage).length();
        GuiNumberField damage = new GuiNumberField( 300 + fieldsAmount, fontRenderer, width / 2, 25 + (30 * ++fieldsAmount), Math.max( 10 * digits, 15 ), 20, digits );
        damage.minValue = 0;
        damage.maxValue = maxDamage;
        if ( stack.getItemDamage() > maxDamage )
            stack.setItemDamage( maxDamage );
        damage.setValue( stack.getItemDamage() );
        damage.action = () -> stack.setItemDamage( damage.getIntValue() );
        numberFields.add( damage );
        centerStrings.add( new CenterString( "Meta Data", 31 + (30 * fieldsAmount) ) );
        
        // NBT BROWSER AND EDITOR
        nbtButton = addButton( new GuiInfinityButton( 300 + (fieldsAmount), (width / 2) - 50, 25 + (30 * ++fieldsAmount), 100, 20, I18n.format( "gui.nbt" ) ) );
        nbtAdvButton = addButton( new GuiInfinityButton( 300 + (fieldsAmount), (width / 2) - 50, 25 + (30 * ++fieldsAmount), 100, 20, I18n.format( "gui.nbtadv" ) ) );
        
        // BUTTONS THAT DEPENDS ON THE KIND OF ITEM
        int specialID = 500;
        for ( GuiActionButton b : ActionButtons.getActionButtons() )
        {
            boolean added = b.addOnCondition( specialID, buttonList, stack, (width / 2) - 50, 25 + (30 * (fieldsAmount + (specialID - 499))), 100, 20 );
            if ( added )
                specialID++;
        }
        
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
        
        // DISPLAY NAME
        int textID = 251;
        drawStrings.add( new DrawString( "Name", width - 110, 35 ) );
        
        GuiActionTextField name = new GuiActionTextField( textID++, fontRenderer, width - 180, 50, 130, 20 );
        name.setMaxStringLength( 100 );
        name.setText( stack.getDisplayName() );
        name.action = () -> stack.setStackDisplayName( name.getText() );
        textFields.add( name );
        addButton( new GuiInfinityButton( 180, width - 45, 50, 40, 20, I18n.format( "gui.clear" ) ) );
        
        // LORE
        drawStrings.add( new DrawString( "Lore", width - 110, 80 ) );
        addLoreStuff();
    }
    
    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents( false );
    }
    
    public void addLoreStuff()
    {
        for ( GuiButton b : buttonList )
        {
            if ( b.id >= 181 && b.id <= 186 )
            {
                buttonList.remove( b );
            }
        }
        
        loreFields.clear();
        int id = 251;
        
        for ( int i = 0 ; i < 6 ; i++ )
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
    
    // TODO hide button to remove line, if the line doesn't exist (use active boolean)
    public void addLoreTextField( int id, int line, boolean active )
    {
        GuiActionTextField lore = new GuiActionTextField( id, fontRenderer, width - 180, 100 + (30 * line), 170, 20 );
        lore.setMaxStringLength( 100 );
        lore.setText( NBTHelper.getLoreLine( stack, line ) != null ? NBTHelper.getLoreLine( stack, line ) : "Lore" + (line + 1) );
        lore.action = () -> {
            NBTHelper.editLoreLine( stack, line, lore.getText() );
            if ( line < 5 && loreFields.size() - 1 == line )
            {
                addLoreTextField( id + 1, line + 1, false );
            }
        };
        loreFields.add( lore );
        
        addButton( new GuiInfinityButton( 181 + line, width - 195, 100 + (30 * line), 14, 20, TextFormatting.DARK_RED + "X" ) );
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        for ( GuiNumberField f : numberFields )
        {
            f.updateCursorCounter();
        }
        
        for ( GuiTextField f : textFields )
        {
            f.updateCursorCounter();
        }
    }
    
    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        super.keyTyped( typedChar, keyCode );
        
        for ( int i = 0 ; i < numberFields.size() ; i++ )
        {
            GuiNumberField f = numberFields.get( i );
            f.textboxKeyTyped( typedChar, keyCode );
        }
        
        for ( int i = 0 ; i < textFields.size() ; i++ )
        {
            GuiTextField f = textFields.get( i );
            f.textboxKeyTyped( typedChar, keyCode );
        }
        
        for ( int i = 0 ; i < loreFields.size() ; i++ )
        {
            GuiTextField f = loreFields.get( i );
            f.textboxKeyTyped( typedChar, keyCode );
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        for ( GuiNumberField f : numberFields )
        {
            f.mouseClicked( mouseX, mouseY, mouseButton );
        }
        
        for ( GuiTextField f : textFields )
        {
            f.mouseClicked( mouseX, mouseY, mouseButton );
        }
        
        for ( GuiTextField f : loreFields )
        {
            f.mouseClicked( mouseX, mouseY, mouseButton );
        }
    }
    
    private void clearCustomName()
    {
        ItemStack copy = stack.copy();
        copy.clearCustomName();
        textFields.get( 1 ).setText( copy.getDisplayName() );
        stack.clearCustomName();
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        super.actionPerformed( button );
        if ( button.id == nbtButton.id )
        {
            this.mc.displayGuiScreen( new GuiNBT( this, stack ) );
        }
        
        else if ( button.id == nbtAdvButton.id )
        {
            this.mc.displayGuiScreen( new GuiNBTAdvanced( this, stack ) );
        }
        
        else if ( button.id == 180 )
        {
            clearCustomName();
        }
        
        else if ( button.id > 180 && button.id < 187 )
        {
            NBTHelper.removeLoreLine( stack, button.id - 181 );
            addLoreStuff();
        }
        
        else if ( button.id >= 130 && button.id < 130 + colorButtons.length )
        {
            for ( int i = 0 ; i < textFields.size() + loreFields.size() ; i++ )
            {
                GuiTextField f = i < textFields.size() ? textFields.get( i ) : loreFields.get( i - textFields.size() );
                
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
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        if ( mc.playerController.isNotCreative() && !mc.isSingleplayer())
        {
            drawCenteredString( fontRenderer, I18n.format( "warning.notcreative" ), width / 2, height - 60, HelperGui.MAIN_BLUE );
        }
        
        for ( GuiNumberField f : numberFields )
        {
            f.drawTextBox();
        }
        
        for ( GuiTextField f : textFields )
        {
            f.drawTextBox();
        }
        
        for ( GuiTextField f : loreFields )
        {
            f.drawTextBox();
        }
        
        for ( CenterString centerS : centerStrings )
        {
            this.drawString( this.fontRenderer, centerS.string, this.width / 2 - fontRenderer.getStringWidth( centerS.string ) - 5, centerS.yPos, HelperGui.MAIN_PURPLE );
        }
        
        for ( DrawString drawS : drawStrings )
        {
            this.drawString( this.fontRenderer, drawS.string, drawS.xPos, drawS.yPos, HelperGui.MAIN_PURPLE );
        }
        
        GuiTextField textField = textFields.get( 0 );
        HelperGui.addTooltipTranslated( textField.x, textField.y, textField.width, textField.height, mouseX, mouseY, "gui.item.id.tooltip" );
        
        HelperGui.addTooltipTranslated( this.width / 2 + 30, this.height - 35, 60, 20, mouseX, mouseY, "gui.item.drop.tooltip" );
        
        HelperGui.addTooltipTranslated( colorButtons[1], mouseX, mouseY, "gui.item.colorremove.tooltip" );
    }
    
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
    public ItemStack getItemStack()
    {
        return stack;
    }
}
