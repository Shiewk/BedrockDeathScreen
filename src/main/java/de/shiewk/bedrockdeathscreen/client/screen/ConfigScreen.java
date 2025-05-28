package de.shiewk.bedrockdeathscreen.client.screen;

import de.shiewk.bedrockdeathscreen.client.BedrockDeathScreenClient;
import de.shiewk.bedrockdeathscreen.config.BedrockDeathScreenConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.io.IOException;

public final class ConfigScreen extends Screen {

    private final Screen parent;
    private final BedrockDeathScreenConfig config = BedrockDeathScreenClient.getConfig();
    private boolean configChanged = false;

    public ConfigScreen(Screen parent) {
        super(Text.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        addDrawableChild(new TextWidget(
                0, 5, width, 9,
                Text.translatable("bedrockdeathscreen.config"),
                textRenderer
        ));
        addDrawableChild(new ButtonWidget.Builder(
                Text.translatable("gui.done"),
                button -> this.close()
        ).position(width / 2 - 75, this.height - 25).build());

        GridWidget grid = new GridWidget();
        grid.getMainPositioner().margin(4, 4, 4, 4);
        final GridWidget.Adder adder = grid.createAdder(2);

        adder.add(createButton(
                "bedrockdeathscreen.config.showScore",
                config.showScore,
                button -> {
                    config.showScore = !config.showScore;
                    configChanged = true;
                    button.setMessage(getButtonText("bedrockdeathscreen.config.showScore", config.showScore));
                }
        ));

        grid.refreshPositions();
        SimplePositioningWidget.setPos(grid, 0, 0, this.width, this.height, 0.5F, 0.5F);
        grid.forEachChild(this::addDrawableChild);
    }

    private Text getButtonText(String translation, boolean state) {
        return Text.translatable(
                translation,
                state ? Text.translatable("gui.yes") : Text.translatable("gui.no")
        ).withColor(state ? 0x00ff00 : 0xff5555);
    }

    private ButtonWidget createButton(String translation, boolean state, ButtonWidget.PressAction action) {
        return ButtonWidget.builder(
                getButtonText(translation, state),
                action
        ).build();
    }

    @Override
    public void close() {
        if (configChanged){
            try {
                BedrockDeathScreenClient.saveConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        assert client != null;
        client.setScreen(parent);
    }
}
