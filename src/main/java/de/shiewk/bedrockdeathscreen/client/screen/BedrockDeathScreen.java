package de.shiewk.bedrockdeathscreen.client.screen;

import de.shiewk.bedrockdeathscreen.client.BedrockDeathScreenClient;
import de.shiewk.bedrockdeathscreen.client.screen.components.BedrockDeathScreenButton;
import de.shiewk.bedrockdeathscreen.config.BedrockDeathScreenConfig;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class BedrockDeathScreen extends DeathScreen {

    public static final Identifier VIGNETTE = Identifier.of("bedrockdeathscreen", "textures/gui/death_vignette.png");

    private final BedrockDeathScreenConfig config;
    private final long screenCreationTime;
    private final Text message;
    private Text scoreText = Text.empty();
    private static final Text menuMessage = Text.translatable("deathScreen.titleScreen");
    private static final MutableText confirmQuitText = Text.translatable("deathScreen.quit.confirm");
    private final boolean hardcore;
    private final int deg = (int) (Math.random() * 360);
    private boolean confirmingExit = false;
    private boolean wasHoveringButtons = false;

    private BedrockDeathScreenButton respawnButton;
    private BedrockDeathScreenButton menuButton;

    public BedrockDeathScreen(@Nullable Text message, boolean isHardcore) {
        super(message, isHardcore);
        this.config = BedrockDeathScreenClient.getConfig();
        this.message = message;
        this.hardcore = isHardcore;
        screenCreationTime = Util.getMeasuringTimeNano();
    }

    public float getTotalScreenTime(){
        return (Util.getMeasuringTimeNano() - screenCreationTime) / 1000000f;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        final float totalScreenTime = getTotalScreenTime();
        context.fill(0, 0, width, height, new Color(0, 0, 0, (int) Math.min(80, totalScreenTime/4f)).getRGB());

        if (totalScreenTime > 750.0f){
            final int backOpacity = (int) Math.min(255, (totalScreenTime - 750f) / 10f);
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    VIGNETTE,
                    0,
                    0,
                    0.0F,
                    0.0F,
                    context.getScaledWindowWidth(),
                    context.getScaledWindowHeight(),
                    context.getScaledWindowWidth(),
                    context.getScaledWindowHeight(),
                    0xffffff + (backOpacity << 24)
            );
            context.fill(0, 0, width, height, new Color(50, 0, 0, (int) (backOpacity/2.5)).getRGB());

            final int textOpacity = (int) Math.min(255, (totalScreenTime - 750f) / 3f);
            if (textOpacity > 3){
                Matrix3x2fStack stack = context.getMatrices().pushMatrix();
                stack.scale(2F, 2F, stack);
                context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2 / 2, (int) (this.height / 3.5 / 2 - 10), new Color(255, 255, 255, textOpacity).getRGB());
                stack.popMatrix();
                if (message != null) {
                    context.drawCenteredTextWithShadow(this.textRenderer, this.message, this.width / 2, (int) (this.height / 3.5), new Color(255, 255, 255, textOpacity).getRGB());
                }
            }
            if (config.showScore){
                final int scoreTextOpacity = (int) Math.min(255, (totalScreenTime - 1250f) / 3f);
                if (scoreTextOpacity > 3){
                    context.drawCenteredTextWithShadow(this.textRenderer, this.scoreText, this.width / 2, (int) (this.height / 3.5) + 12, new Color(255, 255, 255, scoreTextOpacity).getRGB());
                }
            }
        }

        if (confirmingExit){
            context.drawCenteredTextWithShadow(this.textRenderer, BedrockDeathScreen.confirmQuitText, this.width / 2, this.height - this.height / 3 - 24, new Color(255, 255, 255, 255).getRGB());
        }
        respawnButton.render(context, mouseX, mouseY, delta);
        menuButton.render(context, mouseX, mouseY, delta);

        boolean hoveringButton = menuButton.isHovered() || respawnButton.isHovered();
        if (hoveringButton != wasHoveringButtons){
            wasHoveringButtons = hoveringButton;
        }

    }

    @Override
    protected void init() {
        Text respawnMessage = this.hardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn");
        if (this.client != null && this.client.player != null) {
            this.scoreText = Text.translatable("deathScreen.score.value", Text.literal(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
        }
        respawnButton = new BedrockDeathScreenButton(
                width / 2 - 75,
                height - height / 3 - 9,
                150,
                22,
                respawnMessage,
                textRenderer,
                0x3c8527,
                0x1d4d13,
                0x4f913c,
                0x1d4d13,
                0xffffff,
                0x1d4d13,
                0x4a7142,
                true,
                () -> (int) Math.min(255, (getTotalScreenTime() - 1250f) / 3f),
                this::respawn
        );
        menuButton = new BedrockDeathScreenButton(
                width / 2 - 75,
                height - height / 3 + 21,
                150,
                22,
                menuMessage,
                textRenderer,
                0xd0d1d4,
                0x58585a,
                0xe3e3e5,
                0xb1b2b5,
                0x1e1e1e,
                0xb1b2b5,
                0xe0e0e1,
                false,
                () -> (int) Math.min(255, (getTotalScreenTime() - 1750f) / 3f),
                this::clickQuitButton
        );
    }

    private void respawn(){
        if (this.client != null && this.client.player != null) {
            this.client.player.requestRespawn();
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (respawnButton.mouseClicked(click, doubled)) return true;
        return menuButton.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(Click click) {
        respawnButton.mouseReleased(click);
        menuButton.mouseReleased(click);
        return true;
    }

    public float calcCamPitch() {
        return 20;
    }

    public float calcCamYaw(){
        return deg < 180 ? deg : deg-360;
    }

    public float calcCameraOffset(){
        final double e = Math.E;
        final double del = getTotalScreenTime() / 600d;
        final double ep = Math.pow(e, del);
        return (float) (ep / (1d + ep) * 7d + 2d);
    }

    private void clickQuitButton() {
        if (confirmingExit) {
            quitLevel();
        } else {
            confirmingExit = true;
            int y = menuButton.getY();
            menuButton.setY(respawnButton.getY());
            respawnButton.setY(y);

            menuButton.resetClickedState();
            respawnButton.resetClickedState();
        }
    }

    private void quitLevel() {
        assert this.client != null;
        if (this.client.world != null) {
            this.client.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
        }

        this.client.disconnectWithSavingScreen();
        this.client.setScreen(new TitleScreen());
    }
}
