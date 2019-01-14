package ruukas.infinity.nbt;

import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.util.Constants.NBT;

public class NBTHelper
{
    public static ItemStack generateNote( String noteName, String... lore )
    {
        ItemStack stack = new ItemStack( Items.PAPER );
        stack.setStackDisplayName( noteName );
        if ( lore != null )
        {
            NBTTagList loreTagList = new NBTTagList();
            for ( String str : lore )
            {
                loreTagList.appendTag( new NBTTagString( str ) );
            }
            stack.getTagCompound().getCompoundTag( "display" ).setTag( "Lore", loreTagList );
        }
        return stack;
    }
    
    public static void addLoreLine( ItemStack stack, String line )
    {
        NBTTagList lore = getLoreTagList( stack );
        
        lore.appendTag( new NBTTagString( line ) );
    }
    
    public static void removeLoreLine( ItemStack stack, int index )
    {
        NBTTagList lore = getLoreTagList( stack );
        
        if ( index >= lore.tagCount() )
        {
            if ( lore.tagCount() == 0 )
            {
                removeLore( stack );
            }
            
            return;
        }
        
        lore.removeTag( index );
        
        if ( lore.tagCount() == 0 )
        {
            removeLore( stack );
        }
    }
    
    public static void editLoreLine( ItemStack stack, int index, String line )
    {
        NBTTagList lore = getLoreTagList( stack );
        
        if ( index >= lore.tagCount() )
        {
            addLoreLine( stack, line );
        }
        else
        {
            lore.set( index, new NBTTagString( line ) );
            ;
        }
    }
    
    public static void removeLore( ItemStack stack )
    {
        NBTTagCompound displayTag = getDisplayTag( stack );
        
        if ( displayTag.hasKey( "Lore", NBT.TAG_LIST ) )
        {
            displayTag.removeTag( "Lore" );
        }
        
        removeDisplayTagIfEmpty( stack );
    }
    
    public static boolean hasLore( ItemStack stack )
    {
        return hasDisplayTag( stack ) && getDisplayTag( stack ).hasKey( "Lore", NBT.TAG_LIST );
    }
    
    public static NBTTagList getLoreTagList( ItemStack stack )
    {
        
        NBTTagCompound displayTag = getDisplayTag( stack );
        
        if ( !hasLore( stack ) )
        {
            displayTag.setTag( "Lore", new NBTTagList() );
        }
        
        return displayTag.getTagList( "Lore", NBT.TAG_STRING );
    }
    
    @Nullable
    public static String getLoreLine( ItemStack stack, int line )
    {
        if ( hasLore( stack ) )
        {
            NBTTagList lore = getLoreTagList( stack );
            
            if ( line < lore.tagCount() )
            {
                return lore.getStringTagAt( line );
            }
        }
        
        return null;
    }
    
    public static NBTTagCompound getDisplayTag( ItemStack stack )
    {
        return stack.getOrCreateSubCompound( "display" );
    }
    
    public static boolean hasDisplayTag( ItemStack stack )
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey( "display", NBT.TAG_COMPOUND );
    }
    
    public static void removeDisplayTagIfEmpty( ItemStack stack )
    {
        if ( hasDisplayTag( stack ) && getDisplayTag( stack ).hasNoTags() )
        {
            stack.getTagCompound().removeTag( "display" );
        }
        
        removeTagCompoundIfEmpty( stack );
    }
    
    public static NBTTagCompound getEntityTag( ItemStack stack )
    {
        return stack.getOrCreateSubCompound( "EntityTag" );
    }
    
    public static boolean hasEntityTag( ItemStack stack )
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND );
    }
    
    public static void removeEntityTagIfEmpty( ItemStack stack )
    {
        if ( hasEntityTag( stack ) && getEntityTag( stack ).hasNoTags() )
        {
            stack.getTagCompound().removeTag( "EntityTag" );
        }
        
        removeTagCompoundIfEmpty( stack );
    }
    
    public static boolean isUnbreakable( ItemStack stack )
    {
        if ( !stack.hasTagCompound() )
        {
            return false;
        }
        
        else
            return stack.getTagCompound().getBoolean( "Unbreakable" );
        
    }
    
    public static void setUnbreakable( ItemStack stack, boolean b )
    {
        setRootBoolean( stack, "Unbreakable", b );
    }
    
    public static NBTTagCompound getTagCompound( ItemStack stack )
    {
        if ( !stack.hasTagCompound() )
        {
            stack.setTagCompound( new NBTTagCompound() );
        }
        
        return stack.getTagCompound();
    }
    
    private static void setRootBoolean( ItemStack stack, String key, boolean b )
    {
        if ( b )
            getTagCompound( stack ).setBoolean( key, b );
        else if ( stack.hasTagCompound() )
        {
            NBTTagCompound tag = getTagCompound( stack );
            tag.removeTag( key );
            
            removeTagCompoundIfEmpty( stack );
        }
    }
    
    public static void removeTagCompoundIfEmpty( ItemStack stack )
    {
        if ( getTagCompound( stack ).hasNoTags() )
        {
            stack.setTagCompound( null );
        }
    }
    
    public static class RootBoolean
    {
        protected final String key;
        
        private RootBoolean(String key) {
            this.key = key;
        }
        
        public void switchValue( ItemStack stack )
        {
            setValue( stack, !getValue( stack ) );
        }
        
        public boolean getValue( ItemStack stack )
        {
            return stack.hasTagCompound() && getTagCompound( stack ).getBoolean( key );
        }
        
        public void setValue( ItemStack stack, boolean b )
        {
            setRootBoolean( stack, key, b );
        }
        
        public byte getByte( ItemStack stack )
        {
            if ( stack.hasTagCompound() )
                return stack.getTagCompound().getByte( key );
            else
                return 0;
        }
    }
    
    public static class EntityBoolean extends RootBoolean
    {
        private EntityBoolean(String key) {
            super( key );
        }
        
        public void switchValue( ItemStack stack )
        {
            setValue( stack, !getValue( stack ) );
        }
        
        public boolean getValue( ItemStack stack )
        {
            return hasEntityTag( stack ) && getEntityTag( stack ).getBoolean( key );
        }
        
        public void setValue( ItemStack stack, boolean b )
        {
            if ( b )
                getEntityTag( stack ).setBoolean( key, b );
            else if ( hasEntityTag( stack ) )
            {
                NBTTagCompound tag = getEntityTag( stack );
                tag.removeTag( key );
                
                removeEntityTagIfEmpty( stack );
            }
        }
        
        public byte getByte( ItemStack stack )
        {
            if ( hasEntityTag( stack ) )
                return getEntityTag( stack ).getByte( key );
            else
                return 0;
        }
    }
    
    public static enum EnumPosePart {
        BODY( "Body" ), LEFT_ARM( "LeftArm" ), RIGHT_ARM( "RightArm" ), LEFT_LEG( "LeftLeg" ), RIGHT_LEG( "RightLeg" ), HEAD( "Head" );
        
        private final String key;
        
        private EnumPosePart(String key) {
            this.key = key;
        }
        
        public String getKey()
        {
            return this.key;
        }
    }
    
    public static class SkullNBTHelper
    {
        
        public static GameProfile getSkullOwner( ItemStack stack )
        {
            GameProfile profile = null;
            
            if ( stack.hasTagCompound() )
            {
                NBTTagCompound nbttagcompound = stack.getTagCompound();
                
                if ( nbttagcompound.hasKey( "SkullOwner", 10 ) )
                {
                    profile = NBTUtil.readGameProfileFromNBT( nbttagcompound.getCompoundTag( "SkullOwner" ) );
                }
                else if ( nbttagcompound.hasKey( "SkullOwner", 8 ) && !StringUtils.isBlank( nbttagcompound.getString( "SkullOwner" ) ) )
                {
                    profile = new GameProfile( (UUID) null, nbttagcompound.getString( "SkullOwner" ) );
                }
            }
            
            return profile;
        }
        
        public static void setSkullOwner( ItemStack stack, String owner )
        {
            getTagCompound( stack ).setString( "SkullOwner", owner );
        }
    }
    
    public static class ColorNBTHelper
    {
        public static boolean isPotion( ItemStack stack )
        {
            return stack.getItem() instanceof ItemPotion || stack.getItem() instanceof ItemTippedArrow;
        }
        
        public static int getColorAsInt( ItemStack stack )
        {
            if ( isPotion( stack ) )
            {
                return PotionUtils.getColor( stack );
            }
            
            if ( stack.getItem().isMap() )
            {
                return hasDisplayTag( stack ) ? getDisplayTag( stack ).getInteger( "MapColor" ) : 0;
            }
            
            if ( hasColor( stack ) )
            {
                return getDisplayTag( stack ).getInteger( "color" );
            }
            
            if ( applicableForColor( stack ) )
            {
                return 10511680;
            }
            
            return 0;
        }
        
        public static boolean hasColor( ItemStack stack )
        {
            if ( isPotion( stack ) )
            {
                return stack.hasTagCompound() && stack.getTagCompound().hasKey( "CustomPotionColor", NBT.TAG_INT );
            }
            else if ( stack.getItem().isMap() )
            {
                return hasDisplayTag( stack ) && getDisplayTag( stack ).hasKey( "MapColor", NBT.TAG_INT );
            }
            return hasDisplayTag( stack ) && getDisplayTag( stack ).hasKey( "color", NBT.TAG_INT );
        }
        
        public static boolean applicableForColor( ItemStack stack )
        {
            return isPotion( stack ) || stack.getItem().isMap() || stack.getItem() instanceof ItemArmor && ((ItemArmor) stack.getItem()).getArmorMaterial() == ArmorMaterial.LEATHER;
        }
        
        public static void setColor( ItemStack stack, int color )
        {
            if ( isPotion( stack ) )
            {
                if ( !stack.hasTagCompound() )
                {
                    stack.setTagCompound( new NBTTagCompound() );
                }
                
                stack.getTagCompound().setInteger( "CustomPotionColor", color );
            }
            else
            {
                getDisplayTag( stack ).setInteger( stack.getItem().isMap() ? "MapColor" : "color", color );
                
            }
        }
        
        public static int getRed( ItemStack stack )
        {
            return getColorAsInt( stack ) >> 16 & 255;
        }
        
        public static void setRed( ItemStack stack, int red )
        {
            int color = red << 16;
            color += getGreen( stack ) << 8;
            color += getBlue( stack );
            
            setColor( stack, color );
        }
        
        public static int getGreen( ItemStack stack )
        {
            return getColorAsInt( stack ) >> 8 & 255;
        }
        
        public static void setGreen( ItemStack stack, int green )
        {
            int color = getRed( stack ) << 16;
            color += green << 8;
            color += getBlue( stack );
            
            setColor( stack, color );
        }
        
        public static int getBlue( ItemStack stack )
        {
            return getColorAsInt( stack ) & 255;
        }
        
        public static void setBlue( ItemStack stack, int blue )
        {
            int color = getRed( stack ) << 16;
            color += getGreen( stack ) << 8;
            color += blue;
            
            setColor( stack, color );
        }
        
        public static void addDye( ItemStack stack, EnumDyeColor dye )
        {
            ItemStack itemstack = ItemStack.EMPTY;
            int[] aint = new int[ 3 ];
            int i = 0;
            int j = 0;
            ItemArmor itemarmor = null;
            
            ItemStack[] stacks = { stack, new ItemStack( Items.DYE, 1, dye.getDyeDamage() ) };
            
            for ( int k = 0 ; k < 2 ; ++k )
            {
                ItemStack itemstack1 = stacks[k];
                
                if ( !itemstack1.isEmpty() )
                {
                    if ( itemstack1.getItem() instanceof ItemArmor )
                    {
                        itemarmor = (ItemArmor) itemstack1.getItem();
                        
                        if ( itemarmor.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER || !itemstack.isEmpty() )
                        {
                            return;
                        }
                        
                        itemstack = itemstack1;
                        itemstack.setCount( 1 );
                        
                        if ( itemarmor.hasColor( itemstack1 ) )
                        {
                            int l = itemarmor.getColor( itemstack );
                            float f = (float) (l >> 16 & 255) / 255.0F;
                            float f1 = (float) (l >> 8 & 255) / 255.0F;
                            float f2 = (float) (l & 255) / 255.0F;
                            i = (int) ((float) i + Math.max( f, Math.max( f1, f2 ) ) * 255.0F);
                            aint[0] = (int) ((float) aint[0] + f * 255.0F);
                            aint[1] = (int) ((float) aint[1] + f1 * 255.0F);
                            aint[2] = (int) ((float) aint[2] + f2 * 255.0F);
                            ++j;
                        }
                    }
                    else
                    {
                        if (itemstack1.getItem() != Items.DYE)
                        {
                            return;
                        }
                        
                        float[] afloat = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(itemstack1.getMetadata()));
                        int l1 = (int) (afloat[0] * 255.0F);
                        int i2 = (int) (afloat[1] * 255.0F);
                        int j2 = (int) (afloat[2] * 255.0F);
                        i += Math.max( l1, Math.max( i2, j2 ) );
                        aint[0] += l1;
                        aint[1] += i2;
                        aint[2] += j2;
                        ++j;
                    }
                }
            }
            
            if ( itemarmor == null )
            {
                return;
            }
            else
            {
                int i1 = aint[0] / j;
                int j1 = aint[1] / j;
                int k1 = aint[2] / j;
                float f3 = (float) i / (float) j;
                float f4 = (float) Math.max( i1, Math.max( j1, k1 ) );
                i1 = (int) ((float) i1 * f3 / f4);
                j1 = (int) ((float) j1 * f3 / f4);
                k1 = (int) ((float) k1 * f3 / f4);
                int k2 = (i1 << 8) + j1;
                k2 = (k2 << 8) + k1;
                itemarmor.setColor( itemstack, k2 );
                return;
            }
        }
    }
    
    public static class ArmorStandNBTHelper
    {
        public static final RootBoolean SHOW_ARMS = new EntityBoolean( "ShowArms" );
        public static final RootBoolean SMALL = new EntityBoolean( "Small" );
        public static final RootBoolean INVISIBLE = new EntityBoolean( "Invisible" );
        public static final RootBoolean NO_BASE = new EntityBoolean( "NoBasePlate" );
        public static final RootBoolean MARKER = new EntityBoolean( "Marker" );
        
        public static NBTTagCompound getPoseTag( ItemStack stack )
        {
            NBTTagCompound entityTag = getEntityTag( stack );
            
            if ( !entityTag.hasKey( "Pose", NBT.TAG_COMPOUND ) )
            {
                entityTag.setTag( "Pose", new NBTTagCompound() );
            }
            
            return entityTag.getCompoundTag( "Pose" );
        }
        
        public static boolean hasPoseTag( ItemStack stack )
        {
            return hasEntityTag( stack ) && getEntityTag( stack ).hasKey( "Pose", NBT.TAG_COMPOUND );
        }
        
        public static void removePoseTagIfEmpty( ItemStack stack )
        {
            if ( hasPoseTag( stack ) && getPoseTag( stack ).hasNoTags() )
            {
                getEntityTag( stack ).removeTag( "Pose" );
            }
            
            removeEntityTagIfEmpty( stack );
        }
        
        public static void setRotations( ItemStack stack, EnumPosePart part, float x, float y, float z )
        {
            NBTTagList rot = getRotationsTag( stack, part );
            
            if ( rot.hasNoTags() )
            {
                rot.appendTag( new NBTTagFloat( x ) );
                rot.appendTag( new NBTTagFloat( y ) );
                rot.appendTag( new NBTTagFloat( z ) );
            }
            else
            {
                rot.set( 0, new NBTTagFloat( x ) );
                rot.set( 1, new NBTTagFloat( y ) );
                rot.set( 2, new NBTTagFloat( z ) );
            }
        }
        
        public static void setX( ItemStack stack, EnumPosePart part, float x )
        {
            setRotations( stack, part, x, getY( stack, part ), getZ( stack, part ) );
        }
        
        public static void setY( ItemStack stack, EnumPosePart part, float y )
        {
            setRotations( stack, part, getX( stack, part ), y, getZ( stack, part ) );
        }
        
        public static void setZ( ItemStack stack, EnumPosePart part, float z )
        {
            setRotations( stack, part, getX( stack, part ), getY( stack, part ), z );
        }
        
        public static float getX( ItemStack stack, EnumPosePart part )
        {
            if ( !hasRotations( stack, part ) )
            {
                return 0f;
            }
            
            return getRotationsArray( stack, part )[0];
        }
        
        public static float getY( ItemStack stack, EnumPosePart part )
        {
            if ( !hasRotations( stack, part ) )
            {
                return 0f;
            }
            
            return getRotationsArray( stack, part )[1];
        }
        
        public static float getZ( ItemStack stack, EnumPosePart part )
        {
            if ( !hasRotations( stack, part ) )
            {
                return 0f;
            }
            
            return getRotationsArray( stack, part )[2];
        }
        
        public static NBTTagList getRotationsTag( ItemStack stack, EnumPosePart part )
        {
            NBTTagCompound poseTag = getPoseTag( stack );
            
            if ( !poseTag.hasKey( part.getKey(), NBT.TAG_LIST ) )
            {
                poseTag.setTag( part.getKey(), new NBTTagList() );
            }
            
            return poseTag.getTagList( part.getKey(), NBT.TAG_FLOAT );
        }
        
        public static float[] getRotationsArray( ItemStack stack, EnumPosePart part )
        {
            if ( hasRotations( stack, part ) )
            {
                NBTTagList rot = getRotationsTag( stack, part );
                if ( !rot.hasNoTags() )
                {
                    return new float[] { rot.getFloatAt( 0 ), rot.getFloatAt( 1 ), rot.getFloatAt( 2 ) };
                }
            }
            
            return new float[] { 0f, 0f, 0f };
        }
        
        public static boolean hasRotations( ItemStack stack, EnumPosePart part )
        {
            return hasPoseTag( stack ) && getPoseTag( stack ).hasKey( part.key, NBT.TAG_LIST );
        }
        
        public static void removeRotationIfEmpty( ItemStack stack, EnumPosePart part )
        {
            if ( hasRotations( stack, part ) )
            {
                NBTTagList rot = getRotationsTag( stack, part );
                if ( rot.hasNoTags() || (rot.getFloatAt( 0 ) == 0f && rot.getFloatAt( 1 ) == 0f && rot.getFloatAt( 2 ) == 0f) )
                    getPoseTag( stack ).removeTag( part.getKey() );
            }
            
            removePoseTagIfEmpty( stack );
        }
    }
}
