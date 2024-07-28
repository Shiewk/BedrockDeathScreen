package de.shiewk.bedrockdeathscreen.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class BedrockDeathScreen extends DeathScreen {

    private int ticksSinceDeath = 0;
    private final Text message;
    private Text scoreText = Text.empty();
    private static final Text menuMessage = Text.translatable("deathScreen.titleScreen");
    private static final MutableText confirmQuitText = Text.translatable("deathScreen.quit.confirm");
    private Text respawnMessage;
    private final boolean hardcore;
    private final Identifier SOUND = Identifier.of("minecraft", "ui.button.click");
    private final int deg = (int) (Math.random() * 360);
    private boolean confirmingExit = false;

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

    private void renderRespawnButton(DrawContext context, int mouxeX, int mouseY, float opacity){
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

        context.drawCenteredTextWithShadow(textRenderer, respawnMessage, width/2, height - height/3 - 1, textColor);
    }

    private void renderMenuButton(DrawContext context, int mouxeX, int mouseY, float opacity){
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

        context.drawText(textRenderer, menuMessage, (width/2) - (textRenderer.getWidth(menuMessage)/2), height - height/3 - 1 + 31, textColor, false);
    }

    private void renderConfirmRespawnButton(DrawContext context, int mouxeX, int mouseY){
        final int primary = new Color(60, 133, 39, (int) (float) 255.0).getRGB();
        final int secondary = new Color(29, 77, 19, (int) (float) 255.0).getRGB();
        final int gaccent = new Color(79, 145, 60, (int) (float) 255.0).getRGB();
        final int black = new Color(0, 0, 0, (int) (float) 255.0).getRGB();
        final int textColor = new Color(255, 255, 255, (int) (float) 255.0).getRGB();
        final boolean mouseHover = hoversMenuButton(mouxeX, mouseY);

        final int startX = width/2 - 75;
        final int startY = height - height / 3 - 9 + 30;
        final int endX = width/2 + 75;
        final int endY = height - height / 3 + 13 + 30;

        context.fill(startX+2, startY+2, endX-2, endY-1, (mouseHover ? secondary : primary));
        context.drawBorder(startX+1, startY+1, (endX-1) - (startX+1), (endY) - (startY+1), gaccent);
        context.fill(startX+1, endY, endX-1, endY+3, secondary);
        context.drawBorder(startX, startY, (endX) - (startX), (endY+4) - (startY), black);

        context.drawText(textRenderer, respawnMessage, (width/2) - (textRenderer.getWidth(respawnMessage)/2), height - height/3 - 1 + 30, textColor, false);
    }

    private void renderConfirmQuitButton(DrawContext context, int mouxeX, int mouseY){
        final int primary = new Color(208, 209, 212, (int) (float) 255.0).getRGB();
        final int secondary = new Color(88, 88, 90, (int) (float) 255.0).getRGB();
        final int buttonAccent = new Color(177, 178, 181, (int) (float) 255.0).getRGB();
        final int gaccent = new Color(227, 227, 229, (int) (float) 255.0).getRGB();
        final int black = new Color(0, 0, 0, (int) (float) 255.0).getRGB();
        final int textColor = new Color(30, 30, 30, (int) (float) 255.0).getRGB();
        final boolean mouseHover = hoversRespawn(mouxeX, mouseY);

        final int startX = width/2 - 75;
        final int startY = height - height / 3 - 9;
        final int endX = width/2 + 75;
        final int endY = height - height / 3 + 13;

        context.fill(startX+2, startY+2, endX-2, endY-1, (mouseHover ? buttonAccent : primary));
        context.drawBorder(startX+1, startY+1, (endX-1) - (startX+1), (endY) - (startY+1), gaccent);
        context.fill(startX+1, endY, endX-1, endY+3, secondary);
        context.drawBorder(startX, startY, (endX) - (startX), (endY+4) - (startY), black);

        context.drawText(textRenderer, menuMessage, (width/2) - (textRenderer.getWidth(menuMessage)/2), height - height/3 - 1, textColor, false);
    }

    public float getTotalDelta(float delta){
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
                context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2 / 2, (int) (this.height / 3.5 / 2 - 10), new Color(255, 255, 255, textOpacity).getRGB());
                context.getMatrices().pop();
                context.drawCenteredTextWithShadow(this.textRenderer, this.message, this.width / 2, (int) (this.height / 3.5), new Color(255, 255, 255, textOpacity).getRGB());
            }
            final int scoreTextOpacity = (int) Math.min(255, (totalDelta - 1250f) / 3f);
            if (scoreTextOpacity > 0){
                context.drawCenteredTextWithShadow(this.textRenderer, this.scoreText, this.width / 2, (int) (this.height / 3.5) + 12, new Color(255, 255, 255, scoreTextOpacity).getRGB());
            }
        }
        if (!confirmingExit){
            if (totalDelta > 1250f){
                float respawnOpacity = (int) Math.min(255, (totalDelta - 1250f) / 3f);
                renderRespawnButton(context, mouseX, mouseY, respawnOpacity);
            }
            if (totalDelta > 1750f){
                float menuOpacity = (int) Math.min(255, (totalDelta - 1750f) / 3f);
                renderMenuButton(context, mouseX, mouseY, menuOpacity);
            }
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer, BedrockDeathScreen.confirmQuitText, this.width / 2, this.height - this.height / 3 - 9 - 12, new Color(255, 255, 255, 255).getRGB());
            renderConfirmRespawnButton(context, mouseX, mouseY);
            renderConfirmQuitButton(context, mouseX, mouseY);
        }

    }

    @Override
    protected void init() {
        this.respawnMessage = this.hardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn");
        if (this.client != null && this.client.player != null) {
            this.scoreText = Text.translatable("deathScreen.score.value", Text.literal(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
        }
    }

    @Override
    public void tick() {
        super.tick();
        ticksSinceDeath++;
    }

    private void respawn(){
        ticksSinceDeath = 0;
        if (this.client != null && this.client.player != null) {
            this.client.player.requestRespawn();
        }
        playUISound();
    }

    private void playUISound() {
        if (this.client != null) {
            if (this.client.player != null) {
                this.client.player.playSoundToPlayer(SoundEvent.of(SOUND), SoundCategory.MASTER, .75f, 1f);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoversRespawn((int) mouseX, (int) mouseY)){
            if (!confirmingExit){
                respawn();
            } else {
                playUISound();
                quitLevel();
            }
            return true;
        } else if (hoversMenuButton((int) mouseX, (int) mouseY)){
            if (!confirmingExit){
                playUISound();
                this.confirmingExit = true;
            } else {
                respawn();
            }
            return true;
        }
        return false;
    }

    private void quitLevel(){
        if (this.client != null && this.client.world != null) {
            this.client.world.disconnect();
        }
        if (this.client != null) {
            this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
            this.client.setScreen(new TitleScreen());
        }
    }

    public float calcCamPitch() {
        return 20;
    }

    public float calcCamYaw(){
        return deg < 180 ? deg : deg-360;
    }

    public float calcCameraOffset(float delta){
        final double e = Math.E;
        final double del = getTotalDelta(delta) / 600d;
        final double ep = Math.pow(e, del);
        return (float) (ep / (1d + ep) * 7d + 2d);
    }
}
