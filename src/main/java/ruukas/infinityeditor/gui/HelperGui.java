package ruukas.infinityeditor.gui;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import ruukas.infinityeditor.Infinity;

public class HelperGui extends GuiUtils
{
    public static final ResourceLocation EMPTY_ARMOR_SLOT_SWORD = new ResourceLocation( Infinity.MODID, "items/empty_armor_slot_sword" );
    
    public static final int GOOD_GREEN = 0x52b738;
    public static final int BAD_RED = 0xf44262;
    
    @Nullable
    public static GuiScreen getCurrentScreen()
    {
        return Minecraft.getMinecraft().currentScreen;
    }
    
    public static boolean isMouseInRegion( int mouseX, int mouseY, int xPos, int yPos, int width, int height )
    {
        return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
    }
    
    public static void addTooltipTranslated( GuiButton button, int mouseX, int mouseY, String str )
    {
        if ( button != null && button.enabled && button.visible )
        {
            addTooltipTranslated( button.x, button.y, button.width, button.height, mouseX, mouseY, str );
        }
    }
    
    public static void addTooltip( GuiButton button, int mouseX, int mouseY, String... str )
    {
        if ( button != null && button.enabled && button.visible )
        {
            addToolTip( button.x, button.y, button.width, button.height, mouseX, mouseY, str );
        }
    }
    
    public static void addTooltipTranslated( int xPos, int yPos, int width, int height, int mouseX, int mouseY, String str )
    {
        List<String> strings = new ArrayList<>();
        
        for ( int i = 1 ; i < 10 ; i++ )
        {
            String s = (str + "." + i);
            if ( I18n.hasKey( s ) )
            {
                strings.add( I18n.format( s ) );
            }
            else
            {
                break;
            }
        }
        
        if ( !strings.isEmpty() )
        {
            addToolTip( xPos, yPos, width, height, mouseX, mouseY, strings.toArray( new String[ strings.size() ] ) );
        }
        else
        {
            addToolTip( xPos, yPos, width, height, mouseX, mouseY, "missing localization: " + str );
        }
    }
    
    public static void addToolTip( int xPos, int yPos, int width, int height, int mouseX, int mouseY, String... str )
    {
        if ( isMouseInRegion( mouseX, mouseY, xPos, yPos, width, height ) )
        {
            if ( str.length == 1 )
            {
                getCurrentScreen().drawHoveringText( str[0], mouseX, mouseY );
            }
            else
            {
                List<String> strings = new ArrayList<>();
                
                for ( String s : str )
                {
                    strings.add( s );
                }
                
                getCurrentScreen().drawHoveringText( strings, mouseX, mouseY );
            }
        }
    }
    
    public static InventoryPlayer getInventoryPlayerCopy( InventoryPlayer source )
    {
        InventoryPlayer dest = new InventoryPlayer( source.player );
        
        for ( int i = 0 ; i < source.getSizeInventory() ; ++i )
        {
            dest.setInventorySlotContents( i, source.getStackInSlot( i ).copy() );
        }
        
        dest.currentItem = source.currentItem;
        
        return dest;
    }
    
    public static void dropStack( ItemStack stack )
    {
        if ( !stack.isEmpty() )
        {
            Minecraft.getMinecraft().player.inventory.player.dropItem( stack, true );
            Minecraft.getMinecraft().playerController.sendPacketDropItem( stack );
        }
    }
    
    public static int getColorFromRGB( int alpha, int red, int green, int blue )
    {
        int color = alpha << 24;
        color += red << 16;
        color += green << 8;
        color += blue;
        return color;
    }
    
    public static void openWebLink( URI url )
    {
        try
        {
            Class<?> oclass = Class.forName( "java.awt.Desktop" );
            Object object = oclass.getMethod( "getDesktop" ).invoke( (Object) null );
            oclass.getMethod( "browse", URI.class ).invoke( object, url );
        }
        catch ( Throwable throwable1 )
        {
            Throwable throwable = throwable1.getCause();
            Infinity.logger.error( "Couldn't open link: {}", (Object) (throwable == null ? "<UNKNOWN>" : throwable.getMessage()) );
        }
    }
    
    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, Entity ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.1F, 0.0F, 1.0F);
        float f1 = ent.rotationYaw;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        // GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) *
        // 20.0F, 1.0F, 0.0F, 0.0F);
        ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 25.0F;
        // ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) *
        // 20.0F;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.rotationYaw = f1;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        // float f = ActiveRenderInfo.getRotationX();
        // float f11 = ActiveRenderInfo.getRotationZ();
        // float f2 = ActiveRenderInfo.getRotationYZ();
        // float f3 = ActiveRenderInfo.getRotationXY();
        // float f4 = ActiveRenderInfo.getRotationXZ();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);

    }

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.1F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        // GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) *
        // 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 25.0F;
        ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 25.0F;
        // ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) *
        // 20.0F;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Draws a banner facing the mouse cursor.
     * 
     * @param posX
     * @param posY
     * @param scale
     * @param mouseX
     * @param mouseY
     * @param banner
     */
    public static void renderBanner(int posX, int posY, int scale, float mouseX, float mouseY, TileEntityBanner banner) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        // float f = ent.renderYawOffset;
        // float f1 = ent.rotationYaw;
        // float f2 = ent.rotationPitch;
        // float f3 = ent.prevRotationYawHead;
        // float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float) Math.atan((double) (mouseX / 40.0F))) * 20.5F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 2.0F, 1.0F, 0.0F, 0.0F);
        // ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) *
        // 20.0F;
        // ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        // ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) *
        // 20.0F;
        // ent.rotationYawHead = ent.rotationYaw;
        // ent.prevRotationYawHead = ent.rotationYaw;
        // GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        // rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        TileEntityRendererDispatcher.instance.render(banner, 0, 0, 0, Minecraft.getMinecraft().getRenderPartialTicks());
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
