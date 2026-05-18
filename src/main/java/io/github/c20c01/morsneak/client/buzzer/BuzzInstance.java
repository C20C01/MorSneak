package io.github.c20c01.morsneak.client.buzzer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;

public class BuzzInstance extends AbstractTickableSoundInstance {
    private final Player player;
    private Vec2 offset = Vec2.ZERO;

    protected BuzzInstance(SoundEvent event, boolean looping, long seed) {
        super(event, SoundSource.PLAYERS, RandomSource.create(seed));
        player = Minecraft.getInstance().player;
        this.looping = looping;
        updatePosition();
    }

    /**
     * Create a new non-looping buzz instance with a random pitch.
     */
    protected BuzzInstance(SoundEvent event, float minPitch, float maxPitch, long seed) {
        super(event, SoundSource.PLAYERS, RandomSource.create(seed));
        player = Minecraft.getInstance().player;
        this.pitch = minPitch + (maxPitch - minPitch) * random.nextFloat();
        updatePosition();
    }

    public void setOffset(Vec2 offset) {
        this.offset = offset;
        updatePosition();
    }

    private void updatePosition() {
        this.x = (float) player.getX() + offset.x;
        this.y = (float) player.getY();
        this.z = (float) player.getZ() + offset.y;
    }

    @Override
    public void tick() {
        updatePosition();
    }
}
