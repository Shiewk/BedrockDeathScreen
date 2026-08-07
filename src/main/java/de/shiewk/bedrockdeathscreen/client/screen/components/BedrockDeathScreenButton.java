package de.shiewk.bedrockdeathscreen.client.screen.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.function.IntSupplier;

public class BedrockDeathScreenButton extends AbstractWidget {

    public static final int colorBlack = 0x000000;
    public final Font font;
    public final int colorPrimary;
    public final int colorSecondary;
    public final int colorBorder;
    public final int colorHover;
    public final int colorText;
    public final int colorPrimaryPressed;
    public final int colorBorderPressed;
    public final boolean textShadow;
    public final IntSupplier opacitySupplier;
    public final Runnable clickAction;

    private boolean pressed = false;
    private boolean clicked = false;

    public BedrockDeathScreenButton(
            int x, int y, int width, int height,
            Component message, Font font,
            int colorPrimary, int colorSecondary,
            int colorBorder, int colorHover,
            int colorText, int colorPrimaryPressed, int colorBorderPressed, boolean textShadow,
            IntSupplier opacitySupplier, Runnable clickAction
    ) {
        super(x, y, width, height, message);
        this.font = font;
        this.colorPrimary = colorPrimary;
        this.colorSecondary = colorSecondary;
        this.colorBorder = colorBorder;
        this.colorHover = colorHover;
        this.colorText = colorText;
        this.colorPrimaryPressed = colorPrimaryPressed;
        this.colorBorderPressed = colorBorderPressed;
        this.textShadow = textShadow;
        this.opacitySupplier = opacitySupplier;
        this.clickAction = clickAction;
    }

    public int changeColorOpacity(int color, int opacity){
        return (color & 0x00ffffff) | (opacity << 24);
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        final boolean mouseHover = isHovered();

        if (mouseHover) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }

        final int opacity = getOpacity();
        if (opacity < 4) return;
        final int startX = getX();
        final int startY = getY();
        final int endX = getX() + getWidth();
        final int endY = getY() + getHeight();

        boolean shouldAppearPressed = pressed || clicked;
        if (shouldAppearPressed){
            graphics.fill(startX+2, startY+4, endX-2, endY+2, changeColorOpacity(colorPrimaryPressed, opacity));
            graphics.outline(startX+1, startY+3, (endX-1) - (startX+1), (endY+1) - (startY+1), changeColorOpacity(colorBorderPressed, opacity));
            graphics.outline(startX, startY+2, endX - startX, (endY+2) - startY, changeColorOpacity(colorBlack, opacity));
        } else {
            graphics.fill(startX+2, startY+2, endX-2, endY-1, (mouseHover ? changeColorOpacity(colorHover, opacity) : changeColorOpacity(colorPrimary, opacity)));
            graphics.outline(startX+1, startY+1, (endX-1) - (startX+1), (endY) - (startY+1), changeColorOpacity(colorBorder, opacity));
            graphics.fill(startX+1, endY, endX-1, endY+3, changeColorOpacity(colorSecondary, opacity));
            graphics.outline(startX, startY, endX - startX, (endY+4) - startY, changeColorOpacity(colorBlack, opacity));
        }

        if (textShadow){
            graphics.centeredText(
                    font,
                    getMessage(),
                    startX+getWidth()/2,
                    endY - getHeight()/2 - (shouldAppearPressed ? 1 : 3),
                    changeColorOpacity(colorText, opacity)
            );
        } else {
            graphics.text(
                    font,
                    getMessage(),
                    startX + (getWidth()/2) - (font.width(getMessage())/2),
                    endY - getHeight()/2 - (shouldAppearPressed ? 1 : 3),
                    changeColorOpacity(colorText, opacity),
                    false
            );
        }
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent click, boolean doubled) {
        pressed = true;
    }

    @Override
    public void onRelease(@NonNull MouseButtonEvent click) {
        if (pressed && isHovered) {
            clicked = true;
            clickAction.run();
        }
        pressed = false;
    }

    @Override
    public void playDownSound(@NonNull SoundManager soundManager) {
        if (!clicked) super.playDownSound(soundManager);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
        if (getOpacity() > 0){
            return super.mouseClicked(click, doubled);
        } else {
            return false;
        }
    }

    public void resetClickedState() {
        clicked = false;
    }

    public int getOpacity(){
        return opacitySupplier.getAsInt();
    }

    @Override
    public boolean isHovered() {
        return super.isHovered() && getOpacity() > 0;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {

    }
}
