package ruukas.infinityeditor.gui;

import com.google.common.base.Predicate;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.gui.action.GuiActionTextField;
import ruukas.infinityeditor.gui.action.GuiInfinityButton;
import ruukas.infinityeditor.gui.action.GuiNumberField;
import ruukas.infinityeditor.nbt.itemstack.tag.InfinityEnchantmentList;
import ruukas.infinityeditor.nbt.itemstack.tag.ench.InfinityEnchantmentTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiEnchanting extends GuiInfinity {
    private boolean showAll = false;
    private GuiInfinityButton enchantToggleButton;
    private GuiNumberField level;
    private GuiActionTextField filterTextField;

    private String filter;
    private int rotOff = 0;
    private int mouseDist = 0;

    private ItemStack enchantBook;
    private final List<Enchantment> enchants = new ArrayList<>();
    private List<Enchantment> filteredEnchants = new ArrayList<>();
    private String toggleButtonText = "";

    private final Predicate<String> textFilter = text -> text.matches("[a-z]*");


    public static class EnchantComparator implements Comparator<Enchantment> {

        @Override
        public int compare(Enchantment o1, Enchantment o2) {
            return o1.getTranslatedName(1).compareToIgnoreCase(o2.getTranslatedName(1));
        }
        
    }
    
    public GuiEnchanting(GuiScreen lastScreen, ItemStackHolder stackHolder) {
        super( lastScreen, stackHolder );
    }
    
    @Override
    public void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(true);
        enchantToggleButton = addButton(new GuiInfinityButton(150, 15, height - 63, 90, 20, toggleButtonText));
        updateButtonText();
        filterTextField = new GuiActionTextField(101, fontRenderer, width - 15 - 100, height - 33, 100, 18);
        filterTextField.setMaxStringLength(20);
        filterTextField.setValidator(textFilter);
        filterTextField.action = () -> filter = filterTextField.getText();


        level = new GuiNumberField(100, fontRenderer, 15, height - 33, 40, 18, 5);
        level.minValue = 1;
        level.maxValue = 32767;
        level.setValue(1);

        resetEnchantments();

        enchantBook = new ItemStack(Items.ENCHANTED_BOOK);

        if (!enchants.isEmpty()) {
            EnchantmentData dat = new EnchantmentData(enchants.get(0), 1);
            ItemEnchantedBook.addEnchantment(enchantBook, dat);
        }

    }

    private void updateButtonText() {
        toggleButtonText = I18n.format("gui.enchanting.enchanttoggle." + (showAll ? 0 : 1));
        if (enchantToggleButton != null) enchantToggleButton.displayString = toggleButtonText;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        level.updateCursorCounter();
        if ( Math.abs( mouseDist - (height / 3) ) >= 16 )
            rotOff++;
    }
    
    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        if ( keyCode == 1 )
        {
            this.actionPerformed( this.backButton );
        }
        else {
            level.textboxKeyTyped(typedChar, keyCode);
            filterTextField.textboxKeyTyped(Character.toLowerCase(typedChar), keyCode);
            if (filterTextField.isFocused()) filterEnchantments();
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );

        level.mouseClicked(mouseX, mouseY, mouseButton);
        filterTextField.mouseClicked(mouseX, mouseY, mouseButton);
        
        InfinityEnchantmentList list = new InfinityEnchantmentList( getItemStack() );
        InfinityEnchantmentTag[] activeEnchants = list.getAll();
        int start = midY - 5 * activeEnchants.length;
        if ( activeEnchants.length > 0 && HelperGui.isMouseInRegion( mouseX, mouseY, 0, start, 5 + fontRenderer.getStringWidth( "Unbreaking 32767" ), 10 * activeEnchants.length ) )
        {
            list.removeEnchantment( (mouseY - start) / 10 );
            return;
        }
        
        int r = height / 3;
        
        // mouseDist = (int) Math.sqrt(distX * distX + distY * distY);
        if ( Math.abs( mouseDist - r ) < 16 )
        {
            double angle = (2 * Math.PI) / filteredEnchants.size();
            
            int lowDist = Integer.MAX_VALUE;
            Enchantment enchantment = null;

            for (int i = 0; i < filteredEnchants.size(); i++) {
                double angleI = (((double) (rotOff) / 60d)) + (angle * i);

                int x = (int) (midX + (r * Math.cos(angleI)));
                int y = (int) (midY + (r * Math.sin(angleI)));
                int distX = x - mouseX;
                int distY = y - mouseY;

                int dist = (int) Math.sqrt(distX * distX + distY * distY);
                
                if ( dist < 10 && dist < lowDist )
                {
                    lowDist = dist;
                    enchantment = filteredEnchants.get(i);
                }
            }
            
            if ( enchantment != null )
            {
                new InfinityEnchantmentList( getItemStack() ).set( enchantment, (short) (enchantment.getMaxLevel() == 1 ? 1 : level.getIntValue()) );
            }
        }
        else if ( mouseX > midX - 15 && mouseX < midX + 15 && mouseY > midY - 15 && mouseY < midY + 15 )
        {
            for (Enchantment e : filteredEnchants) {
                new InfinityEnchantmentList(getItemStack()).set(e, (short) (e.getMaxLevel() == 1 ? 1 : level.getIntValue()));
            }
        }
        
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException {
        if (button.id == enchantToggleButton.id) {
            showAll = !showAll;
            resetEnchantments();
            updateButtonText();
            filterEnchantments();
        } else
            super.actionPerformed(button);
    }

    private void resetEnchantments() {
        enchants.clear();
        for (Enchantment e : Enchantment.REGISTRY) {
            if (showAll || e.canApply(getItemStack())) {
                enchants.add(e);
            }
        }
        enchants.sort(new EnchantComparator());
        filteredEnchants = enchants;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        String text = I18n.format("gui.enchanting.search");
        drawCenteredString(fontRenderer, text, filterTextField.x + fontRenderer.getStringWidth(text) / 2, filterTextField.y - 12, InfinityConfig.MAIN_COLOR);
        filterTextField.drawTextBox();

        InfinityEnchantmentTag[] enchantmentTags = new InfinityEnchantmentList(getItemStack()).getAll();
        for (int i = 0; i < enchantmentTags.length; i++) {
            InfinityEnchantmentTag e = enchantmentTags[i];
            if (e.getEnchantment() != null) {
                drawString(fontRenderer, e.getEnchantment().getTranslatedName(e.getLevel()).replace("enchantment.level.", ""), 5, midY + i * 10 - enchantmentTags.length * 5, InfinityConfig.MAIN_COLOR);
            } else {
                drawString( fontRenderer, "Unknown ID (" + e.getID() + ")", 5, midY + i * 10 - enchantmentTags.length * 5, HelperGui.BAD_RED );
            }
        }
        
        level.drawTextBox();
        
        int distX = midX - mouseX;
        int distY = midY - mouseY;
        mouseDist = (int) Math.sqrt( distX * distX + distY * distY );
        
        int r = height / 3;

        double angle = (2 * Math.PI) / filteredEnchants.size();
        
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        
        GlStateManager.scale( 5, 5, 1 );
        GlStateManager.translate( (width / 10), (height / 10), 0 );
        GlStateManager.rotate( rotOff * 3, 0.0f, 0.0f, -1.0f );
        this.itemRender.renderItemAndEffectIntoGUI( getItemStack(), -8, -8 );
        GlStateManager.rotate( rotOff * 3, 0.0f, 0.0f, 1.0f );
        GlStateManager.translate( -(width / 10), -(height / 10), 0 );
        
        GlStateManager.scale( 0.2, 0.2, 1 );

        for (int i = 0; i < filteredEnchants.size(); i++) {
            double angleI = (((rotOff + (Math.abs(mouseDist - r) >= 16 ? partialTicks : 0d)) / 60d)) + (angle * i);
            int x = (int) (midX + (r * Math.cos(angleI)));
            int y = (int) (midY + (r * Math.sin(angleI)));
            GlStateManager.translate(0, 0, 300);
            this.drawCenteredString(this.fontRenderer, filteredEnchants.get(i).getTranslatedName(filteredEnchants.get(i).getMaxLevel() == 1 ? 1 : level.getIntValue()).replace("enchantment.level.", ""), x, y - 17, InfinityConfig.MAIN_COLOR);
            GlStateManager.translate(0, 0, -300);

            this.itemRender.renderItemAndEffectIntoGUI(enchantBook, x - 8, y - 8);
            
            drawRect( x - 1, y - 1, x + 1, y + 1, HelperGui.getColorFromRGB( 255, 255, 255, 255 ) );
        }
        
        if ( mouseX > midX - 15 && mouseX < midX + 15 && mouseY > midY - 15 && mouseY < midY + 15 )
        {
            GlStateManager.translate(0, 0, 300);
            // drawRect( midX - 15, midY - 15, midX + 15, midY + 15, HelperGui.MAIN_BLUE );
            drawCenteredString(fontRenderer, I18n.format("gui.enchanting.addall"), midX, midY, InfinityConfig.CONTRAST_COLOR);
            GlStateManager.translate(0, 0, -300);
        }

        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }

    private void filterEnchantments() {
        if(filter == null || filter.isEmpty()) {
            resetEnchantments();
            return;
        }
        ArrayList<Enchantment> tempList = new ArrayList<>();
        for (Enchantment ench : enchants) {
            String enchName = I18n.format(ench.getName());
            if (!enchName.toLowerCase().contains(filter)) continue;
            tempList.add(ench);
        }
        filteredEnchants = tempList;
    }

    @Override
    protected String getNameUnlocalized() {
        return "enchanting";
    }
}
