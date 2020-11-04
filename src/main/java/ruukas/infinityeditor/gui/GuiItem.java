package ruukas.infinityeditor.gui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinityeditor.Infinity;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.gui.action.ActionButtons;
import ruukas.infinityeditor.gui.action.GuiActionButton;
import ruukas.infinityeditor.gui.action.GuiActionTextField;
import ruukas.infinityeditor.gui.action.GuiInfinityButton;
import ruukas.infinityeditor.gui.action.GuiNumberField;
import ruukas.infinityeditor.nbt.NBTHelper;

@SideOnly( Side.CLIENT )
public class GuiItem extends GuiInfinity implements GuiYesNoCallback {
    private GuiInfinityButton nbtButton, nbtAdvButton, hideFlagsButton;

    // Sidebar buttons
    private GuiInfinityButton sidebarButton;
    private boolean ignoreNextClick = false;

    private GuiInfinityButton headCollectionButton;
    private GuiInfinityButton loadButton;
    private GuiInfinityButton specialButton;
    private GuiInfinityButton discordButton;
    private ArrayList<GuiTextField> textFields = new ArrayList<>();

    private ArrayList<GuiNumberField> numberFields = new ArrayList<>();

    private ArrayList<GuiTextField> loreFields = new ArrayList<>();
    private GuiInfinityButton loreButton;
    private ArrayList<GuiButton> loreButtons = new ArrayList<>();

    private ArrayList<CenterString> centerStrings = new ArrayList<>();
    private ArrayList<DrawString> drawStrings = new ArrayList<>();

    private GuiInfinityButton[] colorButtons;

    private ArrayList<GuiButton> specialButtons = new ArrayList<>();

    private int slot;


    public GuiItem(GuiScreen lastScreen, ItemStackHolder stackHolder, int slot) {
        super( lastScreen, stackHolder );
        hasSave = true;
        this.slot = slot;
    }


    public GuiItem(GuiScreen lastScreen, ItemStackHolder itemStackHolder) {
        super( lastScreen, itemStackHolder );
        this.slot = -1;
    }


    @Override
    protected String getNameUnlocalized() {
        return "item";
    }


    @Override
    protected void reset() {
        super.reset();
        clearCustomName();
    }


    @Override
    protected void save() {
        if (slot < 0) {
            mc.playerController.sendSlotPacket( getItemStack(), mc.player.inventory.currentItem + 36 ); // 36 is the index of the action (4 armor, 1 off hand, 5 crafting, and 27
                                                                                                        // inventory, if I remember correctly).
        }
        else {
            mc.playerController.sendSlotPacket( getItemStack(), slot );
        }
    }


    private static class CenterString {
        private final String string;
        public int yPos;


        public CenterString(String string, int y) {
            this.string = string;
            this.yPos = y;
        }
    }


    private static class DrawString {
        private final String string;
        public int xPos, yPos;


        public DrawString(String string, int x, int y) {
            this.string = string;
            this.xPos = x;
            this.yPos = y;
        }
    }


    @Override
    public void initGui() {
        super.initGui();

        setRenderStack( true, midX, 40, 1.0f );

        boolean sidebarOn = InfinityConfig.getItemSidebar();

        renderTag = !sidebarOn;
        renderTooltip = !sidebarOn;

        Keyboard.enableRepeatEvents( true );

        centerStrings.clear();
        drawStrings.clear();

        numberFields.clear();
        textFields.clear();
        specialButtons.clear();

        int fieldsAmount = 0;

        // ID
        GuiActionTextField itemID = new GuiActionTextField( 250, fontRenderer, midX, 25 + (30 * ++fieldsAmount), 75, 20 );
        String registryName = getItemStack().getItem().getRegistryName().toString();
        itemID.setText( registryName.toLowerCase().startsWith( "minecraft:" ) ? registryName.replaceFirst( "minecraft:", "" ) : registryName );
        itemID.setTextColor( InfinityConfig.MAIN_COLOR );
        itemID.setMaxStringLength( 100 );
        itemID.action = () -> {
            Item item = Item.getByNameOrId( itemID.getText() );
            if (item != null) {
                NBTTagCompound tag = getItemStack().getTagCompound();
                stackHolder.setStack( new ItemStack( item, getItemStack().getCount() == 0 ? 1 : getItemStack().getCount(), getItemStack().getMetadata() ) );
                getItemStack().setTagCompound( tag );
                buttonList.clear();
                initGui();
            }
        };

        textFields.add( itemID );
        centerStrings.add( new CenterString( I18n.format( "gui.item.id" ), 31 + (30 * fieldsAmount) ) );

        // COUNT
        GuiNumberField count = new GuiNumberField( 300 + fieldsAmount, fontRenderer, midX, 25 + (30 * ++fieldsAmount), 20, 20, 2 );
        count.minValue = 1;
        count.maxValue = 64;
        count.setValue( getItemStack().getCount() );
        count.action = () -> getItemStack().setCount( count.getIntValue() );
        numberFields.add( count );
        centerStrings.add( new CenterString( I18n.format( "gui.item.count" ), 31 + (30 * fieldsAmount) ) );

        // META/DAMAGE
        int maxDamage = getItemStack().getItem() instanceof ItemBlock || getItemStack().getMaxDamage() == 0 ? 99999 : getItemStack().getMaxDamage();
        int digits = ("" + maxDamage).length();
        GuiNumberField damage = new GuiNumberField( 300 + fieldsAmount, fontRenderer, width / 2, 25 + (30 * ++fieldsAmount), Math.max( 10 * digits, 15 ), 20, digits );
        damage.minValue = 0;
        damage.maxValue = maxDamage;
        if (getItemStack().getItemDamage() > maxDamage)
            getItemStack().setItemDamage( maxDamage );
        damage.setValue( getItemStack().getItemDamage() );
        damage.action = () -> getItemStack().setItemDamage( damage.getIntValue() );
        numberFields.add( damage );
        centerStrings.add( new CenterString( I18n.format( "gui.item.meta" ), 31 + (30 * fieldsAmount) ) );

        // NBT BROWSER AND EDITOR
        nbtButton = addButton( new GuiInfinityButton( 300 + (fieldsAmount), (width / 2) - 82, 25 + (30 * ++fieldsAmount), 80, 20, I18n.format( "gui.nbt" ) ) );
        nbtAdvButton = addButton( new GuiInfinityButton( 300 + (fieldsAmount), (width / 2) + 2, 25 + (30 * (fieldsAmount)), 80, 20, I18n.format( "gui.nbtadv" ) ) );

        hideFlagsButton = addButton( new GuiInfinityButton( 320, width - 75, 74, 70, 20, I18n.format( "gui.hideflags" ) ) );

        // SIDEBAR BUTTONS
        int sidebarButtonID = 350;

        headCollectionButton = addButton( new GuiInfinityButton( sidebarButtonID++, width / 8 - 40, midY - 80, 80, 20, I18n.format( "gui.headcollection" ) ) );
        headCollectionButton.enabled = sidebarOn;
        headCollectionButton.visible = sidebarOn;

        loadButton = addButton( new GuiInfinityButton( sidebarButtonID++, width / 8 - 40, midY - 45, 80, 20, I18n.format( "gui.pick" ) ) );
        loadButton.enabled = sidebarOn;
        loadButton.visible = sidebarOn;

        sidebarButton = addButton( new GuiInfinityButton( sidebarButtonID++, width / 8 - 40, midY - 10, 80, 20, I18n.format( "gui.item.toggleside" ) ) );
        sidebarButton.enabled = sidebarOn;
        sidebarButton.visible = sidebarOn;

        specialButton = addButton( new GuiInfinityButton( sidebarButtonID++, width / 8 - 40, midY + 25, 80, 20, I18n.format( "gui.specialbutton" ) ) );
        specialButton.enabled = sidebarOn;
        specialButton.visible = sidebarOn;

        discordButton = addButton( new GuiInfinityButton( sidebarButtonID++, width / 8 - 40, midY + 60, 80, 20, I18n.format( "gui.item.discord" ) ) );
        discordButton.enabled = sidebarOn;
        discordButton.visible = sidebarOn;

        // BUTTONS THAT DEPENDS ON THE KIND OF ITEM
        int specialID = 500;
        for (GuiActionButton b : ActionButtons.getActionButtons()) {
            boolean added = b.addOnCondition( specialID, buttonList, stackHolder, (width / 2) - 50, 25 + (30 * (fieldsAmount + (specialID - 499))), 100, 20 );
            if (added)
                specialID++;
        }

        // COLOR BUTTONS
        TextFormatting[] formats = TextFormatting.values();
        int colorAmount = 2 + formats.length;
        colorButtons = new GuiInfinityButton[colorAmount];
        colorButtons[0] = addButton( new GuiInfinityButton( 130, width - 1 - 13 * ((colorAmount + 2) / 2) + (13 * 1), height - 30, 13, 15, formats[0].toString().substring( 0, 1 ) ) );
        colorButtons[1] = addButton( new GuiInfinityButton( 131, width - 1 - 13 * ((colorAmount + 2) / 2) + (13 * 2), height - 30, 13, 15, TextFormatting.DARK_RED + "%" ) );

        for (int i = 2; i < colorAmount; i++) {
            TextFormatting f = formats[i - 2];
            colorButtons[i] = addButton( new GuiInfinityButton( 130 + i, width - 1 - 13 * ((colorAmount + 2) / 2) + (13 * ((i % (colorAmount / 2)) + 1)), height - 30 + (15 * (i / (colorAmount / 2))), 13, 15, f.toString() + f.toString().substring( 1 ) ) );
        }

        // DISPLAY NAME
        int textID = 251;
        drawStrings.add( new DrawString( I18n.format( "gui.item.name" ), width - 110, 35 ) );

        GuiActionTextField name = new GuiActionTextField( textID++, fontRenderer, width - 180, 50, 130, 20 );
        name.setMaxStringLength( 100 );
        name.setText( getItemStack().getDisplayName() );
        name.action = () -> getItemStack().setStackDisplayName( name.getText() );
        textFields.add( name );
        addButton( new GuiInfinityButton( 180, width - 45, 50, 40, 20, I18n.format( "gui.clear" ) ) );

        // LORE
        drawStrings.add( new DrawString( I18n.format( "gui.item.lore" ), width - 110, 80 ) );
        addLoreStuff();
    }


    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents( false );
    }


    public void addLoreStuff() {
        for (GuiButton b : loreButtons) {
            if ((b.id >= 181 && b.id <= 186) || b.id == 260) {
                buttonList.remove( b );
            }
        }

        loreButtons.clear();
        loreFields.clear();
        int id = 251;
        int amount = 0;
        for (int i = 0; i < 5; i++) {
            if (NBTHelper.getLoreLine( getItemStack(), i ) != null) {
                addLoreTextField( id++, i, true );
                amount++;
            }
            else {
                addLoreTextField( id++, i, false ); // Adds one extra line before breaking so there's a field to potentially add an
                                                    // extra line.
                break;
            }
        }
        

        loreButton = (GuiInfinityButton) addIfNotIn( new GuiInfinityButton( 260, width - 180, 0, 170, 20, I18n.format( "gui.lore" ) ), buttonList );
    }


    public void addLoreTextField( int id, int line, boolean active ) {
        GuiActionTextField lore = new GuiActionTextField( id, fontRenderer, width - 180, 100 + (30 * line), 170, 20 );
        lore.setMaxStringLength( 100 );
        String loreLine = NBTHelper.getLoreLine( getItemStack(), line );
        lore.setText( loreLine != null ? NBTHelper.getLoreLine( getItemStack(), line ) : "Lore " + (line + 1) );
        lore.action = () -> {
            NBTHelper.editLoreLine( getItemStack(), line, lore.getText() );
            if (line < 4 && loreFields.size() - 1 == line) {
                addLoreTextField( id + 1, line + 1, false );
            }
            else {
                addIfNotIn( new GuiInfinityButton( 182 + line, width - 195, 100 + 30 * line, 14, 20, TextFormatting.DARK_RED + "X" ), loreButtons );
            }
        };

        loreFields.add( lore );

        if (loreLine != null) {
            addIfNotIn( new GuiInfinityButton( 182 + line, width - 195, 100 + 30 * line, 14, 20, TextFormatting.DARK_RED + "X" ), loreButtons );
        }

        if (loreFields.size() > 1) {
            addIfNotIn( new GuiInfinityButton( 181 + line, width - 195, 100 + 30 * (line - 1), 14, 20, TextFormatting.DARK_RED + "X" ), loreButtons );
        }
    }


    private GuiButton addIfNotIn( GuiButton button, List<GuiButton> list ) {
        for (GuiButton b : list) {
            if (b.id == button.id) {
                return b;
            }
        }

        list.add( addButton( button ) );
        return button;
    }


    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        for (GuiNumberField f : numberFields) {
            f.updateCursorCounter();
        }

        for (GuiTextField f : textFields) {
            f.updateCursorCounter();
        }
    }


    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the
     * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
     * on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped( char typedChar, int keyCode ) throws IOException {
        super.keyTyped( typedChar, keyCode );

        for (int i = 0; i < numberFields.size(); i++) {
            GuiNumberField f = numberFields.get( i );
            if (f != null) {
                f.textboxKeyTyped( typedChar, keyCode );
            }
        }

        for (int i = 0; i < textFields.size(); i++) {
            GuiTextField f = textFields.get( i );
            if (f != null) {
                f.textboxKeyTyped( typedChar, keyCode );
            }
        }

        for (int i = 0; i < loreFields.size(); i++) {
            GuiTextField f = loreFields.get( i );
            if (f != null) {
                f.textboxKeyTyped( typedChar, keyCode );
            }
        }
    }


    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException {
        super.mouseClicked( mouseX, mouseY, mouseButton );

        for (GuiNumberField f : numberFields) {
            f.mouseClicked( mouseX, mouseY, mouseButton );
        }

        for (GuiTextField f : textFields) {
            f.mouseClicked( mouseX, mouseY, mouseButton );
        }

        for (GuiTextField f : loreFields) {
            f.mouseClicked( mouseX, mouseY, mouseButton );
        }

        if (ignoreNextClick) {
            ignoreNextClick = false;
        }
        else if (!InfinityConfig.getItemSidebar() && mouseX < width / 4) {
            InfinityConfig.setItemSidebar( true );
            initGui();
        }
    }


    private void clearCustomName() {
        ItemStack copy = getItemStack().copy();
        copy.clearCustomName();
        textFields.get( 1 ).setText( copy.getDisplayName() );
        getItemStack().clearCustomName();
    }


    @Override
    protected void actionPerformed( GuiButton button ) throws IOException {
        if (button.id == nbtButton.id) {
            this.mc.displayGuiScreen( new GuiNBT( this, getItemStack() ) );
        }

        else if (button.id == nbtAdvButton.id) {
            this.mc.displayGuiScreen( new GuiNBTAdvanced( this, getItemStack() ) );
        }

        else if (button.id == 180) {
            clearCustomName();
        }

        else if (button.id > 180 && button.id < 187) {
            NBTHelper.removeLoreLine( getItemStack(), button.id - 181 - 1 );
            addLoreStuff();
        }

        else if (button.id == 260) {
            this.mc.displayGuiScreen( new GuiLore( this, stackHolder ) );
        }

        else if (button.id >= 130 && button.id < 130 + colorButtons.length) {
            for (int i = 0; i < textFields.size() + loreFields.size(); i++) {
                GuiTextField f = i < textFields.size() ? textFields.get( i ) : loreFields.get( i - textFields.size() );

                if (f.isFocused()) {
                    if (button.id == 130) {
                        f.setText( f.getText().substring( 0, f.getCursorPosition() ) + TextFormatting.values()[0].toString().substring( 0, 1 ) + f.getText().substring( f.getCursorPosition(), f.getText().length() ) );
                    }

                    else if (button.id == 131) {
                        f.setText( TextFormatting.getTextWithoutFormattingCodes( f.getText() ) );
                    }

                    else {
                        f.setText( f.getText().substring( 0, f.getCursorPosition() ) + TextFormatting.values()[button.id - 132] + f.getText().substring( f.getCursorPosition(), f.getText().length() ) );
                    }

                    break;
                }
            }
        }

        else if (button.id == sidebarButton.id) {
            InfinityConfig.setItemSidebar( false );
            ignoreNextClick = true;
            initGui();
        }

        else if (button.id == headCollectionButton.id) {
            mc.displayGuiScreen( new GuiHeadCollection( this ) );
        }

        else if (button.id == loadButton.id) {
            mc.displayGuiScreen( new GuiPick( this, stackHolder ) );
        }

        else if (button.id == specialButton.id) {
            mc.displayGuiScreen( new GuiSpecialButtons( this, stackHolder ) );
        }

        else if (button.id == discordButton.id) {
            try {
                String dLink = "https://discord.gg/PBCvQyy";
                URI uri = new URI( dLink );
                // String s = uri.getScheme();

                if (this.mc.gameSettings.chatLinksPrompt) {
                    this.mc.displayGuiScreen( new GuiConfirmOpenLink( this, dLink, 31102009, true ) );
                }
                else {
                    HelperGui.openWebLink( uri );
                }
            }
            catch (URISyntaxException urisyntaxexception) {
                Infinity.logger.error( "Can't open url for {}", discordButton, urisyntaxexception );
            }
        }

        else if (button.id == hideFlagsButton.id) {
            this.mc.displayGuiScreen( new GuiHideFlags( this, stackHolder ) );
        }

        else
            super.actionPerformed( button );
    }


    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks ) {
        super.drawScreen( mouseX, mouseY, partialTicks );

        //loreButton.drawButton( mc, mouseX, mouseY, partialTicks );
        loreButton.enabled = !getItemStack().isEmpty();
        loreButton.y = 100 + 30 * loreFields.size();

        if (mc.playerController.isNotCreative()) {
            drawCenteredString( fontRenderer, I18n.format( "warning.notcreative" ), width / 2, height - 60, InfinityConfig.CONTRAST_COLOR );
        }

        for (GuiNumberField f : numberFields) {
            f.drawTextBox();
        }

        for (GuiTextField f : textFields) {
            f.drawTextBox();
        }

        for (GuiTextField f : loreFields) {
            f.drawTextBox();
        }

        for (CenterString centerS : centerStrings) {
            this.drawString( this.fontRenderer, centerS.string, this.width / 2 - fontRenderer.getStringWidth( centerS.string ) - 5, centerS.yPos, InfinityConfig.MAIN_COLOR );
        }

        for (DrawString drawS : drawStrings) {
            this.drawString( this.fontRenderer, drawS.string, drawS.xPos, drawS.yPos, InfinityConfig.MAIN_COLOR );
        }

        if (!InfinityConfig.getItemSidebar() && mouseX < width / 4) {
            drawRect( 0, 0, width / 4, height, HelperGui.getColorFromRGB( 30, 100, 100, 250 ) );
            drawCenteredString( fontRenderer, I18n.format( "gui.item.toggleside" ), width / 8, midY - 4, InfinityConfig.CONTRAST_COLOR );
        }

        GuiTextField textField = textFields.get( 0 );

        if (textField.getText().length() > 9) {
            HelperGui.addToolTip( textField.x, textField.y, textField.width, textField.height, mouseX, mouseY, textField.getText() );
        }

        HelperGui.addTooltipTranslated( this.width / 2 + 30, this.height - 35, 60, 20, mouseX, mouseY, "gui.item.drop.tooltip" );

        HelperGui.addTooltipTranslated( colorButtons[1], mouseX, mouseY, "gui.item.colorremove.tooltip" );
    }


    @Override
    public void confirmClicked( boolean result, int id ) {
        if (id == 31102009) {
            if (result) {
                try {
                    HelperGui.openWebLink( new URI( "https://discord.gg/PBCvQyy" ) );
                }
                catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

            this.mc.displayGuiScreen( this );
        }
    }
}
