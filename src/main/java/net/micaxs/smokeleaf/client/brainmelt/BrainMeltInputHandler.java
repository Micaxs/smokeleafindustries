package net.micaxs.smokeleaf.client.brainmelt;

import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.effect.harmful.BrainMeltEffect;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
public final class BrainMeltInputHandler {

    private BrainMeltInputHandler() {}

    public static void onInputUpdate(MovementInputUpdateEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        MobEffectInstance inst = player.getEffect(ModEffects.BRAIN_MELT.get());
        if (inst != null) {
            // You can use inst.getAmplifier() if you want to scale the effect with potency

            // Use event input; if your version uses getMovementInput(), swap accordingly.
            Input input = player.input;

            // Invert movement axes
            input.forwardImpulse = -input.forwardImpulse;
            input.leftImpulse = -input.leftImpulse;

            // Swap jump and crouch
            boolean jump = input.jumping;
            boolean crouch = input.shiftKeyDown;
            input.jumping = crouch;
            input.shiftKeyDown = jump;
        }

    }
}