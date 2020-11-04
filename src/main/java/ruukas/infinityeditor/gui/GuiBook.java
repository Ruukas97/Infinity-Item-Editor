package ruukas.infinityeditor.gui;

import java.io.IOException;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.gui.action.GuiActionTextField;
import ruukas.infinityeditor.gui.action.GuiInfinityButton;
import ruukas.infinityeditor.nbt.itemstack.tag.InfinityBookTags;
import ruukas.infinityeditor.nbt.itemstack.tag.InfinityBookTags.SignedData;

@SideOnly( Side.CLIENT )
public class GuiBook extends GuiInfinity
{
    private GuiInfinityButton generationButton, resolvedButton, unsignButton;// , clearTitleButton, clearAuthorButton;
    
    private GuiActionTextField titleField, authorField;
    
    @Nullable
    private SignedData data;
    
    public GuiBook(GuiScreen lastScreen, ItemStackHolder stackHolder) {
        super( lastScreen, stackHolder );
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        setRenderStack( true, midX, 40, 1 );
        renderTooltip = true;
        renderTag = true;
        
        Keyboard.enableRepeatEvents( true );
        
        InfinityBookTags bookTags = new InfinityBookTags( getItemStack() );
        boolean written = getItemStack().getItem() == Items.WRITTEN_BOOK;
        
        int fields = 0;
        titleField = new GuiActionTextField( 200 + fields, fontRenderer, midX, 55 + (30 * fields++), 75, 20 );
        titleField.setMaxStringLength( 100 );
        titleField.setText( bookTags.getTitle() );
        titleField.setEnabled( written );
        titleField.action = () -> {
            bookTags.setTitle( titleField.getText() );
        };
        
        authorField = new GuiActionTextField( 200 + fields, fontRenderer, midX, 55 + (30 * fields++), 75, 20 );
        authorField.setMaxStringLength( 100 );
        authorField.setText( bookTags.getAuthor() );
        authorField.setEnabled( written );
        authorField.action = () -> {
            bookTags.setAuthor( authorField.getText() );
        };
        
        int buttons = 0;
        generationButton = addButton( new GuiInfinityButton( 100 + buttons, midX - 75, 55 + (30 * fields) + (30 * buttons++), 150, 20, I18n.format( "gui.book.generation" ) ) );
        generationButton.enabled = written;
        resolvedButton = addButton( new GuiInfinityButton( 100 + buttons, midX - 75, 55 + (30 * fields) + (30 * buttons++), 150, 20, bookTags.getResolved() ? I18n.format( "gui.book.resolved" ) : I18n.format( "gui.book.unresolved" ) ) );
        resolvedButton.enabled = written;
        
        unsignButton = addButton( new GuiInfinityButton( 100 + buttons, midX - 75, 55 + (30 * fields) + (30 * buttons++), 150, 20, data != null ? I18n.format( "gui.book.resign" ) : I18n.format( "gui.book.unsign" ) ) );
        
        // addButton( new GuiInfinityButton( 100 + buttons, midX - 75, 55 + (30 * buttons++), 150, 20, I18n.format( "gui.book.test" ) ) );
    }
    
    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents( false );
    }
    
    @Override
    public void updateScreen()
    {
        super.updateScreen();
        
        titleField.updateCursorCounter();
        authorField.updateCursorCounter();
    }
    
    @Override
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        super.keyTyped( typedChar, keyCode );
        
        titleField.textboxKeyTyped( typedChar, keyCode );
        authorField.textboxKeyTyped( typedChar, keyCode );
    }
    
    @Override
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        titleField.mouseClicked( mouseX, mouseY, mouseButton );
        authorField.mouseClicked( mouseX, mouseY, mouseButton );
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        if ( button.id == generationButton.id )
        {
            new InfinityBookTags( getItemStack() ).addGeneration();
        }
        
        else if ( button.id == unsignButton.id )
        {
            if ( data == null )
            {
                ItemStack quill = new ItemStack( Items.WRITABLE_BOOK );
                
                if ( getItemStack().hasTagCompound() )
                {
                    quill.setTagCompound( getItemStack().getTagCompound().copy() );
                    data = new InfinityBookTags( quill ).unsign();
                    stackHolder.setStack( quill );
                }
            }
            else
            {
                ItemStack book = new ItemStack( Items.WRITTEN_BOOK );
                
                new InfinityBookTags( book ).resign( data );
                
                data = null;
                stackHolder.setStack( book );
                ;
            }
            
            initGui();
        }
        
        else if ( button.id == resolvedButton.id )
        {
            new InfinityBookTags( getItemStack() ).toggleResolved();
            initGui();
        }
        
        /*
         * else if(button.id == 102){ ItemStack quill = new ItemStack( Items.WRITTEN_BOOK );
         * 
         * new InfinityBookTags( quill ).setTitle( "YT" ).setAuthor( "Hey" ).setResolved( true );
         * 
         * NBTTagList pages = new NBTTagList();
         * 
         * ITextComponent comp = new TextComponentString( "test" ); comp.getStyle().setClickEvent( new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.google.com"));
         * 
         * pages.appendTag( new NBTTagString(ITextComponent.Serializer.componentToJson( comp )));
         * 
         * quill.getTagCompound().setTag( "pages", pages );
         * 
         * stack = quill;
         * 
         * if(lastScreen instanceof GuiInfinity){ ((GuiInfinity)lastScreen).stack = stack; } }
         */
        
        else
        {
            super.actionPerformed( button );
        }
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "book";
    }
    
    @Override
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        titleField.drawTextBox();
        String titleStr = I18n.format( "gui.book.title" );
        drawString( fontRenderer, titleStr, titleField.x - fontRenderer.getStringWidth( titleStr ) - 5, titleField.y + 6, InfinityConfig.MAIN_COLOR );
        
        authorField.drawTextBox();
        String authorStr = I18n.format( "gui.book.author" );
        drawString( fontRenderer, authorStr, authorField.x - fontRenderer.getStringWidth( authorStr ) - 5, authorField.y + 6, InfinityConfig.MAIN_COLOR );
        
        HelperGui.addTooltipTranslated( resolvedButton, mouseX, mouseY, I18n.format( "gui.book.resolved.tooltip" ) );
    }
}
