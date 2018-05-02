package ruukas.infinity.gui;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEntityList extends GuiListExtended
{
    private final GuiEntityPicker entityPicker;
    private final List<GuiEntityListEntry> entries = Lists.<GuiEntityListEntry>newArrayList();
    /** Index to the currently selected world */
    private int selectedIdx = -1;

    public GuiEntityList(GuiEntityPicker entityPicker, Minecraft mc, int width, int height, int top, int bottom, int slotHeight)
    {
        super(mc, width, height, top, bottom, slotHeight);
        this.entityPicker = entityPicker;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiEntityListEntry getListEntry(int index)
    {
        return this.entries.get(index);
    }

    protected int getSize()
    {
        return this.entries.size();
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 20;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return super.getListWidth() + 50;
    }

    public void selectEntity(int idx)
    {
        this.selectedIdx = idx;
        this.entityPicker.selectEntity(this.getSelectedEntity());
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.selectedIdx;
    }

    @Nullable
    public GuiEntityListEntry getSelectedEntity()
    {
        return this.selectedIdx >= 0 && this.selectedIdx < this.getSize() ? this.getListEntry(this.selectedIdx) : null;
    }

    public GuiEntityPicker getGuiWorldSelection()
    {
        return this.entityPicker;
    }
}