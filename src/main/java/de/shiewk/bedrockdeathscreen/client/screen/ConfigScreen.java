package de.shiewk.bedrockdeathscreen.client.screen;

import de.shiewk.bedrockdeathscreen.client.BedrockDeathScreenClient;
import de.shiewk.bedrockdeathscreen.config.BedrockDeathScreenConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public final class ConfigScreen extends Screen {

    private final Screen parent;
    private final BedrockDeathScreenConfig config = BedrockDeathScreenClient.getConfig();
    private boolean configChanged = false;

    public ConfigScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        addWidget(new StringWidget(
                0, 5, width, 9,
                Component.translatable("bedrockdeathscreen.config"),
                font
        ));
        addWidget(new Button.Builder(
                Component.translatable("gui.done"),
                _ -> this.onClose()
        ).pos(width / 2 - 75, this.height - 25).build());

        GridLayout grid = new GridLayout();
        grid.defaultCellSetting().padding(4, 4, 4, 4);
        final GridLayout.RowHelper adder = grid.createRowHelper(2);

        adder.addChild(createButton(
                "bedrockdeathscreen.config.showScore",
                config.showScore,
                button -> {
                    config.showScore = !config.showScore;
                    configChanged = true;
                    button.setMessage(getButtonText("bedrockdeathscreen.config.showScore", config.showScore));
                }
        ));

        grid.arrangeElements();
        FrameLayout.alignInRectangle(grid, 0, 0, this.width, this.height, 0.5F, 0.5F);
        grid.visitWidgets(this::addWidget);
    }

    private Component getButtonText(String translation, boolean state) {
        return Component.translatable(
                translation,
                state ? Component.translatable("gui.yes") : Component.translatable("gui.no")
        ).withColor(state ? 0x00ff00 : 0xff5555);
    }

    private Button createButton(String translation, boolean state, Button.OnPress action) {
        return Button.builder(
                getButtonText(translation, state),
                action
        ).build();
    }

    @Override
    public void onClose() {
        if (configChanged){
            try {
                BedrockDeathScreenClient.saveConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        minecraft.gui.setScreen(parent);
    }
}
