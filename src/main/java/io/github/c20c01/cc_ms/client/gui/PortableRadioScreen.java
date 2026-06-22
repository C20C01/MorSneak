package io.github.c20c01.cc_ms.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.c20c01.cc_ms.MorSneak;
import io.github.c20c01.cc_ms.client.buzzer.BuzzInstance;
import io.github.c20c01.cc_ms.client.buzzer.RadioBuzzer;
import io.github.c20c01.cc_ms.network.PortableRadioUpdatePacket;
import io.github.c20c01.cc_ms.radio.RadioSignal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class PortableRadioScreen extends Screen {
    private static final Identifier RADIO_LOCATION = Identifier.fromNamespaceAndPath(MorSneak.ID, "textures/gui/portable_radio.png");
    private static final Component TITLE = Component.translatable(MorSneak.TEXT_KEY_PORTABLE_RADIO_TITLE);

    private static final int IMAGE_WIDTH = 176;
    private static final int IMAGE_HEIGHT = 170;

    private static final int DARK_GRAY = 0x888888;
    private static final int GRAY = 0xCCCCCC;
    private static final int GREEN = 0x00CC00;
    private static final int RED = 0xCC0000;

    private static final Component RADIO_LABEL_OFF = Component.literal("✔").withColor(GRAY).withoutShadow();
    private static final Component RADIO_LABEL_ON = Component.literal("✔").withColor(GREEN).withoutShadow();
    private static final Tooltip RADIO_LABEL_TOOLTIP = Tooltip.create(Component.translatable(MorSneak.TEXT_KEY_PORTABLE_RADIO_SELECT_MODE));
    private static final Component DELETE_LABEL_OFF = Component.literal("🗑").withColor(GRAY).withoutShadow();
    private static final Component DELETE_LABEL_ON = Component.literal("🗑").withColor(RED).withoutShadow();
    private static final Tooltip DELETE_LABEL_TOOLTIP = Tooltip.create(Component.translatable(MorSneak.TEXT_KEY_PORTABLE_RADIO_DELETE_MODE));
    private static final Component SOUND_LABEL_OFF = Component.literal("♪").withColor(GRAY).withoutShadow();
    private static final Component SOUND_LABEL_ON = Component.literal("♪").withColor(GREEN).withoutShadow();
    private static final Tooltip SOUND_LABEL_TOOLTIP = Tooltip.create(Component.translatable(MorSneak.TEXT_KEY_PORTABLE_RADIO_SOUND_MODE));
    private static final Component MIC_LABEL_OFF = Component.literal("🎙").withColor(RED).withoutShadow();
    private static final Component MIC_LABEL_ON = Component.literal("🎙").withColor(GREEN).withoutShadow();
    private static final Tooltip MIC_LABEL_TOOLTIP = Tooltip.create(Component.translatable(MorSneak.TEXT_KEY_PORTABLE_RADIO_MIC));
    private static final Component EMPTY_LABEL = Component.literal("-").withColor(DARK_GRAY);

    private static final Component SELECT_FREQUENCY_TOOLTIP_SUFFIX = Component.translatable(MorSneak.TEXT_KEY_PORTABLE_RADIO_SELECT).withColor(GREEN);
    private static final Component UNSELECT_FREQUENCY_TOOLTIP_SUFFIX = Component.translatable(MorSneak.TEXT_KEY_PORTABLE_RADIO_UNSELECT).withColor(RED);
    private static final Component DELETE_FREQUENCY_TOOLTIP_SUFFIX = Component.translatable(MorSneak.TEXT_KEY_PORTABLE_RADIO_DELETE).withColor(RED);
    private static final Component OTHER_RADIO_OPEN_TOOLTIP_SUFFIX = Component.translatable(MorSneak.TEXT_KEY_PORTABLE_RADIO_OTHER_OPEN).withColor(RED);

    private final LocalPlayer player;
    private final ItemStack radio;
    private final byte slot;
    private final boolean otherRadioOpen;


    enum Mode {
        RADIO, // select or unselect frequencies
        DELETE, // delete frequencies
        SOUND // play and select sounds
    }

    private Mode mode = Mode.RADIO;
    private int page = 0;
    private GlobalPos frequency;
    private byte signalCode;
    private byte soundCode;

    private final List<GlobalPos> frequencies;
    private List<Component> soundTitles;

    private BuzzInstance previewBuzz;

    private int left, top;

    private StringWidget posLabel;
    private StringWidget dimLabel;
    private StringWidget codeLabel;
    private final Button[] selectButtons = new Button[4];
    private SwitchButton radioButton;
    private SwitchButton micButton;
    private SwitchButton deleteButton;
    private SwitchButton soundButton;

    public static void open(Player player, ItemStack radio, byte slot) {
        Minecraft.getInstance().setScreen(new PortableRadioScreen((LocalPlayer) player, radio, slot));
    }

    public PortableRadioScreen(LocalPlayer player, ItemStack radio, byte slot) {
        super(TITLE);
        this.player = player;
        this.radio = radio;
        this.slot = slot;
        this.otherRadioOpen = getOtherRadioOpen();
        this.frequencies = new ArrayList<>(radio.getOrDefault(MorSneak.FREQUENCIES, List.of()));
        this.signalCode = radio.getOrDefault(MorSneak.SIGNAL_CODE, RadioSignal.CODE_EMPTY);
        this.soundCode = radio.getOrDefault(MorSneak.SOUND_CODE, (byte) 0);
        this.frequency = otherRadioOpen ? null : radio.get(MorSneak.SELECTED_FREQUENCY);
    }

    private boolean getOtherRadioOpen() {
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack.is(MorSneak.PORTABLE_RADIO) && itemStack != radio && itemStack.get(MorSneak.SELECTED_FREQUENCY) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void init() {
        left = (this.width - IMAGE_WIDTH) / 2;
        top = (this.height - IMAGE_HEIGHT) / 2;

        int x, y;

        // info label
        x = left + 10;
        y = top + 20;
        posLabel = new StringWidget(x, y, 154, 11, EMPTY_LABEL, minecraft.font);
        posLabel.setMaxWidth(154);
        addRenderableWidget(posLabel);
        y += 11;
        dimLabel = new StringWidget(x, y, 134, 11, EMPTY_LABEL, minecraft.font);
        dimLabel.setMaxWidth(134);
        addRenderableWidget(dimLabel);
        x += 144;
        codeLabel = new StringWidget(x, y, 11, 11, EMPTY_LABEL, minecraft.font);
        addRenderableWidget(codeLabel);

        // select buttons
        x = left + 8;
        y = top + 52;
        for (byte i = 0; i < 4; i++) {
            byte finalI = i;
            NoFocusButton button = new NoFocusButton(x, y + i * 24, 136, 24, EMPTY_LABEL, _ -> selectButtonPressed(finalI));
            button.active = false;
            selectButtons[i] = button;
            addRenderableWidget(button);
        }

        // arrow buttons
        x = left + 36;
        y = top + 151;
        addRenderableWidget(new ArrowButton(x, y, 20, 12, _ -> onLeftArrowPressed(), true));
        x += 60;
        addRenderableWidget(new ArrowButton(x, y, 20, 12, _ -> onRightArrowPressed(), false));

        // control buttons
        x = left + 153;
        y = top + 56;
        radioButton = new SwitchButton(x, y, 16, 16, RADIO_LABEL_OFF, RADIO_LABEL_ON, _ -> setMode(Mode.RADIO));
        radioButton.setTooltip(RADIO_LABEL_TOOLTIP);
        addRenderableWidget(radioButton);

        y += 24;
        deleteButton = new SwitchButton(x, y, 16, 16, DELETE_LABEL_OFF, DELETE_LABEL_ON, _ -> setMode(Mode.DELETE));
        deleteButton.setTooltip(DELETE_LABEL_TOOLTIP);
        addRenderableWidget(deleteButton);

        y += 24;
        soundButton = new SwitchButton(x, y, 16, 16, SOUND_LABEL_OFF, SOUND_LABEL_ON, _ -> setMode(Mode.SOUND));
        soundButton.setTooltip(SOUND_LABEL_TOOLTIP);
        addRenderableWidget(soundButton);

        y += 24;
        micButton = new SwitchButton(x, y, 16, 16, MIC_LABEL_OFF, MIC_LABEL_ON, this::onMicButtonPressed);
        micButton.setTooltip(MIC_LABEL_TOOLTIP);
        addRenderableWidget(micButton);

        // initialize widgets
        updateWidgets();
    }

    private void setMode(Mode mode) {
        if (this.mode == mode) {
            if (mode == Mode.SOUND) updatePreviewSound(false);
            return;
        }

        if (this.mode == Mode.SOUND) {
            // sound -> other
            updatePreviewSound(false);
            this.page = 0;
        } else if (mode == Mode.SOUND) {
            // other -> sound
            this.page = 0;
        }

        this.mode = mode;
        updateWidgets();
    }

    private void updateWidgets() {
        radioButton.setOn(mode == Mode.RADIO);
        deleteButton.setOn(mode == Mode.DELETE);
        soundButton.setOn(mode == Mode.SOUND);
        micButton.setOn(signalCode != RadioSignal.CODE_EMPTY);
        updateSelectButtons();
        updateInfo();
    }

    private void updateSelectButtons() {
        switch (mode) {
            case RADIO -> updateFrequencyButtons(false);
            case DELETE -> updateFrequencyButtons(true);
            case SOUND -> updateSoundButtons();
        }
    }

    private void selectButtonPressed(byte index) {
        byte absoluteIndex = (byte) (page * 4 + index);
        switch (mode) {
            case RADIO -> {
                selectFrequency(absoluteIndex);
                updateFrequencyButtons(false);
            }
            case DELETE -> {
                removeFrequency(absoluteIndex);
                updateFrequencyButtons(true);
            }
            case SOUND -> {
                setSoundCode(absoluteIndex);
                if (signalCode != RadioSignal.CODE_EMPTY) setSignalCode((byte) (soundCode + 1));
                updatePreviewSound(true);
                updateSoundButtons();
            }
        }
        updateInfo();
    }

    private void selectFrequency(byte index) {
        if (index < 0 || index >= frequencies.size()) {
            setFrequency(null, (byte) -1);
            return;
        }

        GlobalPos frequency = frequencies.get(index);
        if (frequency.equals(this.frequency)) {
            setFrequency(null, (byte) -1);
            return;
        }

        if (otherRadioOpen) {
            assert minecraft.level != null;
            minecraft.level.playLocalSound(player, SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 1.0F, 1.0F);
            return;
        }

        setFrequency(frequency, index);
    }

    private void removeFrequency(byte index) {
        if (index >= frequencies.size()) return;
        GlobalPos removed = frequencies.remove(index);
        if (page == getTotalPages()) page--;
        if (removed.equals(frequency)) setFrequency(null, (byte) -1);
        radio.set(MorSneak.FREQUENCIES, frequencies);
        sendUpdates(PortableRadioUpdatePacket.REMOVE_FREQUENCY, index);
    }

    private void updatePreviewSound(boolean play) {
        SoundManager manager = minecraft.getSoundManager();
        if (previewBuzz != null) {
            manager.stop(previewBuzz);
            previewBuzz = null;
        }
        if (play) {
            previewBuzz = RadioBuzzer.getInstance().getBuzzFactory().get((byte) (soundCode + 1), player.getRandom().nextLong());
            manager.play(previewBuzz);
        }
    }

    private void onMicButtonPressed(Button button) {
        if (signalCode == RadioSignal.CODE_EMPTY) {
            // off -> on
            setSignalCode((byte) (soundCode + 1));
            ((SwitchButton) button).setOn(true);
        } else {
            // on -> off
            setSignalCode(RadioSignal.CODE_EMPTY);
            ((SwitchButton) button).setOn(false);
        }
        updateInfo();
    }

    private void updateFrequencyButtons(boolean deleteMode) {
        int startIndex = page * 4;
        for (int i = 0; i < 4; i++) {
            updateFrequencyButtons(selectButtons[i], startIndex + i, deleteMode);
        }
    }

    private void updateFrequencyButtons(Button button, int index, boolean deleteMode) {
        if (index >= frequencies.size()) {
            button.setMessage(EMPTY_LABEL);
            button.setTooltip(null);
            button.active = false;
            return;
        }

        GlobalPos frequency = frequencies.get(index);
        MutableComponent message = Component.literal(frequency.pos().toShortString());
        MutableComponent tooltipText = Component.literal(frequency.pos().toShortString() + "\n")
                .append(Component.literal(frequency.dimension().identifier() + "\n\n").withColor(DARK_GRAY));
        if (frequency.equals(this.frequency)) {
            message.withColor(GREEN);
            if (deleteMode) {
                tooltipText.append(DELETE_FREQUENCY_TOOLTIP_SUFFIX);
            } else {
                tooltipText.append(UNSELECT_FREQUENCY_TOOLTIP_SUFFIX);
            }
        } else {
            if (deleteMode) {
                tooltipText.append(DELETE_FREQUENCY_TOOLTIP_SUFFIX);
            } else {
                if (otherRadioOpen) {
                    tooltipText.append(OTHER_RADIO_OPEN_TOOLTIP_SUFFIX);
                } else {
                    tooltipText.append(SELECT_FREQUENCY_TOOLTIP_SUFFIX);
                }
            }
        }

        button.setMessage(message);
        button.setTooltip(Tooltip.create(tooltipText));
        button.active = true;
    }

    private void updateSoundButtons() {
        soundTitles = RadioBuzzer.getInstance().getBuzzFactory().getSoundTitles();
        int startIndex = page * 4;
        for (int i = 0; i < 4; i++) {
            updateSoundButtons(selectButtons[i], startIndex + i);
        }
    }

    private void updateSoundButtons(Button button, int index) {
        if (index >= soundTitles.size()) {
            button.setMessage(EMPTY_LABEL);
            button.setTooltip(null);
            button.active = false;
            return;
        }

        button.setMessage(index == soundCode ? soundTitles.get(index).copy().withColor(GREEN) : soundTitles.get(index));
        button.setTooltip(null);
        button.active = true;
    }

    private void onLeftArrowPressed() {
        if (this.page <= 0) return;
        this.page--;
        updateSelectButtons();
    }

    private void onRightArrowPressed() {
        if (this.page >= getTotalPages() - 1) return;
        this.page++;
        updateSelectButtons();
    }

    private int getTotalPages() {
        int size = switch (mode) {
            case RADIO, DELETE -> frequencies.size();
            case SOUND -> soundTitles.size();
        };
        return (size - 1) / 4 + 1;
    }

    private void updateInfo() {
        MutableComponent codeComponent = Component.literal(String.format("%02d", soundCode + 1));
        if (frequency == null) {
            posLabel.setMessage(EMPTY_LABEL);
            dimLabel.setMessage(EMPTY_LABEL);
            codeLabel.setMessage(codeComponent.withColor(DARK_GRAY));
        } else {
            posLabel.setMessage(Component.literal(frequency.pos().toShortString()));
            dimLabel.setMessage(Component.literal(frequency.dimension().identifier().toShortString()).withColor(DARK_GRAY));
            if (signalCode == RadioSignal.CODE_EMPTY) {
                codeLabel.setMessage(codeComponent.withColor(RED));
            } else {
                codeLabel.setMessage(codeComponent.withColor(GREEN));
            }
        }
    }

    private void setSoundCode(byte soundCode) {
        if (this.soundCode == soundCode) return;
        this.soundCode = soundCode;
        radio.set(MorSneak.SOUND_CODE, soundCode);
        sendUpdates(PortableRadioUpdatePacket.UPDATE_SOUND_CODE, soundCode);
    }

    private void setSignalCode(byte signalCode) {
        if (this.signalCode == signalCode) return;
        this.signalCode = signalCode;
        radio.set(MorSneak.SIGNAL_CODE, signalCode);
        sendUpdates(PortableRadioUpdatePacket.UPDATE_SIGNAL_CODE, signalCode);
    }

    /**
     * @param index the index of the frequency to select, or -1 to deselect the current frequency
     */
    private void setFrequency(@Nullable GlobalPos frequency, byte index) {
        this.frequency = frequency;
        radio.set(MorSneak.SELECTED_FREQUENCY, frequency);
        sendUpdates(PortableRadioUpdatePacket.SELECT_FREQUENCY, index);
    }

    private void sendUpdates(byte operation, byte data) {
        player.connection.send(new PortableRadioUpdatePacket(slot, operation, data));
    }

    @Override
    public void onClose() {
        updatePreviewSound(false);
        super.onClose();
    }

    public boolean keyPressed(KeyEvent event) {
        var key = InputConstants.getKey(event);
        if (super.keyPressed(event)) {
            return true;
        } else if (this.minecraft.options.keyInventory.isActiveAndMatches(key)) {
            this.onClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        Component pageNumbers = Component.literal(page + 1 + "/" + getTotalPages());
        graphics.text(minecraft.font, pageNumbers, left + 76 - minecraft.font.width(pageNumbers) / 2, top + 153, 0xFF393939, false);
        graphics.text(minecraft.font, title, left + 7, top + 7, 0xFF000000, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, RADIO_LOCATION, left, top, 0.0F, 0.0F, IMAGE_WIDTH, IMAGE_HEIGHT, 256, 256);
    }
}
