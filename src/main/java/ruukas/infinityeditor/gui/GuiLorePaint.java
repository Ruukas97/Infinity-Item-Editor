package ruukas.infinityeditor.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.gui.action.GuiInfinityButton;
import ruukas.infinityeditor.nbt.NBTHelper;

public class GuiLorePaint extends GuiInfinity {
    private boolean dragging = false, preview = false;
    private int x = 3, y = 3;
    private List<List<LorePixel>> pixelRows;
    private GuiInfinityButton insert, scale, addRow, removeRow, addColumn, removeColumn, previewToggle;
    private final LorePixel currentPixel = new LorePixel();


    public GuiLorePaint(GuiScreen lastScreen, ItemStackHolder stackHolder) {
        super( lastScreen, stackHolder );
        reset();
    }


    public NBTTagList getLore() {
        return NBTHelper.getLoreTagList( getItemStack() );
    }


    @Override
    public void initGui() {
        super.initGui();
        String insertS = I18n.format( "gui.lorepainter.insert" );
        int insertW = fontRenderer.getStringWidth( insertS ) + 5;
        insert = addButton( new GuiInfinityButton( 500, midX - insertW / 2, height - 55, insertW, 20, insertS ) );
        String scaleS = I18n.format( "gui.lorepainter.scale" );
        int scaleW = fontRenderer.getStringWidth( scaleS ) + 5;
        scale = addButton( new GuiInfinityButton( 501, width - scaleW, height - 20, scaleW, 20, scaleS ) );
        addRow = addButton( new GuiInfinityButton( 502, midX - 20, height - 50, 20, 20, "+" ) );
        removeRow = addButton( new GuiInfinityButton( 503, midX, height - 50, 20, 20, "-" ) );
        addColumn = addButton( new GuiInfinityButton( 504, width - 50, midY - 20, 20, 20, "+" ) );
        removeColumn = addButton( new GuiInfinityButton( 505, width - 50, midY, 20, 20, "-" ) );
        String previewS = I18n.format( "gui.lorepainter.preview" );
        previewToggle = addButton( new GuiInfinityButton( 506, 0, height - 20, fontRenderer.getStringWidth( previewS ) + 5, 20, previewS ) );
    }


    @Override
    protected void actionPerformed( GuiButton button ) throws IOException {
        super.actionPerformed( button );

        if (button.id == 500) {
            NBTTagList lore = getLore();
            for (List<LorePixel> row : pixelRows) {
                StringBuilder s = new StringBuilder();
                for (LorePixel pixel : row) {
                    s.append(pixel);
                }
                lore.appendTag( new NBTTagString(s.toString()) );
            }
            return;
        }

        if (button.id == 501) {
            mc.gameSettings.setOptionValue( Options.GUI_SCALE, 1 );
            ScaledResolution scaledresolution = new ScaledResolution( this.mc );
            int j = scaledresolution.getScaledWidth();
            int k = scaledresolution.getScaledHeight();
            this.setWorldAndResolution( this.mc, j, k );
            return;
        }

        if (button.id == 502) {
            List<LorePixel> row = new ArrayList<>();
            for (int i = 0; i < x; i++) {
                row.add( currentPixel.copy() );
            }
            pixelRows.add( row );
            y++;
            return;
        }

        if (button.id == 503) {
            if (y <= 1) {
                return;
            }
            pixelRows.remove( y - 1 );
            y--;
            return;
        }

        if (button.id == 504) {
            for (List<LorePixel> row : pixelRows) {
                row.add( currentPixel.copy() );
            }
            x++;
            return;
        }

        if (button.id == 505) {
            if (x <= 1) {
                return;
            }
            for (List<LorePixel> row : pixelRows) {
                row.remove( x - 1 );
            }
            x--;
            return;
        }

        if (button.id == 506) {
            preview = !preview;
        }
    }


    @Override
    protected void reset() {
        pixelRows = new ArrayList<>();
        for (int i = 0; i < y; i++) {
            List<LorePixel> row = new ArrayList<>();
            for (int j = 0; j < x; j++) {
                row.add( currentPixel.copy() );
            }
            pixelRows.add( row );
        }
    }


    public int getSizeX() {
        return 9 * x;
    }


    public int getSizeY() {
        return 9 * y;
    }


    @Override
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        dragging = true;
    }


    @Override
    protected void mouseReleased( int mouseX, int mouseY, int state ) {
        super.mouseReleased( mouseX, mouseY, state );
        dragging = false;
    }


    @Override
    protected void keyTyped( char typedChar, int keyCode ) throws IOException {
        super.keyTyped( typedChar, keyCode );
    }


    @Override
    public void drawScreen( int mouseX, int mouseY, float partialTicks ) {
        super.drawScreen( mouseX, mouseY, partialTicks );

        drawCenteredString( fontRenderer, x + "x" + y, midX, (midY - getSizeY() / 2 - 15), InfinityConfig.MAIN_COLOR );

        int yPos = 0;


        for (List<LorePixel> row : pixelRows) {
            StringBuilder s = new StringBuilder();
            for (LorePixel pixel : row) {
                s.append(pixel);
            }
            fontRenderer.drawString(s.toString(), (float) (midX - getSizeX() / 2), (float) (midY - getSizeY() / 2 + yPos), 0xFFFFFFFF, false );

            yPos += 9;
        }

        yPos = midY - getSizeY() / 2 + yPos + 9;

        addRow.y = yPos;
        removeRow.y = yPos;
        removeRow.enabled = y > 1;

        int xPos = midX - getSizeX() / 2 + getSizeX() + 9;
        addColumn.x = xPos;
        removeColumn.x = xPos;
        removeColumn.enabled = x > 1;

        StringBuilder symbols = new StringBuilder();
        for (LoreSymbol symbol : LoreSymbol.values()) {
            if (symbol != LoreSymbol.fullspace)
                symbols.append(new LorePixel(currentPixel.color, symbol));
            else
                symbols.append(TextFormatting.ITALIC).append(TextFormatting.BOLD).append("E");
        }
        drawString( fontRenderer, symbols.toString(), 0, 0, 0xFFFFFFFF );
        drawString( fontRenderer, TextFormatting.fromColorIndex( currentPixel.color.getDyeDamage() ) + currentPixel.symbol.getTranslatedName(), 2, 10, 0xFFFFFFFF );

        StringBuilder colors = new StringBuilder();
        for (EnumDyeColor color : EnumDyeColor.values()) {
            colors.append(new LorePixel(color, currentPixel.symbol));
        }
        int colorX = width - fontRenderer.getStringWidth(colors.toString());
        drawString( fontRenderer, colors.toString(), colorX, 0, 0xFFFFFFFF );
        String colorS = TextFormatting.fromColorIndex( currentPixel.color.getDyeDamage() ) + TextFormatting.fromColorIndex( currentPixel.color.getDyeDamage() ).getFriendlyName();
        drawString( fontRenderer, colorS, width - fontRenderer.getStringWidth( colorS ) - 2, 10, 0xFFFFFFFF );


        if (dragging && HelperGui.isMouseInRegion( mouseX, mouseY, midX - getSizeX() / 2, midY - getSizeY() / 2, getSizeX() - 1, getSizeY() - 1 )) {
            int xi = (mouseX - (midX - getSizeX() / 2)) / 9;
            int yi = (mouseY - (midY - getSizeY() / 2)) / 9;
            pixelRows.get( yi ).set( xi, currentPixel.copy() );
        }
        else if (dragging && HelperGui.isMouseInRegion( mouseX, mouseY, 0, 0, LoreSymbol.values().length * 9, 9 )) {
            int xi = mouseX / 9;
            if (xi < LoreSymbol.values().length) {
                currentPixel.symbol = LoreSymbol.values()[xi];
            }
        }
        else if (dragging && HelperGui.isMouseInRegion( mouseX, mouseY, colorX, 0, EnumDyeColor.values().length * 9, 9 )) {
            int xi = (mouseX - colorX) / 9;
            if (xi < EnumDyeColor.values().length) {
                currentPixel.color = EnumDyeColor.values()[xi];
            }
        }

        HelperGui.addTooltip( scale, mouseX, mouseY, String.valueOf( mc.gameSettings.guiScale ) );

        if (preview || HelperGui.isMouseInRegion( mouseX, mouseY, previewToggle.x, previewToggle.y, previewToggle.width, previewToggle.height )) {
            List<String> lines = new ArrayList<>();
            lines.add( title );
            for (List<LorePixel> row : pixelRows) {
                StringBuilder s = new StringBuilder();
                for (LorePixel pixel : row) {
                    s.append(pixel);
                }
                lines.add(s.toString());
            }
            drawHoveringText( lines, mouseX, mouseY );
        }
    }


    @Override
    protected String getNameUnlocalized() {
        return "lorepainter";
    }


    private static class LorePixel {
        private EnumDyeColor color;
        private LoreSymbol symbol;


        private LorePixel(EnumDyeColor color, LoreSymbol symbol) {
            this.color = color;
            this.symbol = symbol;
        }


        private LorePixel() {
            this( EnumDyeColor.WHITE, LoreSymbol.fullblock );
        }


        public TextFormatting getFormat() {
            return TextFormatting.fromColorIndex( color.getDyeDamage() );
        }


        protected LorePixel copy() {
            return new LorePixel( color, symbol );
        }


        @Override
        public String toString() {
            if (symbol.isWhitespace()) {
                return symbol.toString();
            }
            return getFormat().toString() + symbol;
        }
    }


    private static enum LoreSymbol {
        // @formatter:off
        fullblock( "fullblock", "\u2588" ),
        mediumshade( "mediumshade", "\u2592" ),
        darkshade( "darkshade", "\u2593" ),
        fullspace( "fullspace", TextFormatting.BOLD.toString() + ' ' + TextFormatting.RESET.toString() + ' ', true );
        // @formatter:on
 

        private String symbol;
        private String name;
        private boolean whitespace;


        LoreSymbol(String name, String symbol) {
            this( name, symbol, false );
        }


        LoreSymbol(String name, String symbol, boolean whitespace) {
            this.symbol = symbol;
            this.name = name;
            this.whitespace = whitespace;
        }


        public String getSymbol() {
            return symbol;
        }


        public String getName() {
            return name;
        }


        public boolean isWhitespace() {
            return whitespace;
        }


        public String getTranslatedName() {
            return I18n.format( "gui.lorepainter.symbol." + name );
        }


        @Override
        public String toString() {
            return symbol;
        }
    }
}
