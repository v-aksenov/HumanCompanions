package com.github.justinwon777.humancompanions.entity.ai;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;

import java.util.EnumSet;

public class CustomOwnerHurtTargetGoal extends TargetGoal {
    private final TamableAnimal tameAnimal;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public CustomOwnerHurtTargetGoal(TamableAnimal p_26114_) {
        super(p_26114_, false);
        this.tameAnimal = p_26114_;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit() && tameAnimal.getHealth() > 1.0F) {
            LivingEntity livingentity = this.tameAnimal.getOwner();
            if (livingentity == null) {
                return false;
            } else {
                this.ownerLastHurt = livingentity.getLastHurtMob();
                if (this.ownerLastHurt instanceof TamableAnimal || ownerLastHurt != null && ownerLastHurt.getType() == EntityType.PLAYER) {
                    return false;
                } else if (this.ownerLastHurt instanceof Creeper || this.ownerLastHurt instanceof ArmorStand) {
                    return false;
                }
                int i = livingentity.getLastHurtMobTimestamp();
                return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurt, livingentity);
            }
        } else {
            return false;
        }
    }

    public void start() {
        TextComponent text = new TextComponent("Help owner!");
        if (this.tameAnimal.isTame() && this.tameAnimal.getOwner() != null) {
            this.tameAnimal.getOwner().sendMessage(new TranslatableComponent("chat.type.text", this.tameAnimal.getDisplayName(), text),
                    this.tameAnimal.getUUID());
        }
        if (!this.tameAnimal.isOrderedToSit()) {
            this.mob.setTarget(this.ownerLastHurt);
            LivingEntity livingentity = this.tameAnimal.getOwner();
            if (livingentity != null) {
                this.timestamp = livingentity.getLastHurtMobTimestamp();
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