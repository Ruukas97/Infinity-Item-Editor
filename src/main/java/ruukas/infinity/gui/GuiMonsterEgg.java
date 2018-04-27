package ruukas.infinity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.gui.action.GuiInfinityButton;
import ruukas.infinity.gui.monsteregg.GuiEntityTags;
import ruukas.infinity.gui.monsteregg.MobTag;
import ruukas.infinity.gui.monsteregg.MonsterPlacerUtils;

@SideOnly( Side.CLIENT )
public class GuiMonsterEgg extends GuiScreen
{
    
    private ItemStack stack = ItemStack.EMPTY;
    private EntityLiving mob = null;
    
    private final GuiScreen lastScreen;
    
    private GuiInfinityButton mobButton, mobSpecificButton, animalSpecificButton;
    
    protected String title = I18n.format( "gui.spawnegg" );
    
    protected ArrayList<String> prettyNBTList = new ArrayList<>();
    
    public GuiMonsterEgg(GuiScreen lastScreen, ItemStack stack) {
        this.lastScreen = lastScreen;
        this.stack = stack;
    }
    
    @Override
    public void initGui()
    {
        this.mobButton = new GuiInfinityButton( 100, (this.width / 2) - 55, 50, 110, 20, getEntityName() );
        this.buttonList.add( this.mobButton );
        this.buttonList.add( new GuiInfinityButton( 101, (this.width / 2) - 75, 50, 20, 20, "<" ) );
        this.buttonList.add( new GuiInfinityButton( 102, (this.width / 2) + 55, 50, 20, 20, ">" ) );
        
        this.buttonList.add( new GuiInfinityButton( 103, this.width / 2 - 75, 80, 150, 20, I18n.format( "gui.spawnegg.entity" ) ) );
        this.buttonList.add( new GuiInfinityButton( 104, this.width / 2 - 75, 110, 150, 20, I18n.format( "gui.spawnegg.mob" ) ) );
        mobSpecificButton = new GuiInfinityButton( 105, this.width / 2 - 75, 140, 150, 20, I18n.format( "gui.spawnegg.mobspecific", getEntityName() ) );
        this.buttonList.add( mobSpecificButton );
        
        animalSpecificButton = new GuiInfinityButton( 106, this.width / 2 - 75, 140, 150, 20, I18n.format( "gui.spawnegg.animalspecific", getEntityName() ) );
        this.buttonList.add( animalSpecificButton );
        animalSpecificButton.enabled = false;
        animalSpecificButton.visible = false;
        
        this.buttonList.add( new GuiInfinityButton( 200, this.width / 2 - 90, this.height - 35, 60, 20, I18n.format( "gui.back" ) ) );
        this.buttonList.add( new GuiInfinityButton( 201, this.width / 2 - 30, this.height - 25, 60, 20, I18n.format( "gui.save" ) ) );
        this.buttonList.add( new GuiInfinityButton( 202, this.width / 2 + 30, this.height - 35, 60, 20, I18n.format( "gui.drop" ) ) );
        
        this.buttonList.add( new GuiInfinityButton( 203, this.width / 2 - 30, this.height - 45, 60, 20, I18n.format( "gui.reset" ) ) );
        
        updateMob();
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
        if ( keyCode == 1 )
        {
            this.mc.displayGuiScreen( lastScreen );
            
            if ( this.mc.currentScreen == null )
            {
                this.mc.setIngameFocus();
            }
        }
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        
        if ( button.id == 103 )
        {
            this.mc.displayGuiScreen( new GuiEntityTags( this, stack, MobTag.ENTITY_SPECIFIC ) );
        }
        
        else if ( button.id == 104 )
        {
            this.mc.displayGuiScreen( new GuiEntityTags( this, stack, MobTag.MOB_SPECIFIC ) );
        }
        
        else if ( button.id == 105 )
        {
            MobTag[] specificTags = MonsterPlacerUtils.getSpecificTagsForEntity( mob );
            
            if ( specificTags.length > 0 )
            {
                this.mc.displayGuiScreen( new GuiEntityTags( this, stack, specificTags ) );
            }
            else
            {
                mobSpecificButton.enabled = false;
                return;
            }
        }
        
        else if ( button.id == 101 )
        {
            MonsterPlacerUtils.setEntityID( stack, MonsterPlacerUtils.getPreviousEntityEgg( ItemMonsterPlacer.getNamedIdFrom( stack ) ) );
            updateMob();
        }
        
        else if ( button.id == 102 )
        {
            MonsterPlacerUtils.setEntityID( stack, MonsterPlacerUtils.getNextEntityEgg( ItemMonsterPlacer.getNamedIdFrom( stack ) ) );
            updateMob();
        }
        
        else if ( button.id == 200 )
        {
            mc.displayGuiScreen( this.lastScreen );
        }
        
        else if ( button.id == 201 )
        {
            int slot = mc.player.inventory.currentItem;
            mc.playerController.sendSlotPacket( stack, 36 + slot );
            mc.displayGuiScreen( this.lastScreen );
        }
        
        else if ( button.id == 202 )
        {
            HelperGui.dropStack( stack );
        }
        
        else if ( button.id == 203 )
        {
            if ( stack.hasTagCompound() && stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
            {
                String id = null;
                if ( stack.getSubCompound( "EntityTag" ).hasKey( "id" ) )
                {
                    id = stack.getSubCompound( "EntityTag" ).getString( "id" );
                }
                stack.getTagCompound().removeTag( "EntityTag" );
                
                if ( id != null )
                {
                    NBTTagCompound entityTag = new NBTTagCompound();
                    entityTag.setString( "id", id );
                    
                    stack.getTagCompound().setTag( "EntityTag", entityTag );
                }
            }
            updateMob();
        }
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        this.drawDefaultBackground();
        
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        this.itemRender.renderItemAndEffectIntoGUI( stack, (this.width / 2) - 8, 30 );
        GlStateManager.popMatrix();
        
        this.drawCenteredString( this.fontRenderer, this.title, this.width / 2, 15, HelperGui.MAIN_PURPLE );
        
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        GlStateManager.pushMatrix();
        GL11.glScalef( 0.8f, 0.8f, 0.8f );
        this.renderToolTip( stack, 0, 25 );
        
        drawHoveringText( prettyNBTList, 0, this.height / 2 );
        GlStateManager.popMatrix();
        
        if ( mob != null )
        {
            drawEntityOnScreen( (int) (this.width / 3 * 2.5), this.height - 20, 70 );
        }
        
    }
    
    public String getEntityName()
    {
        if ( stack.getItem() == Items.SPAWN_EGG )
        {
            return I18n.format( "entity." + EntityList.getTranslationName( ItemMonsterPlacer.getNamedIdFrom( stack ) ) + ".name" );
        }
        return "Empty";
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
    
    public void updateMob()
    {
        if ( stack.getItem() instanceof ItemMonsterPlacer )
        {
            ResourceLocation id = ItemMonsterPlacer.getNamedIdFrom( stack );
            Entity entity = EntityList.createEntityByIDFromName( id, mc.world );
            if ( entity != null && entity instanceof EntityLiving )
            {
                mob = (EntityLiving) entity;
                applyItemDataToMob();
            }
        }
        
        if ( mob instanceof EntityAnimal )
        {
            mobSpecificButton.y = 170;
            animalSpecificButton.enabled = false;
            animalSpecificButton.visible = true;
        }
        else
        {
            mobSpecificButton.y = 140;
            animalSpecificButton.enabled = false;
            animalSpecificButton.visible = false;
        }
        
        mobButton.displayString = getEntityName();
        mobSpecificButton.displayString = I18n.format( "gui.spawnegg.mobspecific", getEntityName() );
        
        mobSpecificButton.enabled = MonsterPlacerUtils.getSpecificTagsForEntity( mob ).length > 0;
        
        String s;
        if ( stack.hasTagCompound() && stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
        {
            s = stack.getSubCompound( "EntityTag" ).toString();
        }
        else
        {
            s = "{}";
        }
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse( s );
        s = gson.toJson( je );
        
        prettyNBTList.clear();
        for ( String str : s.split( "\\n" ) )
        {
            prettyNBTList.add( str );
        }
    }
    
    public void applyItemDataToMob()
    {
        NBTTagCompound tag = stack.getTagCompound();
        
        if ( tag != null && tag.hasKey( "EntityTag", 10 ) )
        {
            UUID uuid = mob.getUniqueID();
            mob.setUniqueId( uuid );
            mob.readFromNBT( tag.getCompoundTag( "EntityTag" ) );
        }
    }
    
    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public void drawEntityOnScreen( int posX, int posY, int scale )
    {
        EntityLiving ent = mob;
        ent.ticksExisted = (int) mc.world.getWorldTime();
        
        if ( ent instanceof EntityElderGuardian )
        {
            scale /= 2;
        }
        else if ( ent instanceof EntitySquid )
        {
            posY -= 100;
        }
        else if ( ent instanceof EntityGhast )
        {
            posY -= 65;
            scale /= 3;
        }
        else if ( ent instanceof EntitySpider && !(ent instanceof EntityCaveSpider) )
        {
            posX += 30;
        }
        else if ( ent instanceof EntityBat )
        {
            scale = 100;
            posY += 30;
        }
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
        
        rendermanager.renderEntity( mob, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false );
        
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
