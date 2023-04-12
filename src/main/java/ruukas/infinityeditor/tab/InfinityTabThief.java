package ruukas.infinityeditor.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import ruukas.infinityeditor.nbt.NBTHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class InfinityTabThief extends InfinityTab {

    public InfinityTabThief(int i) {
        super(i, "thief");
    }

    @Override
    public void displayAllRelevantItems(NonNullList<ItemStack> stackList) {
        for (EntityPlayer entity : Minecraft.getMinecraft().world.playerEntities) {
            if (entity == null || entity == Minecraft.getMinecraft().player)
                continue;

            boolean addedNote = false;
            for (ItemStack stack : entity.getEquipmentAndArmor()) {
                if (stack != null && stack != ItemStack.EMPTY) {
                    boolean shouldAdd = true;
                    for (ItemStack stackInList : stackList) {
                        if (ItemStack.areItemStacksEqual(stack, stackInList)) {
                            shouldAdd = false;
                            break;
                        }
                    }
                    if (shouldAdd) {
                        if (!addedNote) {
                            stackList.add(NBTHelper.generateNote("Stolen from " + entity.getName(), (String[]) null));
                            addedNote = true;
                        }
                        stackList.add(stack);
                    }
                }
            }
        }

        boolean addedAnyChat = false;
        List<ChatLine> chatLines = getChatLines();
        if (chatLines == null) return;
        for (ChatLine line : chatLines) {
            for (ITextComponent comp : line.getChatComponent()) {
                Style style = comp.getStyle();
                if (style.getHoverEvent() != null && style.getHoverEvent().getAction() == Action.SHOW_ITEM) {
                    ItemStack stack = ItemStack.EMPTY;

                    try {
                        NBTTagCompound nbt = JsonToNBT.getTagFromJson(style.getHoverEvent().getValue().getUnformattedText());
                        stack = new ItemStack(nbt);
                    } catch (NBTException ignored) {
                    }

                    if (!stack.isEmpty()) {
                        boolean shouldAdd = true;
                        for (ItemStack stackInList : stackList) {
                            if (ItemStack.areItemStacksEqual(stack, stackInList)) {
                                shouldAdd = false;
                                break;
                            }
                        }
                        if (shouldAdd) {
                            if (!addedAnyChat) {
                                stackList.add(NBTHelper.generateNote("Linked in chat", "Items linked in chat, such as death messages"));
                                addedAnyChat = true;
                            }
                            stackList.add(stack);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static List<ChatLine> getChatLines() {
        List<ChatLine> chatLine = null;

        try {
            Field chatLinesField = ObfuscationReflectionHelper.findField(GuiNewChat.class, "chatLines");
            // Useful source for getting SRG names: mcpbot.bspk.rs
            // field_146252_h,chatLines,0,Chat lines to be displayed in the chat
            // box
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(chatLinesField, chatLinesField.getModifiers() & ~Modifier.FINAL);

            chatLine = (List<ChatLine>) chatLinesField.get(Minecraft.getMinecraft().ingameGUI.getChatGUI());
        } catch (IllegalArgumentException | UnableToFindFieldException | SecurityException | NoSuchFieldException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }

        return chatLine;
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Items.FEATHER);
    }
}
