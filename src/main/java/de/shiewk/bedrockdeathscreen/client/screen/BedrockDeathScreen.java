package de.shiewk.bedrockdeathscreen.client.screen;

import de.shiewk.bedrockdeathscreen.client.BedrockDeathScreenClient;
import de.shiewk.bedrockdeathscreen.client.screen.components.BedrockDeathScreenButton;
import de.shiewk.bedrockdeathscreen.config.BedrockDeathScreenConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class BedrockDeathScreen extends DeathScreen {

    public static final Identifier VIGNETTE = Identifier.fromNamespaceAndPath("bedrockdeathscreen", "textures/gui/death_vignette.png");

    private final BedrockDeathScreenConfig config;
    private final long screenCreationTime;
    private final Component message;
    private Component scoreText = Component.empty();
    private static final Component menuMessage = Component.translatable("deathScreen.titleScreen");
    private static final MutableComponent confirmQuitText = Component.translatable("deathScreen.quit.confirm");
    private final boolean hardcore;
    private final int deg = (int) (Math.random() * 360);
    private boolean confirmingExit = false;
    private boolean wasHoveringButtons = false;

    private BedrockDeathScreenButton respawnButton;
    private BedrockDeathScreenButton menuButton;

    public BedrockDeathScreen(@Nullable Component message, boolean isHardcore, LocalPlayer decedent) {
        super(message, isHardcore, decedent);
        this.config = BedrockDeathScreenClient.getConfig();
        this.message = message;
        this.hardcore = isHardcore;
        screenCreationTime = Util.getNanos();
    }

    public float getTotalScreenTime(){
        return (Util.getNanos() - screenCreationTime) / 1000000f;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {

        final float totalScreenTime = getTotalScreenTime();
        graphics.fill(0, 0, width, height, new Color(0, 0, 0, (int) Math.min(80, totalScreenTime/4f)).getRGB());

        if (totalScreenTime > 750.0f){
            final int backOpacity = (int) Math.min(255, (totalScreenTime - 750f) / 10f);
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    VIGNETTE,
                    0,
                    0,
                    0.0F,
                    0.0F,
                    graphics.guiWidth(),
                    graphics.guiHeight(),
                    graphics.guiWidth(),
                    graphics.guiHeight(),
                    0xffffff + (backOpacity << 24)
            );
            graphics.fill(0, 0, width, height, new Color(50, 0, 0, (int) (backOpacity/2.5)).getRGB());

            final int textOpacity = (int) Math.min(255, (totalScreenTime - 750f) / 3f);
            if (textOpacity > 3){
                Matrix3x2fStack stack = graphics.pose().pushMatrix();
                stack.scale(2F, 2F, stack);
                graphics.centeredText(this.font, this.title, this.width / 2 / 2, (int) (this.height / 3.5 / 2 - 10), new Color(255, 255, 255, textOpacity).getRGB());
                stack.popMatrix();
                if (message != null) {
                    graphics.centeredText(this.font, this.message, this.width / 2, (int) (this.height / 3.5), new Color(255, 255, 255, textOpacity).getRGB());
                }
            }
            if (config.showScore){
                final int scoreTextOpacity = (int) Math.min(255, (totalScreenTime - 1250f) / 3f);
                if (scoreTextOpacity > 3){
                    graphics.centeredText(this.font, this.scoreText, this.width / 2, (int) (this.height / 3.5) + 12, new Color(255, 255, 255, scoreTextOpacity).getRGB());
                }
            }
        }

        if (confirmingExit){
            graphics.centeredText(this.font, BedrockDeathScreen.confirmQuitText, this.width / 2, this.height - this.height / 3 - 24, new Color(255, 255, 255, 255).getRGB());
        }
        respawnButton.extractRenderState(graphics, mouseX, mouseY, delta);
        menuButton.extractRenderState(graphics, mouseX, mouseY, delta);

        boolean hoveringButton = menuButton.isHovered() || respawnButton.isHovered();
        if (hoveringButton != wasHoveringButtons){
            wasHoveringButtons = hoveringButton;
        }

    }

    @Override
    protected void init() {
        Component respawnMessage = this.hardcore ? Component.translatable("deathScreen.spectate") : Component.translatable("deathScreen.respawn");
        if (this.minecraft.player != null) {
            this.scoreText = Component.translatable(
                    "deathScreen.score.value",
                    Component.literal(Integer.toString(this.minecraft.player.getScore()))
                            .withStyle(ChatFormatting.YELLOW)
            );
        }
        respawnButton = new BedrockDeathScreenButton(
                width / 2 - 75,
                height - height / 3 - 9,
                150,
                22,
                respawnMessage,
                font,
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
                font,
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
        if (this.minecraft.player != null) {
            this.minecraft.player.respawn();
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent ev, boolean doubled) {
        if (respawnButton.mouseClicked(ev, doubled)) return true;
        return menuButton.mouseClicked(ev, doubled);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent ev) {
        respawnButton.mouseReleased(ev);
        menuButton.mouseReleased(ev);
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
        return (float) (ep / (1d + ep) * 1.75d + 0.5d);
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
        if (this.minecraft.level != null) {
            this.minecraft.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
        }

        this.minecraft.disconnectWithSavingScreen();
        this.minecraft.gui.setScreen(new TitleScreen());
    }
}
