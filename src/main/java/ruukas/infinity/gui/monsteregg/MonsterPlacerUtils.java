package ruukas.infinity.gui.monsteregg;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;

public class MonsterPlacerUtils
{
    
    public static void setEntityID( ItemStack stack, ResourceLocation loc )
    {
        if ( loc != null && stack.getItem() instanceof ItemMonsterPlacer )
        {
            if ( !stack.hasTagCompound() )
            {
                stack.setTagCompound( new NBTTagCompound() );
            }
            if ( !stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
            {
                stack.getTagCompound().setTag( "EntityTag", new NBTTagCompound() );
            }
            stack.getSubCompound( "EntityTag" ).setString( "id", loc.toString() );
        }
    }
    
    public static ResourceLocation getPreviousEntityEgg( ResourceLocation res )
    {
        if ( res != null )
        {
            int i = 0;
            Object[] array = EntityList.ENTITY_EGGS.keySet().toArray();
            for ( Object otherRes : array )
            {
                if ( res.equals( otherRes ) )
                {
                    return i != 0 ? (ResourceLocation) array[i - 1] : (ResourceLocation) array[array.length - 1];
                }
                i++;
            }
        }
        
        return new ResourceLocation( "minecraft", "bat" );
    }
    
    public static ResourceLocation getNextEntityEgg( ResourceLocation res )
    {
        if ( res != null )
        {
            boolean returnNext = false;
            for ( ResourceLocation otherRes : EntityList.ENTITY_EGGS.keySet() )
            {
                if ( res.equals( otherRes ) )
                {
                    returnNext = true;
                }
                else if ( returnNext )
                {
                    return otherRes;
                }
            }
        }
        
        // If it never returned anything that should mean that "res" is the last
        // key. So the first key is returned.
        return (ResourceLocation) EntityList.ENTITY_EGGS.keySet().toArray()[0];
    }
    
    public static String getButtonText( MobTag tag, ItemStack stack )
    {
        if ( tag instanceof MobTagSlider )
        {
            MobTagSlider tagSlider = (MobTagSlider) tag;
            
            float value = tagSlider.getFloat( stack );
            
            /*
             * if(tag instanceof MobTagSliderByte){ value = ((MobTagSliderByte)tag).getByte(stack); }else if(tag instanceof MobTagSliderShort){ value = ((MobTagSliderShort)tag).getShort(stack); }else{
             * 
             * }
             */
            if ( value <= tagSlider.min )
            {
                return I18n.format( tag.getName() + ".off", (int) value );
            }
            return I18n.format( tag.getName() + ".on", (int) value );
        }
        else if ( tag instanceof MobTagToggle )
        {
            MobTagToggle tagToggle = (MobTagToggle) tag;
            boolean isEnabled = tagToggle.getValue( stack );
            return isEnabled ? I18n.format( tag.getName() + ".on" ) : I18n.format( tag.getName() + ".off" );
        }
        else if ( tag instanceof MobTagString )
        {
            return tag.getTranslatedName();
        }
        else if ( tag instanceof MobTagList )
        {
            MobTagList tagList = (MobTagList) tag;
            return I18n.format( tag.getName() + "." + tagList.getValue( stack ) );
        }
        else
            return "No button text found for " + tag.getTranslatedName();
    }
    
    /**
     * For non-float options. Toggles the option on/off, or cycles through the list i.e. render distances.
     */
    public static void setOptionValue( MobTag tag, ItemStack stack, int value )
    {
        if ( tag instanceof MobTagToggle )
        {
            MobTagToggle tagToggle = (MobTagToggle) tag;
            tagToggle.switchToggle( stack );
        }
        else if ( tag instanceof MobTagList )
        {
            MobTagList tagList = (MobTagList) tag;
            tagList.nextValue( stack );
        }
    }
    
    public static MobTag[] getSpecificTagsForEntity( EntityLiving ent )
    {
        if ( ent instanceof EntityChicken )
        {
            return MobTag.CHICKEN_SPECIFIC;
        }
        
        if ( ent instanceof EntityCreeper )
        {
            return MobTag.CREEPER_SPECIFIC;
        }
        
        if ( ent instanceof EntityEndermite )
        {
            return MobTag.ENDERMITE_SPECIFIC;
        }
        
        if ( ent instanceof EntityParrot )
        {
            return MobTag.PARROT_SPECIFIC;
        }
        
        if ( ent instanceof EntityPig )
        {
            return MobTag.PIG_SPECIFIC;
        }
        
        if ( ent instanceof EntitySheep )
        {
            return MobTag.SHEEP_SPECIFIC;
        }
        
        if ( ent instanceof EntityShulker )
        {
            return MobTag.SHULKER_SPECIFIC;
        }
        
        if ( ent instanceof EntitySlime )
        {
            return MobTag.SLIME_SPECIFIC;
        }
        
        if ( ent instanceof EntityVindicator )
        {
            return MobTag.VINDICATOR_SPECIFIC;
        }
        
        if ( ent instanceof EntityVillager )
        {
            return MobTag.VILLAGER_SPECIFIC;
        }
        
        if ( ent instanceof EntityZombie )
        {
            if ( !(ent instanceof EntityZombieVillager) )
            {
                return MobTag.ZOMBIE_SPECIFIC;
            }
            else
                return MobTag.ZOMBIEVILLAGER_SPECIFIC;
        }
        
        return new MobTag[ 0 ];
    }
}
