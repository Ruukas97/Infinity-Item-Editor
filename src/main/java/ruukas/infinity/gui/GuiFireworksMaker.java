package ruukas.infinity.gui;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.CreativeCrafting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.util.QualityHelper;

public class GuiFireworksMaker extends InventoryEffectRenderer {
	private static final ResourceLocation background = new ResourceLocation("qualityorder", "textures/gui/fireworkmaker.png");
    private final GuiScreen lastScreen;
	private EntityFireworkRocket rocket;
	private int tick = 100;
	private float initialPitch;
	private static final InventoryBasic firework = new InventoryBasic("tmp", false, 1);
	private CreativeCrafting listener;

	public GuiFireworksMaker(GuiScreen lastScreen, EntityPlayer player) {
		super(new ContainerFirework(player));
		this.lastScreen = lastScreen;

		player.openContainer = this.inventorySlots;
		this.allowUserInput = false;

		this.xSize = 162;
		this.ySize = 40;
	}

	@Override
	public void initGui() {

		super.initGui();
		this.guiLeft = (int) (this.width - this.xSize) / 2;
		this.guiTop = (int) (this.height - this.ySize - 5);

		this.listener = new CreativeCrafting(this.mc);
		this.mc.player.inventoryContainer.addListener(this.listener);

		ItemStack stack = QualityHelper.getFirstStackOfItemInMainInventory(Items.FIREWORKS);

		if (stack != null) {
			setCurrentRocket(stack.copy());
		}
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getCurrentRocket() {
		return firework.getStackInSlot(0);
	}

	@SideOnly(Side.CLIENT)
	private void setCurrentRocket(ItemStack rocketStack) {
		if (rocketStack != null && rocketStack.getItem() instanceof ItemFirework) {
			rocket = new EntityFireworkRocket(this.mc.world, rocketStack, this.mc.player);
			rocket.posY = rocket.posY + 10;
		}else{
			rocket = null;
		}
		firework.setInventorySlotContents(0, rocketStack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		// this.drawGuiContainerForegroundLayer(mouseX, mouseY);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.disableBlend();
		this.fontRenderer.drawString(I18n.format("This GUI is work in progress. Currently, it only previews your firework. More features will be added later.", new Object[0]), 8, 6, 0xffffff);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		// Look upwards to see a preview of the current fireworks
		if (this.mc.player.rotationPitch > -90) {
			this.mc.player.rotationPitch -= 0.7;
		}

		// Preview the fireworks once every 2.5 seconds
		if (rocket != null) {
			if (tick >= 500) {
				rocket.handleStatusUpdate((byte) 17);
				tick = 0;
			} else {
				tick++;
			}
		}
	}

	@Override
	public void onGuiClosed() {
		// Changes theplayer's view back to what it was before the gui was
		// opened
		this.mc.player.rotationPitch = initialPitch;

		super.onGuiClosed();

		if (this.mc.player != null && this.mc.player.inventory != null) {
			this.mc.player.inventoryContainer.removeListener(this.listener);
		}

	}

	/**
	 * Called when the mouse is clicked over a slot or outside the gui.
	 */
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		boolean isShift = type == ClickType.QUICK_MOVE;
		type = slotId == -999 && type == ClickType.PICKUP ? ClickType.THROW : type;
		// Drop item that mouse is holding, if no slot
		if (slotIn == null && type != ClickType.QUICK_CRAFT) {
			InventoryPlayer inventoryplayer1 = this.mc.player.inventory;

			if (inventoryplayer1.getItemStack() != null && inventoryplayer1.getItemStack() != ItemStack.EMPTY) {
				if (mouseButton == 0) {
					this.mc.player.dropItem(inventoryplayer1.getItemStack(), true);
					this.mc.playerController.sendPacketDropItem(inventoryplayer1.getItemStack());
					inventoryplayer1.setItemStack(ItemStack.EMPTY);
				}

				if (mouseButton == 1) {
					ItemStack itemstack5 = inventoryplayer1.getItemStack().splitStack(1);
					this.mc.player.dropItem(itemstack5, true);
					this.mc.playerController.sendPacketDropItem(itemstack5);

					if (inventoryplayer1.getItemStack().getCount() == 0) {
						inventoryplayer1.setItemStack(ItemStack.EMPTY);
					}
				}
			}
		}

		// In banner result inventory
		else if (type != ClickType.QUICK_CRAFT && slotIn.inventory == firework) {
			InventoryPlayer inventoryplayer = this.mc.player.inventory;
			ItemStack handStack = inventoryplayer.getItemStack();
			ItemStack slotStack = slotIn.getStack();

			if (slotIn.getSlotIndex() == 0) {
				// Pressing a number 1-9
				if (type == ClickType.SWAP) {
					if (slotStack != null && slotStack != ItemStack.EMPTY && mouseButton >= 0 && mouseButton < 9) {
						this.mc.playerController.sendSlotPacket(slotStack, mouseButton);
						this.mc.player.inventory.setInventorySlotContents(mouseButton, slotStack);
						setCurrentRocket(ItemStack.EMPTY);
						this.mc.player.inventoryContainer.detectAndSendChanges();
					}

					return;
				}

				// Mouse wheel
				if (type == ClickType.CLONE) {
					if ((inventoryplayer.getItemStack() == null || inventoryplayer.getItemStack() == ItemStack.EMPTY)
							&& slotIn.getHasStack()) {
						ItemStack itemstack6 = slotIn.getStack().copy();
						itemstack6.setCount(itemstack6.getMaxStackSize());
						inventoryplayer.setItemStack(itemstack6);
					}

					return;
				}

				// Pressing Q - Mouse button is 1 if Ctrl is down
				if (type == ClickType.THROW) {
					if (slotStack != null && slotStack != ItemStack.EMPTY) {
						this.mc.player.dropItem(slotStack, true);
						this.mc.playerController.sendPacketDropItem(slotStack);
					}
					return;
				}

				if (slotStack != null && slotStack != ItemStack.EMPTY
						&& (handStack == null || handStack == ItemStack.EMPTY)) {
					inventoryplayer.setItemStack(slotStack);
					handStack = inventoryplayer.getItemStack();

					if (isShift) {
						handStack.setCount(handStack.getMaxStackSize());
					}

					setCurrentRocket(ItemStack.EMPTY);
					return;
				} else if ((handStack != null && handStack != ItemStack.EMPTY)
						&& (handStack.getItem() == Items.FIREWORKS)) {
					ItemStack old2 = (slotStack != null && slotStack != ItemStack.EMPTY) ? slotStack.copy()
							: ItemStack.EMPTY;
					setCurrentRocket(handStack);
					inventoryplayer.setItemStack(old2);
					return;
				}
			}
			return;
		} else {
			// Taken from "if tab == inventory"
			if (type == ClickType.THROW && slotIn != null && slotIn.getHasStack()) {
				ItemStack itemstack = slotIn.decrStackSize(mouseButton == 0 ? 1 : slotIn.getStack().getMaxStackSize());
				this.mc.player.dropItem(itemstack, true);
				this.mc.playerController.sendPacketDropItem(itemstack);
			} else if (type == ClickType.THROW && this.mc.player.inventory.getItemStack() != null
					&& this.mc.player.inventory.getItemStack() != ItemStack.EMPTY) {
				this.mc.player.dropItem(this.mc.player.inventory.getItemStack(), true);
				this.mc.playerController.sendPacketDropItem(this.mc.player.inventory.getItemStack());
				this.mc.player.inventory.setItemStack(ItemStack.EMPTY);
			} else {
				if (isShift) {
					type = ClickType.PICKUP;
				}
				this.mc.player.inventoryContainer.slotClick((slotIn == null ? slotId : slotIn.slotNumber) + 35,
						mouseButton, type, this.mc.player);
				this.mc.player.inventoryContainer.detectAndSendChanges();
			}
			// End of == inventory
		}
	}

	@SideOnly(Side.CLIENT)
	static class ContainerFirework extends Container {
		public List<ItemStack> itemList = Lists.<ItemStack> newArrayList();

		public ContainerFirework(EntityPlayer player) {
			InventoryPlayer inventoryplayer = player.inventory;

			// Crafted Banner and Dye slot
			this.addSlotToContainer(new Slot(GuiFireworksMaker.firework, 0, 73, 3));

			// Player's actionbar
			for (int i = 0; i < 9; ++i) {
				this.addSlotToContainer(new Slot(inventoryplayer, i, i * 18 + 1, 23));
			}

		}

		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return true;
		}


		/**
		 * Take a stack from the specified inventory slot.
		 */
		@Override
		public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
			if (index >= this.inventorySlots.size() - 9 && index < this.inventorySlots.size()) {
				Slot slot = (Slot) this.inventorySlots.get(index);

				if (slot != null && slot.getHasStack()) {
					slot.putStack(ItemStack.EMPTY);
				}
			}

			return ItemStack.EMPTY;
		}

		/**
		 * Called to determine if the current slot is valid for the stack
		 * merging (double-click) code. The stack passed in is null for the
		 * initial slot that was double-clicked.
		 */
		@Override
		public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
			return slotIn.yPos > 90;
		}

		/**
		 * Returns true if the player can "drag-spilt" items into this slot,.
		 * returns true by default. Called to check if the slot can be added to
		 * a list of Slots to split the held ItemStack across.
		 */
		@Override
		public boolean canDragIntoSlot(Slot slotIn) {
			return slotIn.inventory instanceof InventoryPlayer || slotIn.yPos > 90 && slotIn.xPos <= 162;
		}
	}
}
