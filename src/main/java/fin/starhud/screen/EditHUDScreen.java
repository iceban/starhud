package fin.starhud.screen;

import fin.starhud.Main;
import fin.starhud.config.BaseHUDSettings;
import fin.starhud.config.GeneralSettings;
import fin.starhud.config.GroupedHUDSettings;
import fin.starhud.config.Settings;
import fin.starhud.helper.Box;
import fin.starhud.hud.AbstractHUD;
import fin.starhud.hud.GroupedHUD;
import fin.starhud.hud.HUDComponent;
import fin.starhud.hud.HUDId;
import fin.starhud.hud.implementation.EffectHUD;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditHUDScreen extends Screen {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final GeneralSettings.EditHUDScreenSettings SETTINGS = Main.settings.generalSettings.screenSettings;

    private static final int PADDING = 25;
    private static final int WIDGET_WIDTH = 100;
    private static final int WIDGET_HEIGHT = 20;
    private static final int TEXT_FIELD_WIDTH = 40;
    private static final int SQUARE_WIDGET_LENGTH = 20;
    private static final int GAP = 5;

    public Screen parent;

    private final List<AbstractHUD> individualHUDs;
    private final List<GroupedHUD> groupedHUDs;

    private final Map<HUDId, BaseHUDSettings> oldIndividualHUDSettings;
    private final Map<String, GroupedHUDSettings> oldGroupedHUDSettings;

    private final List<HUDId> oldIndividualHudIds;
    private final List<GroupedHUDSettings> oldGroupedHUDs;

    private boolean dragging = false;
    private final List<AbstractHUD> selectedHUDs = new ArrayList<>();

    private boolean isHelpActivated = false;
    private boolean isMoreOptionActivated = false;

    // widgets
    private TextFieldWidget xField;
    private TextFieldWidget yField;
    private ButtonWidget alignmentXButton;
    private ButtonWidget alignmentYButton;
    private ButtonWidget directionXButton;
    private ButtonWidget directionYButton;
    private ButtonWidget scaleButton;

    // special group buttons
    private TextFieldWidget gapField;
    private ButtonWidget groupAlignmentButton;

    private static final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");

    private static final String[] HELP_KEYS = {
            "[Arrow Keys]",
            "[⇧ Shift + Arrows]",
            isMac ? "[⌘ Cmd + Arrows]" : "[Ctrl + Arrows]",
            "[⌥ Alt + Arrows]",
            isMac ? "[⌘ Cmd + R]" : "[Ctrl + R]",
            "[Click]",
            "[Drag]"
    };

    private static final String[] HELP_INFOS = {
            "Move HUD by 1",
            "Move HUD by 5",
            "Change Alignment",
            "Change Growth Direction",
            "Revert All Changes",
            "Select HUD",
            "Move HUD"
    };

    private static final int HELP_HEIGHT = 5 + (HELP_KEYS.length * 9) + 5;

    public EditHUDScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;

        HUDComponent.getInstance().setRenderInGameScreen(false);

        individualHUDs = HUDComponent.getInstance().getIndividualHUDs();
        groupedHUDs = HUDComponent.getInstance().getGroupedHUDs();

        oldIndividualHUDSettings = new HashMap<>();
        for (AbstractHUD p : individualHUDs) {
            BaseHUDSettings settings = p.getSettings();
            oldIndividualHUDSettings.put(p.getId(), new BaseHUDSettings(settings.x, settings.y, settings.originX, settings.originY, settings.growthDirectionX, settings.growthDirectionY, settings.scale));
        }

        oldGroupedHUDSettings = new HashMap<>();
        for (GroupedHUD p : groupedHUDs) {
            BaseHUDSettings settings = p.getSettings();
            oldGroupedHUDSettings.put(
                    p.getGroupId(),
                    new GroupedHUDSettings(
                            new BaseHUDSettings(settings.x, settings.y, settings.originX, settings.originY, settings.growthDirectionX, settings.growthDirectionY, settings.scale),
                            p.getGroupId(),
                            p.groupSettings.gap,
                            p.groupSettings.alignVertical,
                            p.groupSettings.boxColor,
                            p.groupSettings.hudIds
                    )
            );
        }

        oldIndividualHudIds = List.copyOf(Main.settings.hudList.individualHudIds);
        oldGroupedHUDs = List.copyOf(Main.settings.hudList.groupedHuds);
    }

    @Override
    protected void init() {

        final int CENTER_X = this.width / 2;
        final int CENTER_Y = this.height / 2 + (PADDING / 2);

        HUDComponent.getInstance().updateAll();

        xField = new TextFieldWidget(
                CLIENT.textRenderer,
                CENTER_X - TEXT_FIELD_WIDTH - (SQUARE_WIDGET_LENGTH / 2) - GAP,
                CENTER_Y - PADDING,
                TEXT_FIELD_WIDTH,
                WIDGET_HEIGHT,
                Text.of("X")
        );
        yField = new TextFieldWidget(
                CLIENT.textRenderer,
                CENTER_X + (SQUARE_WIDGET_LENGTH / 2) + GAP,
                CENTER_Y - PADDING,
                TEXT_FIELD_WIDTH,
                WIDGET_HEIGHT,
                Text.of("Y")
        );

        alignmentXButton = ButtonWidget.builder(
                Text.of("X Alignment: N/A"),
                button -> {
                    if (selectedHUDs.isEmpty()) return;
                    AbstractHUD selectedHUD = selectedHUDs.getFirst();
                    selectedHUD.getSettings().originX = selectedHUD.getSettings().originX.next();
                    selectedHUD.getSettings().growthDirectionX = selectedHUD.getSettings().growthDirectionX.recommendedScreenAlignment(selectedHUD.getSettings().originX);
                    selectedHUD.update();
                    alignmentXButton.setMessage(Text.of("X Alignment: " + selectedHUD.getSettings().originX));
                }
        ).dimensions(CENTER_X - WIDGET_WIDTH - (SQUARE_WIDGET_LENGTH / 2) - GAP, CENTER_Y - PADDING * 2, WIDGET_WIDTH, WIDGET_HEIGHT).build();

        alignmentYButton = ButtonWidget.builder(
                Text.of("Y Alignment: N/A"),
                button -> {
                    if (selectedHUDs.isEmpty()) return;
                    AbstractHUD selectedHUD = selectedHUDs.getFirst();
                    selectedHUD.getSettings().originY = selectedHUD.getSettings().originY.next();
                    selectedHUD.getSettings().growthDirectionY = selectedHUD.getSettings().growthDirectionY.recommendedScreenAlignment(selectedHUD.getSettings().originY);
                    selectedHUD.update();
                    alignmentYButton.setMessage(Text.of("Y Alignment: " + selectedHUD.getSettings().originY));
                }
        ).dimensions(CENTER_X + (SQUARE_WIDGET_LENGTH / 2) + GAP, CENTER_Y - PADDING * 2, WIDGET_WIDTH, WIDGET_HEIGHT).build();

        scaleButton = ButtonWidget.builder(
                Text.of("N/A"),
                button -> {
                    if (selectedHUDs.isEmpty()) return;
                    AbstractHUD selectedHUD = selectedHUDs.getFirst();
                    selectedHUD.getSettings().scale = (selectedHUD.getSettings().scale + 1) % 7;
                    selectedHUD.update();
                    scaleButton.setMessage(Text.of(Integer.toString(selectedHUD.getSettings().scale)));
                }
        ).tooltip(Tooltip.of(Text.of("Scale"))).dimensions(CENTER_X - (SQUARE_WIDGET_LENGTH / 2), CENTER_Y - PADDING * 2, SQUARE_WIDGET_LENGTH, SQUARE_WIDGET_LENGTH).build();

        directionXButton = ButtonWidget.builder(
                Text.of("X Direction: N/A"),
                button -> {
                    if (selectedHUDs.isEmpty()) return;
                    AbstractHUD selectedHUD = selectedHUDs.getFirst();
                    selectedHUD.getSettings().growthDirectionX = selectedHUD.getSettings().growthDirectionX.next();
                    selectedHUD.update();
                    directionXButton.setMessage(Text.of("X Direction: " + selectedHUD.getSettings().growthDirectionX));
                }
        ).dimensions(CENTER_X - WIDGET_WIDTH - (SQUARE_WIDGET_LENGTH / 2) - GAP, CENTER_Y - PADDING * 3, WIDGET_WIDTH, WIDGET_HEIGHT).build();

        directionYButton = ButtonWidget.builder(
                Text.of("Y Direction: N/A"),
                button -> {
                    if (selectedHUDs.isEmpty()) return;
                    AbstractHUD selectedHUD = selectedHUDs.getFirst();
                    selectedHUD.getSettings().growthDirectionY = selectedHUD.getSettings().growthDirectionY.next();
                    selectedHUD.update();
                    directionYButton.setMessage(Text.of("Y Direction: " + selectedHUD.getSettings().growthDirectionY));
                }
        ).dimensions(CENTER_X + (SQUARE_WIDGET_LENGTH / 2) + GAP, CENTER_Y - PADDING * 3, WIDGET_WIDTH, WIDGET_HEIGHT).build();

        xField.setChangedListener(text -> {
            if (selectedHUDs.isEmpty()) return;
            AbstractHUD selectedHUD = selectedHUDs.getFirst();
            try {
                selectedHUD.getSettings().x = Integer.parseInt(text);
                selectedHUD.update();
            } catch (NumberFormatException ignored) {}
        });

        yField.setChangedListener(text -> {
            if (selectedHUDs.isEmpty()) return;
            AbstractHUD selectedHUD = selectedHUDs.getFirst();
            try {
                selectedHUD.getSettings().y = Integer.parseInt(text);
                selectedHUD.update();
            } catch (NumberFormatException ignored) {}
        });

        ButtonWidget helpButton = ButtonWidget.builder(
                Text.of("?"),
                button -> {
                    isHelpActivated = !isHelpActivated;
                    onHelpSwitched();
                }
        )
                .tooltip(Tooltip.of(Text.of("Help")))
                .dimensions(CENTER_X - SQUARE_WIDGET_LENGTH - (GAP / 2), this.height - SQUARE_WIDGET_LENGTH - GAP, SQUARE_WIDGET_LENGTH, SQUARE_WIDGET_LENGTH)
                .build();

        ButtonWidget moreOptionButton = ButtonWidget.builder(
                Text.of("+"),
                button -> {
                    isMoreOptionActivated = !isMoreOptionActivated;
                    onMoreOptionSwitched();
                }
        )
                .tooltip(Tooltip.of(Text.of("More Options")))
                .dimensions(CENTER_X + (GAP / 2), this.height - SQUARE_WIDGET_LENGTH - GAP, SQUARE_WIDGET_LENGTH, SQUARE_WIDGET_LENGTH)
                .build();


        int terminatorWidth = 70;
        addDrawableChild(ButtonWidget.builder(Text.of("Save & Quit"), button -> {
            AutoConfig.getConfigHolder(Settings.class).save();
            onClose();
        }).dimensions(CENTER_X + (GAP / 2) + SQUARE_WIDGET_LENGTH + GAP, this.height - WIDGET_HEIGHT - GAP, terminatorWidth, WIDGET_HEIGHT).build());

        addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), button -> {
            close();
        }).dimensions(CENTER_X - (GAP / 2) - terminatorWidth - SQUARE_WIDGET_LENGTH - GAP, this.height - WIDGET_HEIGHT - GAP, terminatorWidth, WIDGET_HEIGHT).build());

        // special case: grouped hud buttons

        gapField = new TextFieldWidget(
                CLIENT.textRenderer,
                CENTER_X - terminatorWidth - (GAP / 2), this.height - GAP - SQUARE_WIDGET_LENGTH - GAP - SQUARE_WIDGET_LENGTH,
                terminatorWidth, SQUARE_WIDGET_LENGTH,
                Text.of("Gap")
        );

        gapField.setChangedListener(text -> {
            if (selectedHUDs.isEmpty()) return;
            if (!(selectedHUDs.getFirst() instanceof GroupedHUD hud)) return;

            try {
                hud.groupSettings.gap = Integer.parseInt(text);
            } catch (NumberFormatException ignored) {}
        });

        groupAlignmentButton = ButtonWidget.builder(
                Text.of("N/A"),
                button -> {
                    if (selectedHUDs.isEmpty()) return;
                    if (!(selectedHUDs.getFirst() instanceof GroupedHUD hud)) return;
                    hud.groupSettings.alignVertical = !hud.groupSettings.alignVertical;

                    groupAlignmentButton.setMessage(Text.of(
                            hud.groupSettings.alignVertical ? "Vertical" : "Horizontal"
                    ));
                }
        ).dimensions(CENTER_X + (GAP / 2), this.height - GAP - SQUARE_WIDGET_LENGTH - GAP - SQUARE_WIDGET_LENGTH, terminatorWidth, SQUARE_WIDGET_LENGTH).build();

        alignmentXButton.visible = false;
        directionXButton.visible = false;
        alignmentYButton.visible = false;
        directionYButton.visible = false;
        xField.visible = false;
        yField.visible = false;
        scaleButton.visible = false;

        gapField.visible = false;
        groupAlignmentButton.visible = false;

        addDrawableChild(helpButton);
        addDrawableChild(moreOptionButton);

        addDrawableChild(gapField);
        addDrawableChild(groupAlignmentButton);

        addDrawableChild(alignmentXButton);
        addDrawableChild(alignmentYButton);
        addDrawableChild(directionXButton);
        addDrawableChild(directionYButton);
        addDrawableChild(scaleButton);
        addDrawableChild(xField);
        addDrawableChild(yField);

        updateFieldsFromSelectedHUD();
    }

    private void updateFieldsFromSelectedHUD() {
        if (xField == null || yField == null) return;

        if (selectedHUDs.isEmpty()) {
            xField.setEditable(false);
            yField.setEditable(false);
            xField.setText("N/A");
            yField.setText("N/A");

            alignmentXButton.setMessage(Text.of("X Alignment: N/A"));
            directionXButton.setMessage(Text.of("X Direction: N/A"));
            alignmentYButton.setMessage(Text.of("Y Alignment: N/A"));
            directionYButton.setMessage(Text.of("Y Direction: N/A"));

            scaleButton.setMessage(Text.of("N/A"));

            gapField.setText("N/A");
            groupAlignmentButton.setMessage(Text.of("N/A"));

            gapField.setEditable(false);
            gapField.visible = false;
            groupAlignmentButton.visible = false;
            groupAlignmentButton.active = false;

            alignmentXButton.active = false;
            directionXButton.active = false;
            alignmentYButton.active = false;
            directionYButton.active = false;
            scaleButton.active = false;
        } else {
            AbstractHUD selectedHUD = selectedHUDs.getFirst();
            BaseHUDSettings settings = selectedHUD.getSettings();
            xField.setText(String.valueOf(settings.x));
            yField.setText(String.valueOf(settings.y));

            alignmentXButton.setMessage(Text.of("X Alignment: " + settings.originX));
            directionXButton.setMessage(Text.of("X Direction: " + settings.growthDirectionX));
            alignmentYButton.setMessage(Text.of("Y Alignment: " + settings.originY));
            directionYButton.setMessage(Text.of("Y Direction: " + settings.growthDirectionY));
            scaleButton.setMessage(Text.of(Integer.toString(settings.scale)));

            alignmentXButton.active = true;
            directionXButton.active = true;
            alignmentYButton.active = true;
            directionYButton.active = true;
            scaleButton.active = true;
            xField.setEditable(true);
            yField.setEditable(true);

            gapField.visible = false;
            groupAlignmentButton.visible = false;

            if (selectedHUD instanceof GroupedHUD hud) {
                gapField.visible = true;
                groupAlignmentButton.visible = true;

                gapField.setEditable(true);
                groupAlignmentButton.active = true;

                gapField.setText(
                        Integer.toString(hud.groupSettings.gap)
                );

                groupAlignmentButton.setMessage(Text.of(
                        hud.groupSettings.alignVertical ? "Vertical" : "Horizontal"
                ));
            }

            if (isMoreOptionActivated) {
                alignmentXButton.visible = true;
                directionXButton.visible = true;
                alignmentYButton.visible = true;
                directionYButton.visible = true;
                xField.visible = true;
                yField.visible = true;
                scaleButton.visible = true;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // draw help
        if (isHelpActivated) {
            final int CENTER_X = this.width / 2;
            final int CENTER_Y = this.height / 2 + (PADDING / 2);
            renderHelp(context, CENTER_X, CENTER_Y + GAP);
            if (!selectedHUDs.isEmpty())
                renderHUDInformation(context, CENTER_X, CENTER_Y + GAP  + HELP_HEIGHT + GAP);
        }

        // draw X and Y next to their textField.
        if (xField.isVisible() && yField.isVisible()) {
            context.drawText(CLIENT.textRenderer, "X:", xField.getX() - 5 - 2 - 3, xField.getY() + 6, 0xFFFFFFFF, true);
            context.drawText(CLIENT.textRenderer, ":Y", yField.getX() + yField.getWidth() + 3, yField.getY() + 6, 0xFFFFFFFF, true);
        }

        // draw dragBox if present
        if (dragSelection) {
            int x1 = Math.min(dragStartX, dragCurrentX);
            int y1 = Math.min(dragStartY, dragCurrentY);
            int x2 = Math.max(dragStartX, dragCurrentX);
            int y2 = Math.max(dragStartY, dragCurrentY);
            context.drawBorder(x1, y1, x2 - x1, y2 - y1, 0xFFFFFFFF);
        }

        // draw all visible hud bounding boxes.
        renderBoundingBoxes(context, mouseX, mouseY);
    }

    private void renderBoundingBoxes(DrawContext context, int mouseX, int mouseY) {
        for (AbstractHUD hud: individualHUDs) {
            renderHUD(context, hud, mouseX, mouseY);
        }

        for (AbstractHUD hud : groupedHUDs) {
            renderHUD(context, hud, mouseX, mouseY);
        }
    }

    private void renderHUD(DrawContext context, AbstractHUD hud, int mouseX, int mouseY) {
        if (!hud.shouldRender()) return; // if not rendered
        if (!hud.render(context)) return; // if ALSO not rendered (either failed or no information to render)

        if (hud.isScaled()) {
            context.getMatrices().pushMatrix();
            hud.setHUDScale(context);

            renderBoundingBox(context, hud, mouseX, mouseY);

            context.getMatrices().popMatrix();
            return;
        }

        renderBoundingBox(context, hud, mouseX, mouseY);
    }

    private void renderBoundingBox(DrawContext context, AbstractHUD hud, int mouseX, int mouseY) {
        Box boundingBox = hud.getBoundingBox();

        int x = boundingBox.getX();
        int y = boundingBox.getY();
        int width = boundingBox.getWidth();
        int height = boundingBox.getHeight();
        int color = boundingBox.getColor();
        int scale = hud.getSettings().scale;
        int selectedColor = SETTINGS.selectedBoxColor | 0x80000000;
        int selectedGroupColor = SETTINGS.selectedGroupBoxColor | 0x80000000;

        context.drawBorder(x, y, width, height, color);
        if (isHovered(x, y, width, height, mouseX, mouseY, scale)) {
            context.fill(x, y, x + width, y + height, (color & 0x00FFFFFF) | 0x80000000);
        }
        if (selectedHUDs.contains(hud)) {
            if (hud instanceof GroupedHUD || hud instanceof EffectHUD) // effect hud is a special case.
                context.fill(x, y, x + width, y + height, selectedGroupColor);
            else
                context.fill(x, y, x + width, y + height, selectedColor);
        }
    }

    private void renderHelp(DrawContext context, int x, int y) {
        int padding = 5;

        int lineHeight = CLIENT.textRenderer.fontHeight;
        int maxKeyWidth = CLIENT.textRenderer.getWidth(HELP_KEYS[1]);
        int maxInfoWidth = CLIENT.textRenderer.getWidth(HELP_INFOS[3]);

        int width = padding + maxKeyWidth + padding + 1 + padding + maxInfoWidth + padding;
        int height = padding + (lineHeight * HELP_KEYS.length) + padding - 2;

        x -= width / 2;
        y -= padding;

        context.fill(x, y, x + width, y + height, 0x80000000);

        for (int i = 0; i < HELP_KEYS.length; ++i) {
            String key = HELP_KEYS[i];
            String info = HELP_INFOS[i];

            context.drawText(CLIENT.textRenderer, key, x + padding, y + padding, 0xFFFFFFFF, false);
            context.drawText(CLIENT.textRenderer, info, x + padding + maxKeyWidth + padding + 1 + padding, y + padding, 0xFFFFFFFF, false);

            y += lineHeight;
        }
    }

    private void renderHUDInformation(DrawContext context, int x, int y) {
        String text = selectedHUDs.getFirst().getName();
        int textWidth = CLIENT.textRenderer.getWidth(text);
        int padding = 5;

        x -= (textWidth / 2);

        context.fill(x - padding, y - padding, x + textWidth + padding, y + CLIENT.textRenderer.fontHeight - 2 + padding, 0x80000000);
        context.drawText(CLIENT.textRenderer, text, x, y, 0xFFFFFFFF, false);
    }

    boolean dragSelection = false;
    int dragStartX, dragStartY;
    int dragCurrentX, dragCurrentY;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (super.mouseClicked(mouseX, mouseY, button))
            return true;

        if (button == 0) {
            for (AbstractHUD hud : individualHUDs)
                onMouseClicked(hud, mouseX, mouseY, button);

            for (AbstractHUD hud : groupedHUDs)
                onMouseClicked(hud, mouseX, mouseY, button);

            // if we are not dragging we can start an expanding box
            if (!dragging) {
                selectedHUDs.clear();
                updateFieldsFromSelectedHUD();
                dragSelection = true;
                dragStartX = (int) mouseX;
                dragStartY = (int) mouseY;
                dragCurrentX = (int) mouseX;
                dragCurrentY = (int) mouseY;
            }
        }
        return true;
    }

    public AbstractHUD pendingToggleHUD = null;

    public void onMouseClicked(AbstractHUD hud, double mouseX, double mouseY, int button) {
        if (!hud.shouldRender()) return;
        Box boundingBox = hud.getBoundingBox();
        int scale = hud.getSettings().scale;
        int x = boundingBox.getX();
        int y = boundingBox.getY();
        int width = boundingBox.getWidth();
        int height = boundingBox.getHeight();

        if (isHovered(x, y, width, height, (int) mouseX, (int) mouseY, scale)) {

            if (hasShiftDown()) {
                if (selectedHUDs.contains(hud))
                    pendingToggleHUD = hud;
                else
                    selectedHUDs.add(hud);
            } else {
                selectedHUDs.clear();
                selectedHUDs.add(hud);
            }

            dragging = true;
            updateFieldsFromSelectedHUD();
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (dragging) {
                dragging = false;
                if (!selectedHUDs.isEmpty()) {
                    AbstractHUD selectedHUD = selectedHUDs.getFirst();
                    xField.setText(String.valueOf(selectedHUD.getSettings().x));
                    yField.setText(String.valueOf(selectedHUD.getSettings().y));
                    hudAccumulatedDelta.clear();
                }

                if (pendingToggleHUD != null) {
                    selectedHUDs.remove(pendingToggleHUD);
                    pendingToggleHUD = null;
                    updateFieldsFromSelectedHUD();
                }
            } else {
                if (dragSelection)
                    dragSelection = false;
            }
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private final Map<AbstractHUD, Vector2d> hudAccumulatedDelta = new HashMap<>();

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && !selectedHUDs.isEmpty() && button == 0) {
            pendingToggleHUD = null;

            for (AbstractHUD hud : selectedHUDs) {
                double scaleFactor = hud.getSettings().getScaledFactor();

                Vector2d acc = hudAccumulatedDelta.computeIfAbsent(hud, h -> new Vector2d(0, 0));
                acc.x += deltaX;
                acc.y += deltaY;

                double scaledX = acc.x * scaleFactor;
                double scaledY = acc.y * scaleFactor;

                int dx = (int) scaledX;
                int dy = (int) scaledY;

                if (dx != 0 || dy != 0) {
                    hud.getSettings().x += dx;
                    hud.getSettings().y += dy;
                    hud.update();

                    acc.x -= dx / scaleFactor;
                    acc.y -= dy / scaleFactor;
                }
            }

            return true;
        } else if (dragSelection && button == 0) {
            dragCurrentX = (int) mouseX;
            dragCurrentY = (int) mouseY;

            int x1 = Math.min(dragStartX, dragCurrentX);
            int y1 = Math.min(dragStartY, dragCurrentY);
            int x2 = Math.max(dragStartX, dragCurrentX);
            int y2 = Math.max(dragStartY, dragCurrentY);

            selectedHUDs.clear();

            for (AbstractHUD hud : individualHUDs) {
                if (hud.shouldRender())
                    if (!hud.getBoundingBox().isEmpty())
                        if (intersectsBox(x1, y1, x2, y2, hud))
                            selectedHUDs.add(hud);

            }

            for (AbstractHUD hud : groupedHUDs) {
                if (hud.shouldRender())
                    if (!hud.getBoundingBox().isEmpty())
                        if (intersectsBox(x1, y1, x2, y2, hud))
                            selectedHUDs.add(hud);
            }

            updateFieldsFromSelectedHUD();
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!dragSelection && !dragging) {
            if (!selectedHUDs.isEmpty())
                for (AbstractHUD hud : selectedHUDs)
                    onKeyPressed(hud, keyCode, modifiers);

            boolean isCtrl = isMac
                    ? (modifiers & GLFW.GLFW_MOD_SUPER) != 0
                    : (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;

            boolean handled = false;

            switch (keyCode) {
                case GLFW.GLFW_KEY_G -> {
                    if (selectedHUDs.isEmpty()) break;
                    if (selectedHUDs.size() > 1) {
                        if (selectedHUDs.stream().noneMatch(hud -> hud instanceof GroupedHUD || hud instanceof EffectHUD)) {
                            group(selectedHUDs);
                            selectedHUDs.clear();
                        }
                    } else {
                        if (selectedHUDs.getFirst() instanceof GroupedHUD groupedHUD) {
                            unGroup(groupedHUD);
                            selectedHUDs.clear();
                        }
                    }
                    handled = true;
                }
                case GLFW.GLFW_KEY_R -> {
                    if (isCtrl) {
                        revertChanges();
                        selectedHUDs.clear();
                    }

                    handled = true;
                }
            }

            if (handled) {
                updateFieldsFromSelectedHUD();
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void onKeyPressed(AbstractHUD hud, int keyCode, int modifiers) {

        BaseHUDSettings settings = hud.getSettings();

        boolean handled = false;
        boolean isCtrl = isMac
                ? (modifiers & GLFW.GLFW_MOD_SUPER) != 0
                : (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
        boolean isShift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
        boolean isAlt = (modifiers & GLFW.GLFW_MOD_ALT) != 0;

        int step = isShift ? 5 : 1;

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT -> {
                if (isCtrl) {
                    settings.originX = settings.originX.prev();
                    settings.growthDirectionX = settings.growthDirectionX.recommendedScreenAlignment(settings.originX);
                }
                else if (isAlt) settings.growthDirectionX = settings.growthDirectionX.prev();
                else settings.x -= step;

                handled = true;
            }

            case GLFW.GLFW_KEY_RIGHT -> {
                if (isCtrl) {
                    settings.originX = settings.originX.next();
                    settings.growthDirectionX = settings.growthDirectionX.recommendedScreenAlignment(settings.originX);
                }
                else if (isAlt) settings.growthDirectionX = settings.growthDirectionX.next();
                else settings.x += step;

                handled = true;
            }

            case GLFW.GLFW_KEY_UP -> {
                if (isCtrl) {
                    settings.originY = settings.originY.prev();
                    settings.growthDirectionY = settings.growthDirectionY.recommendedScreenAlignment(settings.originY);
                }
                else if (isAlt) {
                    settings.growthDirectionY = settings.growthDirectionY.prev();
                }
                else settings.y -= step;

                handled = true;
            }

            case GLFW.GLFW_KEY_DOWN -> {
                if (isCtrl) {
                    settings.originY = settings.originY.next();
                    settings.growthDirectionY = settings.growthDirectionY.recommendedScreenAlignment(settings.originY);
                }
                else if (isAlt) settings.growthDirectionY = settings.growthDirectionY.next();
                else settings.y += step;

                handled = true;
            }

            case GLFW.GLFW_KEY_MINUS ->  {
                if (!yField.isFocused() && !xField.isFocused()) {
                    settings.scale = (settings.scale + 6) % 7;
                    handled = true;
                }
            }

            case GLFW.GLFW_KEY_EQUAL ->  {
                if (isShift && !yField.isFocused() && !xField.isFocused()) {
                    settings.scale = (settings.scale + 1) % 7;
                    handled = true;
                }
            }
        }

        if (handled) {
            hud.update();
            updateFieldsFromSelectedHUD();
        }
    }

    public boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY, int HUDScale) {
        float scale = HUDScale == 0 ? 1 : (float) CLIENT.getWindow().getScaleFactor() / HUDScale;
        int scaledMouseX = (int) (mouseX * scale);
        int scaledMouseY = (int) (mouseY * scale);
        return scaledMouseX >= x && scaledMouseX <= x + width &&
                scaledMouseY >= y && scaledMouseY <= y + height;
    }

    private boolean intersectsBox(int x1, int y1, int x2, int y2, AbstractHUD hud) {
        Box box = hud.getBoundingBox();

        float scale = hud.getSettings().scale == 0 ? 1.0f : (float) MinecraftClient.getInstance().getWindow().getScaleFactor() / hud.getSettings().scale;

        int scaledX1 = (int) (x1 * scale);
        int scaledY1 = (int) (y1 * scale);
        int scaledX2 = (int) (x2 * scale);
        int scaledY2 = (int) (y2 * scale);

        int hudLeft   = box.getX();
        int hudTop    = box.getY();
        int hudRight  = box.getX() + box.getWidth();
        int hudBottom = box.getY() + box.getHeight();

        return hudRight >= Math.min(scaledX1, scaledX2) &&
                hudLeft  <= Math.max(scaledX1, scaledX2) &&
                hudBottom >= Math.min(scaledY1, scaledY2) &&
                hudTop    <= Math.max(scaledY1, scaledY2);
    }


    private boolean isDirty() {
        List<HUDId> individualIds = Main.settings.hudList.individualHudIds;
        List<GroupedHUDSettings> groupedHUDSettings = Main.settings.hudList.groupedHuds;

//        for (HUDId id : individualIds)
//            System.out.println(id);
//
//        System.out.println("----------------");
//
//        for (HUDId id : oldIndividualHudIds)
//            System.out.println(id);
//
//        System.out.println("----------------");
//
        for (GroupedHUDSettings groupSetting : groupedHUDSettings)
            System.out.println(groupSetting);

        System.out.println("----------------");

        for (GroupedHUDSettings oldGroupSetting : oldGroupedHUDSettings.values())
            System.out.println(oldGroupSetting);

        System.out.println("!!!!!!!!!!!!!!!!!!!!!");

        if (!individualIds.equals(oldIndividualHudIds))
            return true;
        if (!groupedHUDSettings.equals(oldGroupedHUDs))
            return true;

        for (AbstractHUD hud : individualHUDs) {
            BaseHUDSettings current = hud.getSettings();
            BaseHUDSettings original = oldIndividualHUDSettings.get(hud.getId());
            if (original == null || !current.isEqual(original))
                return true;
        }

        for (GroupedHUDSettings current : groupedHUDSettings) {
            GroupedHUDSettings original = oldGroupedHUDSettings.get(current.id);
            if (original == null || !current.isEqual(original))
                return true;
        }

        return false;
    }


    private void revertChanges() {
        Main.settings.hudList.individualHudIds.clear();
        Main.settings.hudList.individualHudIds.addAll(oldIndividualHudIds);
        Main.settings.hudList.groupedHuds.clear();
        Main.settings.hudList.groupedHuds.addAll(oldGroupedHUDs);

        HUDComponent.getInstance().updateActiveHUDs();

        for (AbstractHUD hud : individualHUDs) {
            BaseHUDSettings original = oldIndividualHUDSettings.get(hud.getId());
            if (original != null)
                hud.getSettings().copySettings(original);
        }

        for (GroupedHUD hud : groupedHUDs) {
            GroupedHUDSettings original = oldGroupedHUDSettings.get(hud.groupSettings.id);
            System.out.println(hud.groupSettings);
            System.out.println("---------------");
            System.out.println(original);
            if (original != null)
                hud.groupSettings.copyFrom(original);

        }

        HUDComponent.getInstance().updateAll();
        AutoConfig.getConfigHolder(Settings.class).save();
    }


    @Override
    public void close() {
        if (isDirty()) {
            this.client.setScreen(new ConfirmScreen(
                    result -> {
                        if (result) {
                            revertChanges();
                            onClose();
                        } else {
                            this.client.setScreen(this);
                        }
                    },
                    Text.of("Discard Changes?"),
                    Text.of("You have unsaved changes. Do you want to discard them?")
            ));
        } else {
            onClose();
        }
    }

    public void onClose() {
        this.client.setScreen(this.parent);
        HUDComponent.getInstance().setRenderInGameScreen(true);
    }

    private void onHelpSwitched() {
    }

    private void onMoreOptionSwitched() {
        if (isMoreOptionActivated) {
            alignmentXButton.visible = true;
            directionXButton.visible = true;
            alignmentYButton.visible = true;
            directionYButton.visible = true;
            xField.visible = true;
            yField.visible = true;
            scaleButton.visible = true;
        } else {
            alignmentXButton.visible = false;
            directionXButton.visible = false;
            alignmentYButton.visible = false;
            directionYButton.visible = false;
            xField.visible = false;
            yField.visible = false;
            scaleButton.visible = false;
        }
    }

    // grouping function, experimental, may crash.

    // hud in huds MUST be ungrouped. not doing so will crash.
    public void group(List<AbstractHUD> huds) {
        GroupedHUDSettings newSettings = new GroupedHUDSettings();

        List<GroupedHUDSettings> groupedHUDs = Main.settings.hudList.groupedHuds;
        List<HUDId> individualHUDs = Main.settings.hudList.individualHudIds;

        // remove hud from individualHUDs, and add hud to the group via settings.
        for (AbstractHUD hud : huds) {
            if (hud.isInGroup()) {
                throw new IllegalStateException("HUD " + hud.getId() + " is already in a group.");
            }
            individualHUDs.remove(hud.getId());
            newSettings.hudIds.add(hud.getId());
        }

        groupedHUDs.add(newSettings);

        HUDComponent.getInstance().updateActiveHUDs();
    }

    public void unGroup(GroupedHUD groupedHUD) {
        List<AbstractHUD> huds = groupedHUD.huds;

        List<GroupedHUDSettings> groupedHUDs = Main.settings.hudList.groupedHuds;
        List<HUDId> individualHUDs = Main.settings.hudList.individualHudIds;

        for (AbstractHUD hud : huds) {
            individualHUDs.add(hud.getId());
        }

        groupedHUDs.remove(groupedHUD.groupSettings);

        HUDComponent.getInstance().updateActiveHUDs();
    }
}
