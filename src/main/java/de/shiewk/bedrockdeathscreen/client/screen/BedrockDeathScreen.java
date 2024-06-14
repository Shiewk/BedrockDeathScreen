package de.shiewk.bedrockdeathscreen.client.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class BedrockDeathScreen extends DeathScreen {

    private int ticksSinceDeath = 0;
    private Text scoreText = Text.empty();
    private Text message;
    private static final Text menuMessage = Text.translatable("deathScreen.titleScreen");
    private Text respawnMessage;
    private final boolean hardcore;
    private final Identifier SOUND = Identifier.of("minecraft", "ui.button.click");

    public BedrockDeathScreen(@Nullable Text message, boolean isHardcore) {
        super(message, isHardcore);
        this.message = message;
        this.hardcore = isHardcore;
    }

    private boolean hoversRespawn(int mouseX, int mouseY){
        int startX = width/2 - 75;
        int startY = height - height / 3 - 9;
        int endX = width/2 + 75;
        int endY = height - height / 3 + 13;
        return getTotalDelta(0) > 1050 &&
                mouseX > startX &&
                mouseX < endX &&
                mouseY > startY &&
                mouseY < endY;
    }

    private boolean hoversMenuButton(int mouseX, int mouseY){
        final int startX = width/2 - 75;
        final int startY = height - height / 3 - 9 + 30;
        final int endX = width/2 + 75;
        final int endY = height - height / 3 + 13 + 30;
        return getTotalDelta(0) > 1050 &&
                mouseX > startX &&
                mouseX < endX &&
                mouseY > startY &&
                mouseY < endY;
    }

    private void renderRespawnButton(DrawContext context, int mouxeX, int mouseY, float delta, float opacity){
        if ((int) opacity == 0){
            return;
        }
        final int primary = new Color(60, 133, 39, (int) opacity).getRGB();
        final int secondary = new Color(29, 77, 19, (int) opacity).getRGB();
        final int gaccent = new Color(79, 145, 60, (int) opacity).getRGB();
        final int black = new Color(0, 0, 0, (int) opacity).getRGB();
        final int textColor = new Color(255, 255, 255, (int) opacity).getRGB();
        final boolean mouseHover = hoversRespawn(mouxeX, mouseY);

        final int startX = width/2 - 75;
        final int startY = height - height / 3 - 9;
        final int endX = width/2 + 75;
        final int endY = height - height / 3 + 13;

        context.fill(startX+2, startY+2, endX-2, endY-1, (mouseHover ? secondary : primary));
        context.drawBorder(startX+1, startY+1, (endX-1) - (startX+1), (endY) - (startY+1), gaccent);
        context.fill(startX+1, endY, endX-1, endY+3, secondary);
        context.drawBorder(startX, startY, (endX) - (startX), (endY+4) - (startY), black);

        final MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.scale(1.25f, 1.25f, 1.25f);

        context.drawCenteredTextWithShadow(textRenderer, respawnMessage, (int) (width/2/1.25), (int) (height/1.25 - height/1.25/3 - 1), textColor);

        matrices.pop();
    }

    private void renderMenuButton(DrawContext context, int mouxeX, int mouseY, float delta, float opacity){
        if ((int) opacity == 0){
            return;
        }
        final int primary = new Color(208, 209, 212, (int) opacity).getRGB();
        final int secondary = new Color(88, 88, 90, (int) opacity).getRGB();
        final int buttonAccent = new Color(177, 178, 181, (int) opacity).getRGB();
        final int gaccent = new Color(227, 227, 229, (int) opacity).getRGB();
        final int black = new Color(0, 0, 0, (int) opacity).getRGB();
        final int textColor = new Color(30, 30, 30, (int) opacity).getRGB();
        final boolean mouseHover = hoversMenuButton(mouxeX, mouseY);

        final int startX = width/2 - 75;
        final int startY = height - height / 3 - 9 + 30;
        final int endX = width/2 + 75;
        final int endY = height - height / 3 + 13 + 30;

        context.fill(startX+2, startY+2, endX-2, endY-1, (mouseHover ? buttonAccent : primary));
        context.drawBorder(startX+1, startY+1, (endX-1) - (startX+1), (endY) - (startY+1), gaccent);
        context.fill(startX+1, endY, endX-1, endY+3, secondary);
        context.drawBorder(startX, startY, (endX) - (startX), (endY+4) - (startY), black);

        context.drawText(textRenderer, menuMessage, (int) (width/2) - (textRenderer.getWidth(menuMessage)/2), (int) (height - height/3 - 1 + 31), textColor, false);
    }

    private float getTotalDelta(float delta){
        return ticksSinceDeath * 50f + delta;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        final float totalDelta = getTotalDelta(delta);
        context.fill(0, 0, width, height, new Color(0, 0, 0, (int) Math.min(80, totalDelta/4f)).getRGB());
        if (totalDelta > 750.0f){
            final int backOpacity = (int) Math.min(255, (totalDelta - 750f) / 10f);
            context.fill(0, 0, width, height, new Color(155, 0, 0, (int) (backOpacity/2.5)).getRGB());

            final int textOpacity = (int) Math.min(255, (totalDelta - 750f) / 3f);
            if (textOpacity > 0){
                context.getMatrices().push();
                context.getMatrices().scale(2F, 2F, 2F);
                context.drawCenteredTextWithShadow(this.textRenderer, this.title, (int) (this.width / 2 / 2), (int) (this.height / 3.5 / 2 - 10), new Color(255, 255, 255, textOpacity).getRGB());
                context.getMatrices().pop();
                context.drawCenteredTextWithShadow(this.textRenderer, this.message, this.width / 2, (int) (this.height / 3.5), new Color(255, 255, 255, textOpacity).getRGB());
            }
        }
        if (totalDelta > 1250f){
            float respawnOpacity = (int) Math.min(255, (totalDelta - 1250f) / 3f);
            renderRespawnButton(context, mouseX, mouseY, delta, respawnOpacity);
        }
        if (totalDelta > 1750f){
            float menuOpacity = (int) Math.min(255, (totalDelta - 1750f) / 3f);
            renderMenuButton(context, mouseX, mouseY, delta, menuOpacity);
        }
    }

    @Override
    protected void init() {
        this.respawnMessage = this.hardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn");
        this.scoreText = Text.translatable("deathScreen.score.value", new Object[]{Text.literal(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW)});
    }

    @Override
    public void tick() {
        super.tick();
        ticksSinceDeath++;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoversRespawn((int) mouseX, (int) mouseY)){
            ticksSinceDeath = 0;
            this.client.player.requestRespawn();
            this.client.player.playSoundToPlayer(SoundEvent.of(SOUND), SoundCategory.MASTER, 1f, 1f);
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
