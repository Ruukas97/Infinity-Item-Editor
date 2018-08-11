package ruukas.infinity.gui;

import java.io.IOException;
import java.util.UUID;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.InfinityConfig;
import ruukas.infinity.gui.action.GuiInfinityButton;
import ruukas.infinity.nbt.NBTHelper;
import ruukas.infinity.nbt.NBTHelper.EnumPosePart;

@SideOnly( Side.CLIENT )
public class GuiPose extends GuiScreen
{
    
    private ItemStack stack = ItemStack.EMPTY;
    private EntityArmorStand armorStand = null;
    
    private final GuiScreen lastScreen;
    
    private GuiInfinityButton backButton, resetButton, dropButton;
    
    protected String title = I18n.format( "gui.armorstand.pose" );
    
    public GuiPose(GuiScreen lastScreen, ItemStack stack) {
        this.lastScreen = lastScreen;
        this.stack = stack;
    }
    
    @Override
    public void initGui()
    {
        backButton = addButton( new GuiInfinityButton( 200, this.width / 2 - 90, this.height - 25, 60, 20, I18n.format( "gui.back" ) ) );
        resetButton = addButton( new GuiInfinityButton( 201, this.width / 2 - 30, this.height - 25, 60, 20, I18n.format( "gui.reset" ) ) );
        dropButton = addButton( new GuiInfinityButton( 202, this.width / 2 + 30, this.height - 25, 60, 20, I18n.format( "gui.drop" ) ) );
        
        int sliders = 0;
        int vertical = 1;
        for ( EnumPosePart part : EnumPosePart.values() )
        {
            addButton( new GuiSlider( 300 + sliders++, 10, 5 + 25 * (vertical), 120, 20, part.getKey() + "-x ", "", 0d, 359d, NBTHelper.ArmorStandNBTHelper.getX( stack, part ), false, true, new ISlider() {
                @Override
                public void onChangeSliderValue( GuiSlider slider )
                {
                    NBTHelper.ArmorStandNBTHelper.setX( stack, part, (float) Math.floor( slider.getValue() ) );
                    updateArmorStand();
                }
            } ) );
            
            addButton( new GuiSlider( 300 + sliders++, 135, 5 + 25 * (vertical), 120, 20, part.getKey() + "-y ", "", 0d, 359d, NBTHelper.ArmorStandNBTHelper.getY( stack, part ), false, true, new ISlider() {
                @Override
                public void onChangeSliderValue( GuiSlider slider )
                {
                    NBTHelper.ArmorStandNBTHelper.setY( stack, part, (float) Math.floor( slider.getValue() ) );
                    updateArmorStand();
                }
            } ) );
            
            addButton( new GuiSlider( 300 + sliders++, 260, 5 + 25 * (vertical), 120, 20, part.getKey() + "-z ", "", 0d, 359d, NBTHelper.ArmorStandNBTHelper.getZ( stack, part ), false, true, new ISlider() {
                @Override
                public void onChangeSliderValue( GuiSlider slider )
                {
                    NBTHelper.ArmorStandNBTHelper.setZ( stack, part, (float) Math.floor( slider.getValue() ) );
                    updateArmorStand();
                }
            } ) );
            
            vertical++;
        }
        
        updateArmorStand();
    }
    
    @Override
    public void onGuiClosed()
    {
    
    }
    
    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        if ( keyCode == Keyboard.KEY_ESCAPE )
        {
            actionPerformed( backButton );
        }
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        
        if ( button.id == backButton.id )
        {
            mc.displayGuiScreen( this.lastScreen );
        }
        
        else if ( button.id == resetButton.id )
        {
            NBTHelper.getEntityTag( stack ).removeTag( "Pose" );
            NBTHelper.removeEntityTagIfEmpty( stack );
            updateArmorStand();
        }
        
        else if ( button.id == dropButton.id )
        {
            HelperGui.dropStack( stack );
        }
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        this.drawDefaultBackground();
        
        if ( armorStand != null )
        {
            drawEntityOnScreen( (int) (this.width / 3 * 2.5), this.height - 50, 120 );
        }
        
        this.drawCenteredString( this.fontRenderer, this.title, this.width / 2, 15, InfinityConfig.MAIN_COLOR );
        
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        HelperGui.addTooltipTranslated( resetButton, mouseX, mouseY, "gui.armorstand.pose.reset" );
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
    
    public void updateArmorStand()
    {
        if ( stack.getItem() instanceof ItemArmorStand )
        {
            EntityArmorStand entity = new EntityArmorStand( mc.world );
            
            if ( entity != null && entity instanceof EntityArmorStand )
            {
                armorStand = (EntityArmorStand) entity;
                applyItemDataToMob();
            }
        }
    }
    
    public void applyItemDataToMob()
    {
        NBTTagCompound tag = stack.getTagCompound();
        
        if ( tag != null && tag.hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
        {
            UUID uuid = armorStand.getUniqueID();
            armorStand.setUniqueId( uuid );
            armorStand.readFromNBT( tag.getCompoundTag( "EntityTag" ) );
        }
    }
    
    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public void drawEntityOnScreen( int posX, int posY, int scale )
    {
        EntityArmorStand ent = armorStand;
        ent.ticksExisted = (int) mc.world.getWorldTime();
        
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate( (float) posX, (float) posY, 50.0F );
        GlStateManager.scale( (float) (-scale), (float) scale, (float) scale );
        GlStateManager.rotate( 180.0F, 0.0F, 0.0F, 1.0F );
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate( 40.0F, 0.0F, 1.0F, 0.0F );
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate( -50.0F, 0.0F, 1.0F, 0.0F );
        GlStateManager.rotate( 10F, 1.0F, 0.0F, 0.0F );
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate( 0.0F, 0.0F, 0.0F );
        RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY( 180.0F );
        rendermanager.setRenderShadow( false );
        
        rendermanager.renderEntity( armorStand, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false );
        
        rendermanager.setRenderShadow( true );
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture( OpenGlHelper.lightmapTexUnit );
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture( OpenGlHelper.defaultTexUnit );
    }
}
