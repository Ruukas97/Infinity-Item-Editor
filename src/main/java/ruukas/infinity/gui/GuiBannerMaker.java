package ruukas.infinity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.CreativeCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.qualityorder.util.itemstack.QualityBanner;
import ruukas.qualityorder.util.nbt.tileentity.TileEntityTagBanner;
import ruukas.qualityorder.util.nbt.tileentity.TileEntityTagBanner.Pattern;

public class GuiBannerMaker extends InventoryEffectRenderer
{
    
    private static final ResourceLocation background = new ResourceLocation( "qualityorder", "textures/gui/bannermaker.png" );
    private static final InventoryBasic bannerInventory = new InventoryBasic( "tmp", true, 8 );
    private static final InventoryBasic dyeInventory = new InventoryBasic( "tmp2", true, 17 );
    private static final InventoryBasic bannerResult = new InventoryBasic( "tmp3", true, 2 );
    
    protected final GuiScreen lastScreen;
    private Slot destroyItemSlot;
    /** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
    private float currentScroll;
    /** True if the scrollbar is being dragged */
    private boolean isScrolling;
    /**
     * True if the left mouse button was held down last time drawScreen was called.
     */
    private boolean wasClicking;
    
    private CreativeCrafting listener;
    
    private GuiTextField nbtTextField;
    
    public GuiBannerMaker(GuiScreen lastScreen, EntityPlayer player) {
        super( new ContainerBanner( player ) );
        this.lastScreen = lastScreen;
        player.openContainer = this.inventorySlots;
        this.allowUserInput = true;
        this.ySize = 136;
        this.xSize = 195;
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.nbtTextField.updateCursorCounter();
    }
    
    /**
     * Sets the banner's NBT data to be what the user wrote in the text field.
     */
    private void updateBannerNBT()
    {
        if ( nbtTextField.getText().length() == 0 )
        {
            setCurrentBanner( ItemStack.EMPTY, false );
        }
        else
        {
            try
            {
                nbtTextField.setTextColor( 0x61ff00 );
                TileEntityTagBanner teTag = new TileEntityTagBanner( JsonToNBT.getTagFromJson( this.nbtTextField.getText() ) );
                setCurrentBanner( new QualityBanner( teTag, getCurrentBanner().getItem() == Items.SHIELD ).getItemStack(), false );
            }
            catch ( NBTException nbtexception )
            {
                nbtTextField.setTextColor( 0xff0000 );
                try
                {
                    throw nbtexception;
                }
                catch ( NBTException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Updates the list of banners the user can make next.
     * 
     * @param stackList
     */
    private void updateBanners( List<ItemStack> stackList )
    {
        ItemStack currentBanner = getCurrentBanner();
        ItemStack currentDyeStack = getCurrentDye();
        
        if ( currentBanner == null || currentBanner == ItemStack.EMPTY )
        {
            for ( EnumDyeColor dye : EnumDyeColor.values() )
            {
                new QualityBanner( new TileEntityTagBanner( dye ) ).addToList( stackList );
            }
        }
        else if ( currentDyeStack == null || currentDyeStack == ItemStack.EMPTY )
        {
            /*
             * stackList.add(AlphabetSkulls.getAlphabetSkull('p')); stackList.add(AlphabetSkulls.getAlphabetSkull('i')); stackList.add(AlphabetSkulls.getAlphabetSkull('c')); stackList.add(AlphabetSkulls.getAlphabetSkull('k'));
             * 
             * stackList.add(ItemStack.EMPTY);
             * 
             * stackList.add(AlphabetSkulls.getAlphabetSkull('c')); stackList.add(AlphabetSkulls.getAlphabetSkull('o')); stackList.add(AlphabetSkulls.getAlphabetSkull('l')); stackList.add(AlphabetSkulls.getAlphabetSkull('o')); stackList.add(AlphabetSkulls.getAlphabetSkull('r'));
             */
        }
        else
        {
            EnumDyeColor currentDye = EnumDyeColor.byDyeDamage( currentDyeStack.getItemDamage() );
            boolean isShield = currentBanner.getItem() == Items.SHIELD;
            boolean hasCompound = currentBanner.hasTagCompound() && currentBanner.getTagCompound().hasKey( "BlockEntityTag", Constants.NBT.TAG_COMPOUND ) && currentBanner.getTagCompound().getCompoundTag( "BlockEntityTag" ).hasKey( "Base", Constants.NBT.TAG_INT );
            
            for ( BannerPattern pat : BannerPattern.values() )
            {
                if ( pat == BannerPattern.BASE )
                    continue;
                QualityBanner newBanner = new QualityBanner( hasCompound ? currentBanner.getTagCompound().copy() : new TileEntityTagBanner( EnumDyeColor.WHITE ), !hasCompound, isShield );
                newBanner.getTileEntityTagBanner().addPattern( new Pattern( currentDye, pat ) );
                newBanner.addToList( stackList );
            }
        }
    }
    
    /**
     * Called every time the list should change (mostly if the currentbanner changes).
     * 
     * @param shouldReloadTextField
     */
    private void updateList( boolean shouldReloadTextField )
    {
        ContainerBanner bannerContainer = (ContainerBanner) this.inventorySlots;
        if ( destroyItemSlot == null )
        {
            this.destroyItemSlot = new Slot( dyeInventory, 0, 173, 112 );
        }
        if ( !bannerContainer.inventorySlots.contains( destroyItemSlot ) )
        {
            bannerContainer.inventorySlots.add( destroyItemSlot );
        }
        this.dragSplittingSlots.clear();
        bannerContainer.itemList.clear();
        updateBanners( bannerContainer.itemList );
        
        if ( shouldReloadTextField )
        {
            nbtTextField.setTextColor( 0xffffff );
            if ( getCurrentBanner() != null && getCurrentBanner() != ItemStack.EMPTY && getCurrentBanner().hasTagCompound() && getCurrentBanner().getTagCompound().hasKey( "BlockEntityTag", Constants.NBT.TAG_COMPOUND ) )
            {
                this.nbtTextField.setText( getCurrentBanner().getOrCreateSubCompound( "BlockEntityTag" ).toString() );
            }
            else
            {
                this.nbtTextField.setText( "" );
            }
        }
        
        bannerContainer.scrollTo( currentScroll );
    }
    
    /**
     * Changes the current banner into a shield - or back to a banner, if it is already a shield.
     */
    private void swapBanner()
    {
        if ( getCurrentBanner() != null && getCurrentBanner() != ItemStack.EMPTY )
        {
            QualityBanner swappedBanner;
            if ( !getCurrentBanner().hasTagCompound() )
            {
                swappedBanner = new QualityBanner( new TileEntityTagBanner( EnumDyeColor.WHITE ), getCurrentBanner().getItem() == Items.BANNER );
            }
            else
            {
                swappedBanner = new QualityBanner( getCurrentBanner().getTagCompound(), false, getCurrentBanner().getItem() == Items.BANNER );
            }
            setCurrentBanner( swappedBanner.getItemStack(), true );
        }
    }
    
    private void removeLayerBanner()
    {
        if ( getCurrentBanner() != null && getCurrentBanner() != ItemStack.EMPTY && getCurrentBanner().hasTagCompound() )
        {
            QualityBanner newBanner = new QualityBanner( getCurrentBanner().getTagCompound().copy(), false );
            if ( newBanner.getTileEntityTagBanner() != null && newBanner.getTileEntityTagBanner().getPatterns() != null && !newBanner.getTileEntityTagBanner().getPatterns().hasNoTags() )
            {
                newBanner.getTileEntityTagBanner().getPatterns().removeTag( newBanner.getTileEntityTagBanner().getPatterns().tagCount() - 1 );
                setCurrentBanner( newBanner.getItemStack(), true );
            }
        }
    }
    
    private void setCurrentyDye( ItemStack dye )
    {
        bannerResult.setInventorySlotContents( 0, dye );
        updateList( false );
    }
    
    private ItemStack getCurrentDye()
    {
        return bannerResult.getStackInSlot( 0 );
    }
    
    /**
     * Sets the current banner to an ItemStack. And sends an "update list" request.
     * 
     * @param banner
     * @param shouldUpdateTextField
     */
    private void setCurrentBanner( ItemStack banner, boolean shouldUpdateTextField )
    {
        bannerResult.setInventorySlotContents( 1, banner );
        updateList( shouldUpdateTextField );
    }
    
    /**
     * Returns the ItemStack currently stored in the banner result slot.
     * 
     * @return
     */
    public ItemStack getCurrentBanner()
    {
        return bannerResult.getStackInSlot( 1 );
    }
    
    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        // super.keyTyped(typedChar, keyCode);
        this.nbtTextField.textboxKeyTyped( typedChar, keyCode );
        updateBannerNBT();
        
        if ( keyCode == 1 )
        {
            mc.displayGuiScreen( lastScreen );
            
            if ( mc.currentScreen == null )
            {
                mc.setIngameFocus();
            }
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        this.nbtTextField.mouseClicked( mouseX, mouseY, mouseButton );
        // Is in toolbar height
        if ( mouseY < guiTop + 14 && mouseY > guiTop )
        {
            // Is further than first icon start
            if ( mouseX > guiLeft + 168 )
            {
                if ( mouseX < guiLeft + 178 )
                {
                    removeLayerBanner();
                }
                else if ( mouseX > guiLeft + 180 && mouseX < guiLeft + 190 )
                {
                    Minecraft.getMinecraft().displayGuiScreen( null );
                }
            }
        }
        else if ( mouseY < guiTop + 40 && mouseY > guiTop + 27 && mouseX > guiLeft + 157 && mouseX < guiLeft + 187 )
        {
            swapBanner();
        }
    }
    
    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick( Slot slotIn, int slotId, int mouseButton, ClickType type )
    {
        boolean isShift = type == ClickType.QUICK_MOVE;
        type = slotId == -999 && type == ClickType.PICKUP ? ClickType.THROW : type;
        
        // Drop item mouse is holding if no slot
        if ( slotIn == null
        /*
         * && selectedTabIndex != CreativeTabs.INVENTORY.getTabIndex()
         */ && type != ClickType.QUICK_CRAFT )
        {
            InventoryPlayer inventoryplayer1 = this.mc.player.inventory;
            
            if ( inventoryplayer1.getItemStack() != null && inventoryplayer1.getItemStack() != ItemStack.EMPTY )
            {
                if ( mouseButton == 0 )
                {
                    this.mc.player.dropItem( inventoryplayer1.getItemStack(), true );
                    this.mc.playerController.sendPacketDropItem( inventoryplayer1.getItemStack() );
                    inventoryplayer1.setItemStack( ItemStack.EMPTY );
                }
                
                if ( mouseButton == 1 )
                {
                    ItemStack itemstack5 = inventoryplayer1.getItemStack().splitStack( 1 );
                    this.mc.player.dropItem( itemstack5, true );
                    this.mc.playerController.sendPacketDropItem( itemstack5 );
                    
                    if ( inventoryplayer1.getItemStack().getCount() == 0 )
                    {
                        inventoryplayer1.setItemStack( ItemStack.EMPTY );
                    }
                }
            }
        }
        // Shift Clear All Items
        else if ( slotIn != null && slotIn == destroyItemSlot && isShift )
        {
            for ( int j = 0 ; j < 9 ; ++j )
            {
                this.mc.playerController.sendSlotPacket( ItemStack.EMPTY, j );
                this.mc.player.inventory.setInventorySlotContents( j, ItemStack.EMPTY );
            }
        }
        // Destroy Item put into destroyItemSlot
        else if ( slotIn != null && slotIn == destroyItemSlot )
        {
            this.mc.player.inventory.setItemStack( ItemStack.EMPTY );
        }
        
        // In banner result inventory
        else if ( type != ClickType.QUICK_CRAFT && (slotIn.inventory == dyeInventory || slotIn.inventory == bannerInventory || slotIn.inventory == bannerResult) )
        {
            InventoryPlayer inventoryplayer = this.mc.player.inventory;
            ItemStack handStack = inventoryplayer.getItemStack();
            ItemStack slotStack = slotIn.getStack();
            
            if ( slotIn.inventory == dyeInventory )
            {
                if ( handStack != null && handStack != ItemStack.EMPTY )
                {
                    this.mc.player.inventory.setItemStack( ItemStack.EMPTY );
                }
                else if ( slotStack != null && slotStack != ItemStack.EMPTY )
                {
                    setCurrentyDye( slotStack );
                }
                return;
            }
            else if ( slotIn.inventory == bannerInventory )
            {
                if ( handStack != null && handStack != ItemStack.EMPTY )
                {
                    this.mc.player.inventory.setItemStack( ItemStack.EMPTY );
                }
                else if ( slotStack != null && slotStack != ItemStack.EMPTY )
                {
                    setCurrentBanner( slotStack, true );
                }
                return;
            }
            else if ( slotIn.inventory == bannerResult )
            {
                if ( slotIn.getSlotIndex() == 1 )
                {
                    // Pressing a number 1-9
                    if ( type == ClickType.SWAP )
                    {
                        if ( slotStack != null && slotStack != ItemStack.EMPTY && mouseButton >= 0 && mouseButton < 9 )
                        {
                            // itemstack7.stackSize =
                            // itemstack7.getMaxStackSize();
                            this.mc.playerController.sendSlotPacket( slotStack, mouseButton );
                            this.mc.player.inventory.setInventorySlotContents( mouseButton, slotStack );
                            setCurrentBanner( ItemStack.EMPTY, true );
                            this.mc.player.inventoryContainer.detectAndSendChanges();
                        }
                        
                        return;
                    }
                    
                    // Mouse wheel
                    if ( type == ClickType.CLONE )
                    {
                        if ( (inventoryplayer.getItemStack() == null || inventoryplayer.getItemStack() == ItemStack.EMPTY) && slotIn.getHasStack() )
                        {
                            ItemStack itemstack6 = slotIn.getStack().copy();
                            itemstack6.setCount( itemstack6.getMaxStackSize() );
                            inventoryplayer.setItemStack( itemstack6 );
                        }
                        
                        return;
                    }
                    
                    // Pressing Q - Mouse button is 1 if Ctrl is down
                    if ( type == ClickType.THROW )
                    {
                        if ( slotStack != null && slotStack != ItemStack.EMPTY )
                        {
                            this.mc.player.dropItem( slotStack, true );
                            this.mc.playerController.sendPacketDropItem( slotStack );
                        }
                        return;
                    }
                    
                    if ( slotStack != null && slotStack != ItemStack.EMPTY && (handStack == null || handStack == ItemStack.EMPTY) )
                    {
                        inventoryplayer.setItemStack( slotStack );
                        handStack = inventoryplayer.getItemStack();
                        
                        if ( isShift )
                        {
                            handStack.setCount( handStack.getMaxStackSize() );
                        }
                        
                        setCurrentBanner( ItemStack.EMPTY, true );
                        return;
                    }
                    else if ( (handStack != null && handStack != ItemStack.EMPTY) && (handStack.getItem() == Items.BANNER || handStack.getItem() == Items.SHIELD) )
                    {
                        ItemStack old2 = (slotStack != null && slotStack != ItemStack.EMPTY) ? slotStack.copy() : ItemStack.EMPTY;
                        setCurrentBanner( handStack, true );
                        inventoryplayer.setItemStack( old2 );
                        return;
                    }
                }
                return;
            }
        }
        else
        {
            if ( type == ClickType.THROW && slotIn != null && slotIn.getHasStack() )
            {
                ItemStack itemstack = slotIn.decrStackSize( mouseButton == 0 ? 1 : slotIn.getStack().getMaxStackSize() );
                this.mc.player.dropItem( itemstack, true );
                this.mc.playerController.sendPacketDropItem( itemstack );
            }
            else if ( type == ClickType.THROW && this.mc.player.inventory.getItemStack() != null && this.mc.player.inventory.getItemStack() != ItemStack.EMPTY )
            {
                this.mc.player.dropItem( this.mc.player.inventory.getItemStack(), true );
                this.mc.playerController.sendPacketDropItem( this.mc.player.inventory.getItemStack() );
                this.mc.player.inventory.setItemStack( ItemStack.EMPTY );
            }
            else
            {
                if ( isShift )
                {
                    type = ClickType.PICKUP;
                }
                this.mc.player.inventoryContainer.slotClick( (slotIn == null ? slotId : slotIn.slotNumber) + 10, mouseButton, type, this.mc.player );
                this.mc.player.inventoryContainer.detectAndSendChanges();
            }
        }
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        if ( this.mc.playerController.isInCreativeMode() )
        {
            super.initGui();
            this.buttonList.clear();
            
            this.nbtTextField = new GuiTextField( 0, this.fontRenderer, this.guiLeft + 10, guiTop + 61, 136, 20 );
            this.nbtTextField.setMaxStringLength( 500 );
            this.nbtTextField.setFocused( false );
            this.nbtTextField.setTextColor( 16777215 );
            this.nbtTextField.setEnableBackgroundDrawing( false );
            
            Keyboard.enableRepeatEvents( true );
            
            // this.searchField = new GuiTextField(0, this.fontRendererObj,
            // this.guiLeft + 82, this.guiTop + 6, 89,
            // this.fontRendererObj.FONT_HEIGHT);
            
            updateList( true );
            
            this.listener = new CreativeCrafting( this.mc );
            this.mc.player.inventoryContainer.addListener( this.listener );
            
            NonNullList<ItemStack> dyes = NonNullList.<ItemStack>create();
            Items.DYE.getSubItems( Items.DYE.getCreativeTab(), dyes );
            int i = 1;
            for ( ItemStack dye : dyes )
            {
                dyeInventory.setInventorySlotContents( i++, dye );
            }
            
        }
        else
        {
            this.mc.displayGuiScreen( new GuiInventory( this.mc.player ) );
        }
    }
    
    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        super.onGuiClosed();
        
        if ( this.mc.player != null && this.mc.player.inventory != null )
        {
            this.mc.player.inventoryContainer.removeListener( this.listener );
        }
        
        Keyboard.enableRepeatEvents( false );
    }
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer( int mouseX, int mouseY )
    {
        GlStateManager.disableBlend();
        this.fontRenderer.drawString( I18n.format( "gui.bannermaker", new Object[ 0 ] ), 8, 6, 4210752 );
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer( float partialTicks, int mouseX, int mouseY )
    {
        GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );
        RenderHelper.enableGUIStandardItemLighting();
        
        this.mc.getTextureManager().bindTexture( background );
        this.drawTexturedModalRect( this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize );
        
        GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );
        int i = this.guiLeft + 9;
        int j = this.guiTop + 28;
        int k = i + 142;
        
        this.drawTexturedModalRect( i+((float) (k - i - 15) * this.currentScroll), j, 241, 0, 15, 10 );
        
        if ( getCurrentBanner() == null || getCurrentBanner() == ItemStack.EMPTY )
        {
            return;
            
        }
        else
        {
            if ( getCurrentBanner().getItem() == Items.BANNER )
            {
                TileEntityBanner bannerTe = new TileEntityBanner();
                bannerTe.setItemValues( getCurrentBanner(), false );
                HelperGui.renderBanner( this.guiLeft + 164, this.guiTop + 103, 20, (float) (this.guiLeft + 175 - mouseX), (float) (this.guiTop + 103 - 30 - mouseY), bannerTe );
            }
            else
            {
                EntityArmorStand armorStand = new EntityArmorStand( Minecraft.getMinecraft().world );
                for ( EntityEquipmentSlot slot : EntityEquipmentSlot.values() )
                {
                    armorStand.setItemStackToSlot( slot, Minecraft.getMinecraft().player.getItemStackFromSlot( slot ) );
                }
                armorStand.setHeldItem( EnumHand.OFF_HAND, getCurrentBanner() );
                armorStand.setInvisible( false );
                HelperGui.drawEntityOnScreen( this.guiLeft + 169, this.guiTop + 103, 18, (float) (this.guiLeft + 145 - mouseX), (float) (this.guiTop + 103 - 30 - mouseY), armorStand );
            }
            
        }
        this.nbtTextField.drawTextBox();
        
    }
    
    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        
        if ( i != 0 )
        {
            int j = (((ContainerBanner) this.inventorySlots).itemList.size() / 8);
            
            if ( i > 0 )
            {
                i = 1;
            }
            
            if ( i < 0 )
            {
                i = -1;
            }
            
            this.currentScroll = (float) ((double) this.currentScroll - (double) i / (double) j);
            this.currentScroll = MathHelper.clamp( this.currentScroll, 0.0F, 1.0F );
            ((ContainerBanner) this.inventorySlots).scrollTo( this.currentScroll );
        }
    }
    
    @Override
    protected boolean isPointInRegion( int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY )
    {
        int i = this.guiLeft;
        int j = this.guiTop;
        pointX = pointX - i;
        pointY = pointY - j;
        return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
    }
    
    private void drawMouseOverButton( int mouseX, int mouseY, int buttonX, int buttonY, int buttonSizeX, int buttonSizeY, String tooltip )
    {
        if ( isPointInRegion( buttonX, buttonY, buttonSizeX, buttonSizeY, mouseY, mouseY ) )
        {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            
            GlStateManager.colorMask( true, true, true, false );
            this.drawGradientRect( buttonX, buttonY, buttonX + buttonSizeX, buttonY + buttonSizeY, -2130706433, -2130706433 );
            GlStateManager.colorMask( true, true, true, true );
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            
            ArrayList<String> list = new ArrayList<String>();
            list.add( tooltip );
            drawHoveringText( list, mouseX, mouseY );
        }
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        boolean flag = Mouse.isButtonDown( 0 );
        int lowScrollXBounding = guiLeft + 9;
        int lowScrollYBounding = guiTop + 28;
        int highScrollXBounding = lowScrollXBounding + 142;
        int highScrollYBounding = lowScrollYBounding + 10;
        
        if ( !this.wasClicking && flag && mouseX >= lowScrollXBounding && mouseY >= lowScrollYBounding && mouseX < highScrollXBounding && mouseY < highScrollYBounding )
        {
            this.isScrolling = true;
        }
        
        if ( !flag )
        {
            this.isScrolling = false;
        }
        
        this.wasClicking = flag;
        
        if ( this.isScrolling )
        {
            this.currentScroll = ((float) (mouseX - lowScrollXBounding) - 7.5F) / ((float) (highScrollXBounding - lowScrollXBounding) - 15.0F);
            this.currentScroll = MathHelper.clamp( this.currentScroll, 0.0F, 1.0F );
            ((ContainerBanner) this.inventorySlots).scrollTo( this.currentScroll );
        }
        
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        drawMouseOverButton( mouseX, mouseY, 168, 4, 10, 10, "Remove a layer" );
        drawMouseOverButton( mouseX, mouseY, 180, 4, 10, 10, "Close" );
        drawMouseOverButton( mouseX, mouseY, 157, 27, 30, 13, "Swap to Shield/Banner" );
        
        if ( this.destroyItemSlot != null && this.isPointInRegion( this.destroyItemSlot.xPos, this.destroyItemSlot.yPos, 16, 16, mouseX, mouseY ) )
        {
            this.drawHoveringText( I18n.format( "inventory.binSlot", new Object[ 0 ] ), mouseX, mouseY );
        }
        
        GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );
        GlStateManager.disableLighting();
    }
    
    @SideOnly( Side.CLIENT )
    static class ContainerBanner extends Container
    {
        
        public List<ItemStack> itemList = Lists.<ItemStack>newArrayList();
        
        public ContainerBanner(EntityPlayer player) {
            InventoryPlayer inventoryplayer = player.inventory;
            
            // Destroy slot
            // this.addSlotToContainer(QualityGuiBannerMaker.destroyItemSlot);
            
            // Crafted Banner and Dye slot
            this.addSlotToContainer( new Slot( GuiBannerMaker.bannerResult, 0, 154, 42 ) );
            this.addSlotToContainer( new Slot( GuiBannerMaker.bannerResult, 1, 172, 42 ) );
            
            // Banner slots
            for ( int i = 0 ; i < 8 ; ++i )
            {
                this.addSlotToContainer( new Slot( GuiBannerMaker.bannerInventory, i, 9 + i * 18, 42 ) );
            }
            
            // Dye slots
            for ( int i = 0 ; i < 2 ; ++i )
            {
                for ( int j = 0 ; j < 8 ; ++j )
                {
                    this.addSlotToContainer( new Slot( GuiBannerMaker.dyeInventory, i * 8 + j + 1, 9 + j * 18, 73 + i * 18 ) );
                }
            }
            
            // Player's actionbar
            for ( int i = 0 ; i < 9 ; ++i )
            {
                this.addSlotToContainer( new Slot( inventoryplayer, i, 9 + i * 18, 112 ) );
            }
            
            this.scrollTo( 0.0F );
        }
        
        /**
         * Updates the gui slots ItemStack's based on scroll position.
         */
        public void scrollTo( float scollTo )
        {
            int i = (this.itemList.size() - 8);
            int j = (int) ((double) (scollTo * (float) i));
            
            if ( j < 0 )
            {
                j = 0;
            }
            for ( int l = 0 ; l < 8 ; ++l )
            {
                int i1 = l + j;
                
                if ( i1 >= 0 && i1 < this.itemList.size() )
                {
                    GuiBannerMaker.bannerInventory.setInventorySlotContents( l, (ItemStack) this.itemList.get( i1 ) );
                }
                else
                {
                    GuiBannerMaker.bannerInventory.setInventorySlotContents( l, ItemStack.EMPTY );
                }
            }
        }
        
        public boolean canScroll()
        {
            return this.itemList.size() > 8;
        }
        
        public boolean canInteractWith( EntityPlayer playerIn )
        {
            return true;
        }
        
        /**
         * Retries slotClick() in case of failure
         */
        protected void retrySlotClick( int slotId, int clickedButton, boolean mode, EntityPlayer playerIn )
        {
        }
        
        /**
         * Take a stack from the specified inventory slot.
         */
        public ItemStack transferStackInSlot( EntityPlayer playerIn, int index )
        {
            if ( index >= this.inventorySlots.size() - 9 && index < this.inventorySlots.size() )
            {
                Slot slot = (Slot) this.inventorySlots.get( index );
                
                if ( slot != null && slot.getHasStack() )
                {
                    slot.putStack( ItemStack.EMPTY );
                }
            }
            
            return ItemStack.EMPTY;
        }
        
        /**
         * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is null for the initial slot that was double-clicked.
         */
        public boolean canMergeSlot( ItemStack stack, Slot slotIn )
        {
            return slotIn.yPos > 90;
        }
        
        /**
         * Returns true if the player can "drag-spilt" items into this slot,. returns true by default. Called to check if the slot can be added to a list of Slots to split the held ItemStack across.
         */
        public boolean canDragIntoSlot( Slot slotIn )
        {
            return slotIn.inventory instanceof InventoryPlayer || slotIn.yPos > 90 && slotIn.xPos <= 162;
        }
    }
}
