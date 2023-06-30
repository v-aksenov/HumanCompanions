package com.github.justinwon777.humancompanions.entity.ai;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CustomSetOrderedToSitGoal extends Goal {
    private final TamableAnimal tameAnimal;

    public CustomSetOrderedToSitGoal(TamableAnimal pMob) {
        this.tameAnimal = pMob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return this.tameAnimal.isOrderedToSit();
    }

    public boolean canUse() {
        if (!this.tameAnimal.isTame()) {
            return false;
        } else if (this.tameAnimal.isInWaterOrBubble()) {
            return false;
        } else if (!this.tameAnimal.isOnGround()) {
            return false;
        } else {
            return true;
        }
    }

    public void start() {
        this.tameAnimal.getNavigation().stop();
        this.tameAnimal.setInSittingPose(true);
        this.tameAnimal.setTarget(null);
        this.tameAnimal.setAggressive(false);
        this.tameAnimal.setInvulnerable(true);
        TextComponent text = new TextComponent("Start Sitting! Sitting = " + this.tameAnimal.isOrderedToSit());
        if (this.tameAnimal.isTame() && this.tameAnimal.getOwner() != null) {
            this.tameAnimal.getOwner().sendMessage(new TranslatableComponent("chat.type.text", this.tameAnimal.getDisplayName(), text),
                    this.tameAnimal.getUUID());
        }
    }

    public void stop() {
        this.tameAnimal.setInSittingPose(false);
        this.tameAnimal.setInvulnerable(false);
        TextComponent text = new TextComponent("Stop Sitting! Sitting = " + this.tameAnimal.isOrderedToSit());
        if (this.tameAnimal.isTame() && this.tameAnimal.getOwner() != null) {
            this.tameAnimal.getOwner().sendMessage(new TranslatableComponent("chat.type.text", this.tameAnimal.getDisplayName(), text),
                    this.tameAnimal.getUUID());
        }
    }
}
