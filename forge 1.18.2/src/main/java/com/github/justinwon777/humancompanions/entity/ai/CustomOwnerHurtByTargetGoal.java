package com.github.justinwon777.humancompanions.entity.ai;

import java.util.EnumSet;

import com.github.justinwon777.humancompanions.core.Config;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class CustomOwnerHurtByTargetGoal extends TargetGoal {
    private final TamableAnimal tameAnimal;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public CustomOwnerHurtByTargetGoal(TamableAnimal p_26107_) {
        super(p_26107_, false);
        this.tameAnimal = p_26107_;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit() && tameAnimal.getHealth() > 1.0F) {
            LivingEntity livingentity = this.tameAnimal.getOwner();
            if (livingentity == null) {
                return false;
            } else {
                this.ownerLastHurtBy = livingentity.getLastHurtByMob();
                if (this.ownerLastHurtBy instanceof TamableAnimal) {
                    if (((TamableAnimal) this.ownerLastHurtBy).isTame()) {
                        LivingEntity owner1 = ((TamableAnimal) this.ownerLastHurtBy).getOwner();
                        LivingEntity owner2 = this.tameAnimal.getOwner();
                        if (owner1 == owner2) {
                            if (!Config.FRIENDLY_FIRE_COMPANIONS.get()) {
                                return false;
                            }
                        }
                    }
                }
                int i = livingentity.getLastHurtByMobTimestamp();
                return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurtBy, livingentity);
            }
        } else {
            return false;
        }
    }

    public void start() {
        TextComponent text = new TextComponent("Owner was hurt! Protect the owner!");
        if (this.tameAnimal.isTame() && this.tameAnimal.getOwner() != null) {
            this.tameAnimal.getOwner().sendMessage(new TranslatableComponent("chat.type.text", this.tameAnimal.getDisplayName(), text),
                    this.tameAnimal.getUUID());
        }
        if (!this.tameAnimal.isOrderedToSit()) {
            this.mob.setTarget(this.ownerLastHurtBy);
            LivingEntity livingentity = this.tameAnimal.getOwner();
            if (livingentity != null) {
                this.timestamp = livingentity.getLastHurtByMobTimestamp();
            }
            super.start();
        }
    }

    public void stop() {
        this.mob.setTarget(null);
        this.targetMob = null;
        this.tameAnimal.setTarget(null);
        this.tameAnimal.setAggressive(false);
    }
}
