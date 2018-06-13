package ruukas.infinity.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.gui.action.GuiInfinityButton;
import ruukas.infinity.nbt.itemstack.tag.InfinityBookTags;

@SideOnly( Side.CLIENT )
public class GuiBook extends GuiInfinity
{    
    private GuiInfinityButton generationButton, unsignButton;
    
    public GuiBook(GuiScreen lastScreen, ItemStack stack) {
        super( lastScreen, stack );
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        
        int buttons = 0;
        generationButton = addButton( new GuiInfinityButton( 100 + buttons, (this.width / 2) - 75, 50 + (30 * buttons++), 150, 20, I18n.format( "gui.book.generation" ) ) );
        unsignButton = addButton( new GuiInfinityButton( 100 + buttons, (this.width / 2) - 75, 50 + (30 * buttons++), 150, 20, I18n.format( "gui.book.unsign" ) ) );
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        if ( button.id == generationButton.id )
        {
            new InfinityBookTags( getItemStack() ).addGeneration();
        }
        
        if ( button.id == unsignButton.id )
        {
            ItemStack quill = new ItemStack( Items.WRITABLE_BOOK );
            
            if(getItemStack().hasTagCompound()){
                quill.setTagCompound( getItemStack().getTagCompound() );
                new InfinityBookTags( quill ).unsign();
                stack = quill;
                back();
            }
        }
        
        else {
            super.actionPerformed( button );
        }
    }
    
    
    @Override
    protected String getNameUnlocalized()
    {
        return "book";
    }
}
