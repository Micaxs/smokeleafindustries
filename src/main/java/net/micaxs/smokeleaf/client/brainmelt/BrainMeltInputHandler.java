package net.micaxs.smokeleaf.client.brainmelt;

import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

public final class BrainMeltInputHandler {

    private BrainMeltInputHandler() {}

    public static void onInputUpdate(MovementInputUpdateEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        MobEffectInstance inst = player.getEffect(ModEffects.BRAIN_MELT);
        if (inst != null) {
            // You can use inst.getAmplifier() if you want to scale the effect with potency
            ClientInput input = event.getInput();

            // Invert movement axes
            Vec2 move = input.getMoveVector();
            input.moveVector = new Vec2(-move.x, -move.y);

            // Invert the key presses too (they drive sprinting etc.), and swap jump and crouch
            Input keys = input.keyPresses;
            input.keyPresses = new Input(keys.backward(), keys.forward(), keys.right(), keys.left(),
                    keys.shift(), keys.jump(), keys.sprint());
        }

    }
}
