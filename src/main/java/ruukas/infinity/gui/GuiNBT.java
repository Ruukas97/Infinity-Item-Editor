package ruukas.infinity.gui;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.gui.action.GuiInfinityButton;

@SideOnly(Side.CLIENT)
public class GuiNBT extends GuiScreen {

	private ItemStack stack = ItemStack.EMPTY;

	private final GuiScreen lastScreen;

	private GuiInfinityButton updateNbtButton;
	private GuiInfinityButton backButton, resetButton;
	
	private GuiTextField nbtTextField;
	
	protected String title = I18n.format("gui.nbt");
	
	protected ArrayList<String> prettyNBTList = new ArrayList<>();
		
	private String feedback = "";
	private boolean good = false;


	public GuiNBT(GuiScreen lastScreen, ItemStack stack) {
		this.lastScreen = lastScreen;
		this.stack = stack;
	}
	

	@Override
	public void initGui() {
        Keyboard.enableRepeatEvents(true);
		nbtTextField = new GuiTextField(100, this.fontRenderer, this.width/4, 80, this.width/2, 16);
		nbtTextField.setMaxStringLength(5000);
		nbtTextField.setText(stack.hasTagCompound() ? stack.getTagCompound().toString() : "{}");
		
		updateNbtButton = this.addButton(new GuiInfinityButton(105, 3*width/7, 100, width/7, 20, I18n.format("gui.nbt.update")));
		
		backButton = addButton(new GuiInfinityButton(200, this.width / 2 - 60, this.height - 25, 60, 20, I18n.format("gui.back")));
		resetButton = addButton(new GuiInfinityButton(201, this.width / 2, this.height - 25, 60, 20, I18n.format("gui.reset")));
	}
	
	@Override
	public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
	}
	
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
    	nbtTextField.updateCursorCounter();
    }
    

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is
	 * the equivalent of KeyListener.keyTyped(KeyEvent e). Args : character
	 * (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) {
			this.actionPerformed(this.backButton);
		} else if (keyCode == 28 || keyCode == 156) {
			this.actionPerformed(this.updateNbtButton);
		} else {
			nbtTextField.textboxKeyTyped(typedChar, keyCode);
		}
	}
	
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        
        nbtTextField.mouseClicked(mouseX, mouseY, mouseButton);
	}


	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == updateNbtButton.id){
            try
            {
                stack.setTagCompound(JsonToNBT.getTagFromJson(nbtTextField.getText()));
                good = true;
                feedback = "Looks good";
            }
            catch (NBTException nbtexception)
            {
            	good = false;
            	feedback = nbtexception.getMessage();
            }
        }
		
		else if (button.id == backButton.id) {
			this.mc.displayGuiScreen(lastScreen);

			if (this.mc.currentScreen == null) {
				this.mc.setIngameFocus();
			}
		}
		
		else if (button.id == resetButton.id) {
			if(stack.hasTagCompound()){
				stack.setTagCompound(new NBTTagCompound());
			}
		} 
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		
		GlStateManager.pushMatrix();
		GL11.glScalef(0.8f, 0.8f, 0.8f);
		this.renderToolTip(stack, 0, 25);
		
		String s = stack.hasTagCompound() ? stack.getTagCompound().toString() : "{}";

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(s);
		s = gson.toJson(je);

		prettyNBTList.clear();
		for (String str : s.split("\\n")) {
			prettyNBTList.add(str);
		}
		
		drawHoveringText(prettyNBTList, 0, this.height);

		GlStateManager.popMatrix();		
		
		GlStateManager.pushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableLighting();
		this.itemRender.renderItemAndEffectIntoGUI(stack, (width / 2) - 8, 30);
		GlStateManager.popMatrix();

		this.drawCenteredString(fontRenderer, title, width / 2, 15, HelperGui.TITLE_PURPLE);		
		
		this.drawCenteredString(fontRenderer, feedback, width / 2, 130, good ? HelperGui.GOOD_GREEN : HelperGui.BAD_RED);		

		nbtTextField.drawTextBox();
				
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public ItemStack getItemStack() {
		return stack;
	}
}
