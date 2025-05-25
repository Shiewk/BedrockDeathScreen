package de.shiewk.bedrockdeathscreen.client.screen.components;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;

import java.util.function.IntSupplier;

public class BedrockDeathScreenButton extends ClickableWidget {

    public static final int colorBlack = 0x000000;
    public final TextRenderer textRenderer;
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
            Text message, TextRenderer textRenderer,
            int colorPrimary, int colorSecondary,
            int colorBorder, int colorHover,
            int colorText, int colorPrimaryPressed, int colorBorderPressed, boolean textShadow,
            IntSupplier opacitySupplier, Runnable clickAction
    ) {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
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
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        final boolean mouseHover = isHovered();

        final int opacity = getOpacity();
        if (opacity < 4) return;
        final int startX = getX();
        final int startY = getY();
        final int endX = getX() + getWidth();
        final int endY = getY() + getHeight();

        boolean shouldAppearPressed = pressed || clicked;
        if (shouldAppearPressed){
            context.fill(startX+2, startY+4, endX-2, endY+2, changeColorOpacity(colorPrimaryPressed, opacity));
            context.drawBorder(startX+1, startY+3, (endX-1) - (startX+1), (endY+1) - (startY+1), changeColorOpacity(colorBorderPressed, opacity));
            context.drawBorder(startX, startY+2, endX - startX, (endY+2) - startY, changeColorOpacity(colorBlack, opacity));
        } else {
            context.fill(startX+2, startY+2, endX-2, endY-1, (mouseHover ? changeColorOpacity(colorHover, opacity) : changeColorOpacity(colorPrimary, opacity)));
            context.drawBorder(startX+1, startY+1, (endX-1) - (startX+1), (endY) - (startY+1), changeColorOpacity(colorBorder, opacity));
            context.fill(startX+1, endY, endX-1, endY+3, changeColorOpacity(colorSecondary, opacity));
            context.drawBorder(startX, startY, endX - startX, (endY+4) - startY, changeColorOpacity(colorBlack, opacity));
        }

        if (textShadow){
            context.drawCenteredTextWithShadow(
                    textRenderer,
                    getMessage(),
                    startX+getWidth()/2,
                    endY - getHeight()/2 - (shouldAppearPressed ? 1 : 3),
                    changeColorOpacity(colorText, opacity)
            );
        } else {
            context.drawText(
                    textRenderer,
                    getMessage(),
                    startX + (getWidth()/2) - (textRenderer.getWidth(getMessage())/2),
                    endY - getHeight()/2 - (shouldAppearPressed ? 1 : 3),
                    changeColorOpacity(colorText, opacity),
                    false
            );
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        pressed = true;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        if (pressed && hovered) {
            clicked = true;
            clickAction.run();
        }
        pressed = false;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        if (!clicked) super.playDownSound(soundManager);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (getOpacity() > 0){
            return super.mouseClicked(mouseX, mouseY, button);
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
}
