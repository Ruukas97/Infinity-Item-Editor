package ruukas.infinity.gui.action;

import java.util.Comparator;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmorStand;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import ruukas.infinity.gui.GuiArmorStand;
import ruukas.infinity.gui.GuiAttributes;
import ruukas.infinity.gui.GuiBannerMaker;
import ruukas.infinity.gui.GuiBook;
import ruukas.infinity.gui.GuiChestItem;
import ruukas.infinity.gui.GuiColor;
import ruukas.infinity.gui.GuiEnchanting;
import ruukas.infinity.gui.GuiFireworksMaker;
import ruukas.infinity.gui.GuiHead;
import ruukas.infinity.gui.GuiMonsterEgg;
import ruukas.infinity.gui.GuiPotion;
import ruukas.infinity.gui.GuiSign;
import ruukas.infinity.gui.HelperGui;
import ruukas.infinity.gui.chest.InventoryChestItem;
import ruukas.infinity.nbt.NBTHelper;

public class ActionButtons {
    public static GuiActionButton[] getActionButtons() {
        return new GuiActionButton[] { new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack().getItem().getMaxDamage( getItemStack() ) > 0;
            }


            @Override
            public void action() {
                NBTHelper.setUnbreakable( getItemStack(), !NBTHelper.isUnbreakable( getItemStack() ) );
                displayString = getText();
            }


            @Override
            public String getText() {
                return I18n.format( "tag.unbreakable." + (NBTHelper.isUnbreakable( getItemStack() ) ? 1 : 0) );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack() != ItemStack.EMPTY && getItemStack().getCount() > 0;
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiAttributes( Minecraft.getMinecraft().currentScreen, getItemStackHolder() ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.attributes" );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack().getItem() instanceof ItemMonsterPlacer;
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiMonsterEgg( Minecraft.getMinecraft().currentScreen, getItemStackHolder() ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.spawnegg" );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack().getItem() instanceof ItemBlock && (((ItemBlock) getItemStack().getItem()).getBlock() instanceof BlockChest || ((ItemBlock) getItemStack().getItem()).getBlock() instanceof BlockShulkerBox);
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiChestItem( Minecraft.getMinecraft().currentScreen, HelperGui.getInventoryPlayerCopy( Minecraft.getMinecraft().player.inventory ), new InventoryChestItem( getItemStack() ) ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.chestinventory" );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack().getItem() instanceof ItemSkull;
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiHead( Minecraft.getMinecraft().currentScreen, getItemStack() ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.head" );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack().getItem() instanceof ItemArmorStand;
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiArmorStand( Minecraft.getMinecraft().currentScreen, getItemStackHolder() ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.armorstand" );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return NBTHelper.ColorNBTHelper.applicableForColor( getItemStack() );
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiColor( Minecraft.getMinecraft().currentScreen, getItemStack() ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.color" );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack().getItem().isEnchantable( getItemStack() ) || EnumEnchantmentType.ALL.canEnchantItem( getItemStack().getItem() ) || getItemStack().getItem() == Items.ENCHANTED_BOOK;
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiEnchanting( Minecraft.getMinecraft().currentScreen, getItemStackHolder() ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.enchanting" );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack().getItem() == Items.POTIONITEM || getItemStack().getItem() == Items.SPLASH_POTION || getItemStack().getItem() == Items.LINGERING_POTION || getItemStack().getItem() == Items.TIPPED_ARROW;
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiPotion( Minecraft.getMinecraft().currentScreen, getItemStackHolder() ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.potion" );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack().getItem() == Items.WRITTEN_BOOK;
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiBook( Minecraft.getMinecraft().currentScreen, getItemStackHolder() ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.book" );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack().getItem() == Items.BANNER || getItemStack().getItem() == Items.SHIELD;
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiBannerMaker( Minecraft.getMinecraft().currentScreen, Minecraft.getMinecraft().player ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.bannermaker" );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack().getItem() == Items.FIREWORKS;
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiFireworksMaker( Minecraft.getMinecraft().currentScreen, Minecraft.getMinecraft().player ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.fireworks" );
            }
        }, new GuiActionButton() {
            @Override
            public boolean condition() {
                return getItemStack().getItem() == Items.SIGN;
            }


            @Override
            public void action() {
                Minecraft.getMinecraft().displayGuiScreen( new GuiSign( Minecraft.getMinecraft().currentScreen, getItemStackHolder() ) );
            }


            @Override
            public String getText() {
                return I18n.format( "gui.sign" );
            }
        } };
    }


    public static class SorterGuiActionButton implements Comparator<GuiActionButton> {
        @Override
        public int compare( GuiActionButton o1, GuiActionButton o2 ) {
            return o1.getText().compareTo( o2.getText() );
        }

    }
}
