package ruukas.infinityeditor.gui;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.gui.action.GuiInfinityButton;
import ruukas.infinityeditor.util.GiveHelper;

public abstract class GuiInfinity extends GuiScreen {

    protected final GuiScreen lastScreen;
    protected ItemStackHolder stackHolder;
    protected String title;

    protected GuiInfinityButton backButton, resetButton, dropButton;
    protected int buttonID;

    int midX;
    int midY;

    protected GuiInfinityButton saveButton;
    protected boolean hasSave = false;

    private boolean renderStack = false;
    private int stackX = 5;
    private int stackY = 5;
    // private float stackScale = 1.0f;

    protected boolean renderTooltip = false;

    protected boolean renderTag = false;

    protected GuiInfinity(GuiScreen lastScreen, ItemStackHolder itemStackHolder) {
        this.lastScreen = lastScreen;
        this.stackHolder = itemStackHolder;

        title = I18n.format("gui." + getNameUnlocalized());
    }

    public static class ItemStackHolder {
        protected ItemStack stack;

        public ItemStackHolder() {
            this(ItemStack.EMPTY);
        }

        public ItemStackHolder(ItemStack stack) {
            this.stack = stack;
        }

        public void setStack(ItemStack stack) {
            if (stack != null) {
                this.stack = stack;
            }
        }

        public ItemStack getStack() {
            return this.stack;
        }
    }

    protected abstract String getNameUnlocalized();

    // TODO consider scale in position (See GuiColor for example)
    protected void setRenderStack(boolean doRender, int x, int y, float scale) {
        renderStack = doRender;
        stackX = x - 8;
        stackY = y - 8;
        // stackScale = scale;
    }

    @Override
    public void initGui() {
        midX = width / 2;
        midY = height / 2;

        buttonList.clear(); // Most of the time this will already be empty, but it's a good extra precaution to have
        buttonID = 200;

        if (hasSave) {
            backButton = addButton(new GuiInfinityButton(buttonID++, midX - 90, height - 35, 60, 20, I18n.format("gui.close")));
            saveButton = addButton(new GuiInfinityButton(buttonID++, midX - 30, height - 25, 60, 20, I18n.format("gui.save")));
            resetButton = addButton(new GuiInfinityButton(buttonID++, midX - 30, height - 45, 60, 20, I18n.format("gui.reset")));
        } else {
            backButton = addButton(new GuiInfinityButton(buttonID++, midX - 90, height - 35, 60, 20, I18n.format("gui.back"))); // Back instead of close
            saveButton = null; // If for some reason "hasSave" was changed since last init. Note: initGui is called by setWorldAndResolution().
            resetButton = addButton(new GuiInfinityButton(buttonID++, midX - 30, height - 35, 60, 20, I18n.format("gui.reset"))); // Y is different, as the button isn't offset to make space for saveButton.
        }

        dropButton = addButton(new GuiInfinityButton(buttonID++, midX + 30, height - 35, 60, 20, I18n.format("gui.drop"))); // saveButton doesnt influence the dropButton
    }

    /**
     * Should be called whenever a change is made to the ItemStack.
     */
    protected void update() {

    }

    protected void back() {
        mc.displayGuiScreen(lastScreen);

        if (mc.currentScreen == null) {
            mc.setIngameFocus();
        }
    }

    protected void save() {
        mc.playerController.sendSlotPacket(getItemStack(), mc.player.inventory.currentItem + 36); // 36 is the index of the action (4 armor, 1 off hand, 5 crafting, and 27 inventory, if I remember correctly).
        // back(); - Not sure if it should keep the GUI open or not.
    }

    protected void drop() {
        if (isShiftKeyDown()) {
            String s = GiveHelper.getStringFromItemStack(getItemStack());
            GuiScreen.setClipboardString(s);
        } else {
            HelperGui.dropStack(getItemStack());
        }
    }

    protected void reset() {
        if (getItemStack().hasTagCompound()) {
            getItemStack().setTagCompound(null);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == backButton.id) {
            back();
        } else if (hasSave && button.id == saveButton.id) {
            save();
        } else if (button.id == dropButton.id) {
            drop();
        } else if (button.id == resetButton.id) {
            reset();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) { // Esc
            back();
        } else if (hasSave && (keyCode == 28 || keyCode == 156)) { // Enter or NumpadEnter
            save();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (hasSave && this.saveButton != null)
            this.saveButton.enabled = this.dropButton.enabled = mc.playerController.isInCreativeMode() || mc.isSingleplayer();

        drawDefaultBackground();

        if (getItemStack().getItem() != Items.AIR && getItemStack() != ItemStack.EMPTY) {

            if (renderTooltip || renderTag) {
                GlStateManager.pushMatrix();

                GL11.glScalef(0.8f, 0.8f, 0.8f);

                if (renderTooltip)
                    renderToolTip(getItemStack(), 0, 25);

                if (renderTag) {
                    // This shouldn't be done on each draw
                    String s = new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(getItemStack().hasTagCompound() ? getItemStack().getTagCompound().toString() : "{}"));
                    ArrayList<String> prettyNBTList = new ArrayList<>();
                    for (String str : s.split("\\n")) {
                        prettyNBTList.add(TextFormatting.DARK_PURPLE + str);
                    }

                    drawHoveringText(prettyNBTList, 0, height);
                }

                GlStateManager.popMatrix();
            }

            if (renderStack) {
                GlStateManager.pushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.disableLighting();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableColorMaterial();
                GlStateManager.enableLighting();
                itemRender.zLevel = 100.0F;
                itemRender.renderItemAndEffectIntoGUI(getItemStack(), stackX, stackY);
                itemRender.renderItemOverlays(fontRenderer, getItemStack(), stackX, stackY);

                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
            }
        }

        drawCenteredString(fontRenderer, title, midX, 15, InfinityConfig.MAIN_COLOR);

        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.color(1.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public ItemStack getItemStack() {
        return stackHolder.getStack();
    }
}
