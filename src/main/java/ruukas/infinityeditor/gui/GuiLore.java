package ruukas.infinityeditor.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import ruukas.infinityeditor.gui.action.GuiActionLoreField;
import ruukas.infinityeditor.gui.action.GuiActionTextField;
import ruukas.infinityeditor.gui.action.GuiInfinityButton;
import ruukas.infinityeditor.nbt.NBTHelper;

public class GuiLore extends GuiInfinity {
    private int offset = 0;
    private boolean draggingScroll = false;
    private ArrayList<GuiLoreLine> lines = new ArrayList<>();

    private GuiInfinityButton[] colorButtons;
    private GuiInfinityButton addLine, copyLore, copyAll, paste, flags, painter;
    // TODO buttons: Copy Lore, Copy Tooltip, Flags, Painter


    public GuiLore(GuiScreen lastScreen, ItemStackHolder stackHolder) {
        super( lastScreen, stackHolder );
    }


    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents( true );
        setRenderStack( true, midX, 35, 1 );

        // COLOR BUTTONS
        TextFormatting[] formats = TextFormatting.values();
        int colorAmount = 2 + formats.length;
        colorButtons = new GuiInfinityButton[colorAmount];
        colorButtons[0] = addButton( new GuiInfinityButton( 130, width - 1 - 13 * ((colorAmount + 2) / 2) + (13 * 1), height - 30, 13, 15, formats[0].toString().substring( 0, 1 ) ) );
        colorButtons[1] = addButton( new GuiInfinityButton( 131, width - 1 - 13 * ((colorAmount + 2) / 2) + (13 * 2), height - 30, 13, 15, TextFormatting.DARK_RED + "%" ) );
        for (int i = 2; i < colorAmount; i++) {
            TextFormatting f = formats[i - 2];
            colorButtons[i] = addButton( new GuiInfinityButton( 130 + i, width - 1 - 13 * ((colorAmount + 2) / 2) + (13 * ((i % (colorAmount / 2)) + 1)), height - 30 + (15 * (i / (colorAmount / 2))), 13, 15, f.toString() + f.toString().substring( 1 ) ) );
        }

        NBTTagList lore = getLore();
        lines.clear();
        for (int i = 0; i < lore.tagCount(); i++) {
            lines.add( new GuiLoreLine( i, fontRenderer, this ) );
        }


        int b = 90;
        int x = 100;
        int padding = 2;
        int margins = 6;
        String text = I18n.format( "gui.lore.addline" );
        int bWidth = fontRenderer.getStringWidth( text ) + margins;
        addLine = addButton( new GuiInfinityButton( b++, x, 10, bWidth, 20, text ) );
        x += bWidth + padding;
        text = I18n.format( "gui.lore.copylore" );
        bWidth = fontRenderer.getStringWidth( text ) + margins;
        copyLore = addButton( new GuiInfinityButton( b++, x, 10, bWidth, 20, text ) );
        x += bWidth + padding;
        text = I18n.format( "gui.lore.copyall" );
        bWidth = fontRenderer.getStringWidth( text ) + margins;
        copyAll = addButton( new GuiInfinityButton( b++, x, 10, bWidth, 20, text ) );
        x += bWidth + padding;
        text = I18n.format( "gui.lore.paste" );
        bWidth = fontRenderer.getStringWidth( text ) + margins;
        paste = addButton( new GuiInfinityButton( b++, x, 10, bWidth, 20, text ) );
        x += bWidth + padding;
        text = I18n.format( "gui.hideflags" );
        bWidth = fontRenderer.getStringWidth( text ) + margins;
        flags = addButton( new GuiInfinityButton( b++, x, 10, bWidth, 20, text ) );
        x += bWidth + padding;
        text = I18n.format( "gui.lorepainter" );
        bWidth = fontRenderer.getStringWidth( text ) + margins;
        painter = addButton( new GuiInfinityButton( b, x, 10, bWidth, 20, text ) );
    }


    public NBTTagList getLore() {
        return NBTHelper.getLoreTagList( getItemStack() );
    }


    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents( false );
    }


    @Override
    public void updateScreen() {
        super.updateScreen();
        for (int i = 0; i < lines.size(); i++) {
            lines.get( i ).field.updateCursorCounter();
        }
    }


    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int delta = Mouse.getEventDWheel();
        int change = delta == 0 ? 0 : (delta > 0 ? -1 : 1);
        if (change != 0)
            offset = MathHelper.clamp( offset + change, 0, Math.max( 0, lines.size() - lineSpaces() ) );
    }


    @Override
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        for (int i = 0; i < lines.size(); i++) {
            lines.get( i ).field.mouseClicked( mouseX, mouseY, mouseButton );
        }

        if (mouseButton == 0) {
            for (int i = offset; i < offset + lineSpaces() && i < lines.size(); i++) {
                GuiLoreLine line = lines.get( i );
                GuiButton[] guiButtons = new GuiButton[] { line.remove, line.up, line.down };
                for (int j = 0; j < guiButtons.length; j++) {
                    GuiButton guibutton = guiButtons[j];
                    if (guibutton.mousePressed( this.mc, mouseX, mouseY )) {
                        net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre( this, guibutton, this.buttonList );
                        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post( event ))
                            break;
                        guibutton = event.getButton();
                        this.selectedButton = guibutton;
                        guibutton.playPressSound( this.mc.getSoundHandler() );
                        boolean res = line.actionPerformed( guibutton );
                        if (this.equals( this.mc.currentScreen ))
                            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post( new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post( this, event.getButton(), this.buttonList ) );
                        if (res) {
                            return;
                        }
                    }
                }
            }
        }
        if (HelperGui.isMouseInRegion( mouseX, mouseY, width - 15, 50, 11, height - 99 )) {
            draggingScroll = true;
        }
    }


    @Override
    protected void mouseReleased( int mouseX, int mouseY, int state ) {
        super.mouseReleased( mouseX, mouseY, state );
        draggingScroll = false;
    }


    @Override
    protected void keyTyped( char typedChar, int keyCode ) throws IOException {
        super.keyTyped( typedChar, keyCode );
        offset = MathHelper.clamp( offset, 0, Math.max( 0, lines.size() - lineSpaces() ) );
        for (int i = offset; i < offset + lineSpaces() && i < lines.size(); i++) {
            GuiLoreLine line = lines.get( i );
            line.field.textboxKeyTyped( typedChar, keyCode );
        }
    }
    
    @Override
    protected void reset() {
        super.reset();
        initGui();
    }


    @Override
    protected void actionPerformed( GuiButton button ) throws IOException {
        super.actionPerformed( button );
        if (button.id == 90) {
            NBTTagList lore = getLore();
            int i = lore.tagCount();
            lore.appendTag( new NBTTagString( "" ) );
            lines.add( new GuiLoreLine( i, fontRenderer, this ) );
            int size = lines.size();
            int space = lineSpaces();
            offset = MathHelper.clamp( size - space, 0, Math.max( 0, size - space ) );
            return;
        }
        if (button.id == 91) {
            String[] lore = new String[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                lore[i] = lines.get( i ).field.getText();
            }
            setClipboardString( String.join( "\n", lore ) );
            return;
        }
        if (button.id == 92) {
            setClipboardString( String.join( "\n", getItemStack().getTooltip( mc.player, ITooltipFlag.TooltipFlags.NORMAL ) ) );
            return;
        }
        if (button.id == 93) {
            String clipboard = getClipboardString();
            String lines[] = clipboard.split( "\\r?\\n" );

            NBTTagList list = getLore();
            for (String l : lines) {
                String s = l.startsWith( Character.toString( (char) 167 ) ) || l.length() <= 1 ? l : TextFormatting.RESET.toString() + TextFormatting.GRAY + l;
                list.appendTag( new NBTTagString( s ) );
            }
            initGui();
            return;
        }
        if (button.id == 94) {
            mc.displayGuiScreen( new GuiHideFlags( this, stackHolder ) );
            return;
        }
        if (button.id == 95) {
            mc.displayGuiScreen( new GuiLorePaint( this, stackHolder ) );
            return;
        }
        if (button.id >= 130 && button.id < 130 + colorButtons.length) {
            for (int i = offset; i < offset + lineSpaces() && i < lines.size(); i++) {
                GuiLoreLine line = lines.get( i );
                GuiActionTextField f = line.field;
                if (f.isFocused()) {
                    if (button.id == 130) {
                        f.setText( f.getText().substring( 0, f.getCursorPosition() ) + TextFormatting.values()[0].toString().substring( 0, 1 ) + f.getText().substring( f.getCursorPosition(), f.getText().length() ) );
                    }

                    else if (button.id == 131) {
                        f.setText( TextFormatting.getTextWithoutFormattingCodes( f.getText() ) );
                    }

                    else {
                        f.setText( f.getText().substring( 0, f.getCursorPosition() ) + TextFormatting.values()[button.id - 132] + f.getText().substring( f.getCursorPosition(), f.getText().length() ) );
                    }

                    return;
                }
            }
        }
        else
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get( i ).actionPerformed( button )) {
                    return;
                }
            }
    }


    public int lineSpaces() {
        return ((height - 70) / 30) - 1;
    }


    @Override
    public void drawScreen( int mouseX, int mouseY, float partialTicks ) {
        super.drawScreen( mouseX, mouseY, partialTicks );
        int space = lineSpaces();
        int size = lines.size();
        offset = MathHelper.clamp( offset, 0, Math.max( 0, size - space ) );
        int y = 25;
        String tooltip = null;
        for (int i = offset; i < offset + space && i < size; i++) {
            GuiLoreLine line = lines.get( i );
            y = 55 + 30 * (line.id - offset);
            line.render( mouseX, mouseY, partialTicks, y );

            if (HelperGui.isMouseInRegion( mouseX, mouseY, line.field.x, line.field.y, line.field.width, line.field.height )) {
                tooltip = line.field.getText().replace( (char) 167, '&' );
            }
        }

        drawHorizontalLine( width - 15, width - 5, 50, 0xFFAAAAAA );
        drawHorizontalLine( width - 15, width - 5, height - 50, 0xFFAAAAAA );
        int scrollHeight = height - 103;
        float covered = (size < space) ? 1 : space / (float) size;
        int coveredHeight = (int) Math.max( 1, scrollHeight * covered );

        if (draggingScroll) {
            // float half = coveredHeight/2;
            float perc = (mouseY - 50) / ((float) (height - 99));
            int max = size - space;
            offset = (int) MathHelper.clamp( Math.round( max * perc ), 0, max );
        }

        float div = size - space;
        float perc = div <= 0f ? 0 : offset / div;
        int scrollY = (int) (52 + ((scrollHeight - coveredHeight) * perc));

        drawRect( width - 14, scrollY, width - 5, (int) (scrollY + coveredHeight), 0xFF666666 );

        // drawString( fontRenderer, "Offset: " + offset + ", Lines: " + size + ",
        // Space: " + space + ", Covered: " + covered + ", Perc: " + perc, 10, 10,
        // 0xFFFFFFFF );
        addLine.y = y + 30;
        copyLore.y = y + 30;
        copyLore.enabled = size > 0;
        copyAll.y = y + 30;
        paste.y = y + 30;
        flags.y = y + 30;
        painter.y = y + 30;

        if (tooltip != null && tooltip.length() > 0) {
            drawHoveringText( tooltip, mouseX, mouseY );
        }
        else {
            List<String> list = getItemStack().getTooltip( mc.player, ITooltipFlag.TooltipFlags.NORMAL );
            String[] array = new String[list.size()];
            list.toArray( array );
            HelperGui.addToolTip( midX - 9, 27, 18, 18, mouseX, mouseY, array );
        }
    }


    @Override
    protected String getNameUnlocalized() {
        return "lore";
    }


    private static class GuiLoreLine {
        private GuiLore gui;
        private int id;
        private GuiActionTextField field;
        private GuiInfinityButton remove, up, down;


        private GuiLoreLine(int id, FontRenderer font, GuiLore gui) {
            this.gui = gui;
            this.id = id;
            field = new GuiActionLoreField( 1000 + id, font, 100, 0, 1000, 20 );
            field.setMaxStringLength( 500 );
            refresh();
            field.action = () -> {
                gui.getLore().set( id, new NBTTagString( field.getText() ) );
            };

            remove = new GuiInfinityButton( 500 + id, gui.width, 0, 20, 20, "\u2715" );
            up = new GuiInfinityButton( 600 + id, 100, 0, 20, 20, "\u2B06" );
            up.enabled = id > 0;
            down = new GuiInfinityButton( 700 + id, 100, 0, 20, 20, "\u2B07" );
        }


        public void refresh() {
            field.setText( gui.getLore().getStringTagAt( id ) );
        }


        public void swap( GuiLoreLine other ) {
            String oS = other.field.getText();
            other.field.setText( field.getText() );
            field.setText( oS );
        }


        public boolean actionPerformed( GuiButton button ) {
            if (button == remove) {
                int size = gui.lines.size();
                gui.lines.remove( size - 1 );
                gui.getLore().removeTag( id );
                gui.initGui();
                return true;
            }
            if (button == up) {
                if (id >= 1)
                    swap( gui.lines.get( id - 1 ) );
                return true;
            }
            if (button == down) {
                if (id + 1 < gui.lines.size())
                    swap( gui.lines.get( id + 1 ) );
                return true;
            }
            return false;
        }


        public void render( int mouseX, int mouseY, float partialTicks, int y ) {
            String l = "Line " + (id + 1);
            gui.drawString( gui.fontRenderer, l, field.x - gui.fontRenderer.getStringWidth( l ) - 10, y + 6, 0xFFFFFFFF );
            field.y = y;
            field.width = gui.width - field.x - 100;
            field.drawTextBox();

            up.x = 3 + field.x + field.width;
            up.y = y;
            up.drawButton( gui.mc, mouseX, mouseY, partialTicks );

            down.enabled = id < gui.lines.size() - 1;
            down.x = 1 + up.x + up.width;
            down.y = y;
            down.drawButton( gui.mc, mouseX, mouseY, partialTicks );

            remove.x = 1 + down.x + down.width;
            remove.y = y;
            remove.drawButton( gui.mc, mouseX, mouseY, partialTicks );
        }
    }
}
