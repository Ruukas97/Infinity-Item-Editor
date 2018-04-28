package ruukas.infinity.gui.action;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.gui.HelperGui;

@SideOnly( Side.CLIENT )
public class GuiNumberField extends Gui
{
    private final int id;
    private final FontRenderer fontRenderer;
    public int x;
    public int y;
    /** The width of this text field. */
    public int width;
    public int height;
    
    public char[] digits;
    
    public int maxValue = Integer.MAX_VALUE;
    public int minValue = Integer.MIN_VALUE;
    
    private char[] allowed = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private boolean isNegative;
    
    private int cursorCounter;
    
    private boolean enableBackgroundDrawing = true;
    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    private boolean canLoseFocus = true;
    /**
     * If this value is true along with isEnabled, keyTyped will process the keys.
     */
    private boolean isFocused;
    /**
     * If this value is true along with isFocused, keyTyped will process the keys.
     */
    private boolean isEnabled = true;
    
    private int cursorPosition;
    
    private int enabledColor = HelperGui.MAIN_PURPLE;
    private int cursorColor = HelperGui.MAIN_BLUE;
    private int disabledColor = 7368816;
    
    /** True if this textbox is visible */
    private boolean visible = true;
    
    public Runnable action;
    
    public GuiNumberField(int componentId, FontRenderer fontrendererObj, int x, int y, int width, int height, int digits) {
        this.id = componentId;
        this.fontRenderer = fontrendererObj;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        this.digits = new char[ digits ];
        
        resetDigits();
    }
    
    public String getValueAsString()
    {
        for ( int i = 0 ; i < digits.length ; i++ )
        {
        }
        return (isNegative ? '-' : "") + new String( digits );
    }
    
    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter()
    {
        this.cursorCounter++;
    }
    
    /**
     * Sets the text of the textbox, and moves the cursor to the end.
     */
    public void setDigit( int i, char c )
    {
        if ( isAllowed( c ) )
        {
            digits[i] = c;
        }
        
        if ( getIntValue() > maxValue )
        {
            String maxStr = (maxValue + "");
            int diff = digits.length - maxStr.length();
            char mC = maxStr.charAt( this.cursorPosition - diff );
            
            if ( c == mC )
            {
                this.setValue( maxValue );
            }
            else
            {
                this.setDigit( mC );
            }
        }
        
        else if ( getIntValue() < minValue )
        {
            String minStr = (minValue + "");
            int diff = digits.length - minStr.length();
            char mC = minStr.charAt( this.cursorPosition - diff );
            
            if ( c == mC )
            {
                this.setValue( minValue );
            }
            else
            {
                this.setDigit( mC );
            }
        }
 
        
        if ( action != null )
        {
            action.run();
        }
    }
    
    /**
     * Sets the text of the textbox, and moves the cursor to the end.
     */
    public void setDigit( char c )
    {
        this.setDigit( cursorPosition, c );
    }
    
    public void setValue( int value )
    {
        boolean neg = value < 0;
        String s = ("" + value).substring( neg ? 1 : 0 );
        
        int diff = this.getValueAsString().length() - s.length();
        
        if ( diff < 0 )
        {
            return;
        }
        
        resetDigits();
        
        for ( int i = 0 ; i < s.length() ; i++ )
        {
            this.digits[diff + i] = s.charAt( i );
        }
    }
    
    public void resetDigits()
    {
        for ( int i = 0 ; i < digits.length ; i++ )
        {
            this.digits[i] = '0';
        }
    }
    
    /**
     * Returns the contents of the textbox
     */
    public int getIntValue()
    {
        return Integer.valueOf( this.getValueAsString() );
    }
    
    public boolean isAllowed( char c )
    {
        for ( char ch : allowed )
        {
            if ( c == ch )
            {
                return true;
            }
        }
        return false;
    }
    
    public int getId()
    {
        return this.id;
    }
    
    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursor( boolean right )
    {
        this.setCursorPosition( this.cursorPosition + (right ? 1 : -1) );
    }
    
    /**
     * Sets the current position of the cursor.
     */
    public void setCursorPosition( int pos )
    {
        this.cursorPosition = pos;
        int i = digits.length - 1;
        this.cursorPosition = MathHelper.clamp( this.cursorPosition, 0, i );
    }
    
    /**
     * Moves the cursor to the very start of this text box.
     */
    public void setCursorPositionZero()
    {
        this.setCursorPosition( 0 );
    }
    
    /**
     * Moves the cursor to the very end of this text box.
     */
    public void setCursorPositionEnd()
    {
        this.setCursorPosition( digits.length - 1 );
    }
    
    /**
     * Call this method from your GuiScreen to process the keys into the textbox
     */
    public boolean textboxKeyTyped( char typedChar, int keyCode )
    {
        if ( !this.isFocused )
        {
            return false;
        }
        else if ( GuiScreen.isKeyComboCtrlC( keyCode ) )
        {
            GuiScreen.setClipboardString( String.valueOf( digits ) );
            return true;
        }
        else if ( GuiScreen.isKeyComboCtrlV( keyCode ) )
        {
            if ( this.isEnabled )
            {
                // TODO pasting
            }
            
            return true;
        }
        else if ( GuiScreen.isKeyComboCtrlX( keyCode ) )
        {
            GuiScreen.setClipboardString( String.valueOf( digits ) );
            
            if ( this.isEnabled )
            {
                resetDigits();
            }
            
            return true;
        }
        else
        {
            switch ( keyCode )
            {
                case 14:
                    
                    if ( this.isEnabled )
                    {
                        setDigit( '0' );
                    }
                    
                    return true;
                case 199:
                    
                    setCursorPositionZero();
                    
                    return true;
                case 203:
                    
                    this.moveCursor( false );
                    
                    return true;
                case 205:
                    
                    this.moveCursor( true );
                    
                    return true;
                case 207:
                    
                    this.setCursorPositionEnd();
                    
                    return true;
                case 211:
                    
                    if ( this.isEnabled )
                    {
                        setDigit( '0' );
                    }
                    
                    return true;
                default:
                    if ( this.isEnabled )
                    {
                        if ( typedChar == '-' && minValue < 0 )
                        {
                            isNegative = true;
                        }
                        else if ( typedChar == '+' && maxValue > -1 )
                        {
                            isNegative = false;
                        }
                        else if ( isAllowed( typedChar ) )
                        {
                            setDigit( typedChar );
                            moveCursor( true );
                        }
                        
                        return true;
                    }
                    return false;
            }
        }
    }
    
    /**
     * Called when mouse is clicked, regardless as to whether it is over this button or not.
     */
    public boolean mouseClicked( int mouseX, int mouseY, int mouseButton )
    {
        boolean flag = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
        
        if ( this.canLoseFocus )
        {
            this.setFocused( flag );
        }
        
        if ( this.isFocused && flag && mouseButton == 0 )
        {
            int i = mouseX - this.x;
            
            if ( this.enableBackgroundDrawing )
            {
                i -= 4;
            }
            
            String s = this.fontRenderer.trimStringToWidth( this.getValueAsString(), this.getWidth() );
            this.setCursorPosition( this.fontRenderer.trimStringToWidth( s, i ).length() );
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Draws the textbox
     */
    public void drawTextBox()
    {
        if ( this.getVisible() )
        {
            int color = this.isEnabled ? enabledColor : disabledColor;
            
            if ( this.getEnableBackgroundDrawing() )
            {
                drawRect( this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, color );
                drawRect( this.x, this.y, this.x + this.width, this.y + this.height, HelperGui.ALT_PURPLE );
            }
            
            int cursorPos = this.cursorPosition;
            String string = this.fontRenderer.trimStringToWidth( this.getValueAsString(), this.getWidth() );
            boolean cursorFine = cursorPos >= 0 && cursorPos <= string.length();
            boolean displayCursor = this.isFocused && this.cursorCounter / 6 % 2 == 0 && cursorFine;
            int textX = this.enableBackgroundDrawing ? this.x + 4 : this.x;
            int textY = this.enableBackgroundDrawing ? this.y + (this.height - 8) / 2 : this.y;
            int halfX = textX;
            
            if ( !string.isEmpty() )
            {
                String halfString = cursorFine ? string.substring( 0, cursorPos ) : string;
                halfX = this.fontRenderer.drawStringWithShadow( halfString, (float) textX, (float) textY, color ) - 1;
            }
            
            int cursorX = halfX;
            
            if ( !cursorFine )
            {
                cursorX = cursorPos > 0 ? textX + this.width : textX;
            }
            
            if ( !string.isEmpty() && cursorFine && cursorPos < string.length() )
            {
                halfX = this.fontRenderer.drawStringWithShadow( string.substring( cursorPos ), (float) halfX, (float) textY, color );
            }
            
            if ( displayCursor )
            {
                this.fontRenderer.drawStringWithShadow( "_", (float) cursorX, (float) textY, cursorColor );
            }
        }
    }
    
    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition()
    {
        return this.cursorPosition;
    }
    
    /**
     * Gets whether the background and outline of this text box should be drawn (true if so).
     */
    public boolean getEnableBackgroundDrawing()
    {
        return this.enableBackgroundDrawing;
    }
    
    /**
     * Sets whether or not the background and outline of this text box should be drawn.
     */
    public void setEnableBackgroundDrawing( boolean enableBackgroundDrawingIn )
    {
        this.enableBackgroundDrawing = enableBackgroundDrawingIn;
    }
    
    /**
     * Sets the color to use when drawing this text box's text. A different color is used if this text box is disabled.
     */
    public void setTextColor( int color )
    {
        this.enabledColor = color;
    }
    
    /**
     * Sets the color to use for text in this text box when this text box is disabled.
     */
    public void setDisabledTextColour( int color )
    {
        this.disabledColor = color;
    }
    
    /**
     * Sets focus to this gui element
     */
    public void setFocused( boolean isFocusedIn )
    {
        if ( isFocusedIn && !this.isFocused )
        {
            this.cursorCounter = 0;
        }
        
        this.isFocused = isFocusedIn;
        
        if ( Minecraft.getMinecraft().currentScreen != null )
        {
            Minecraft.getMinecraft().currentScreen.setFocused( isFocusedIn );
        }
    }
    
    /**
     * Getter for the focused field
     */
    public boolean isFocused()
    {
        return this.isFocused;
    }
    
    /**
     * Sets whether this text box is enabled. Disabled text boxes cannot be typed in.
     */
    public void setEnabled( boolean enabled )
    {
        this.isEnabled = enabled;
    }
    
    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    public int getWidth()
    {
        return this.getEnableBackgroundDrawing() ? this.width - 6 : this.width;
    }
    
    /**
     * Sets whether this text box loses focus when something other than it is clicked.
     */
    public void setCanLoseFocus( boolean canLoseFocusIn )
    {
        this.canLoseFocus = canLoseFocusIn;
    }
    
    /**
     * returns true if this textbox is visible
     */
    public boolean getVisible()
    {
        return this.visible;
    }
    
    /**
     * Sets whether or not this textbox is visible
     */
    public void setVisible( boolean isVisible )
    {
        this.visible = isVisible;
    }
}