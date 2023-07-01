package com.github.justinwon777.humancompanions.entity.ai;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class CustomHurtByTargetGoal extends TargetGoal {
    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private static final int ALERT_RANGE_Y = 10;
    private boolean alertSameType;
    private int timestamp;
    private final Class<?>[] toIgnoreDamage;
    @Nullable
    private Class<?>[] toIgnoreAlert;

    public CustomHurtByTargetGoal(PathfinderMob pMob, Class<?>... pToIgnoreDamage) {
        super(pMob, true);
        this.toIgnoreDamage = pToIgnoreDamage;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        int hurtByMobTimestamp = this.mob.getLastHurtByMobTimestamp();
        LivingEntity lastHurtByMob = this.mob.getLastHurtByMob();
        if (hurtByMobTimestamp != this.timestamp && lastHurtByMob != null) {
            if (lastHurtByMob.getType() == EntityType.PLAYER || lastHurtByMob instanceof TamableAnimal) {
                return false;
            } else {
                Class[] var3 = this.toIgnoreDamage;
                int var4 = var3.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Class<?> $$2 = var3[var5];
                    if ($$2.isAssignableFrom(lastHurtByMob.getClass())) {
                        return false;
                    }
                }

                return this.canAttack(lastHurtByMob, HURT_BY_TARGETING);
            }
        } else {
            return false;
        }
    }

    public CustomHurtByTargetGoal setAlertOthers(Class<?>... pReinforcementTypes) {
        this.alertSameType = true;
        this.toIgnoreAlert = pReinforcementTypes;
        return this;
    }

    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
        if (this.alertSameType) {
            this.alertOthers();
        }

        super.start();
    }

    protected void alertOthers() {
        double $$0 = this.getFollowDistance();
        AABB $$1 = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate($$0, 10.0, $$0);
        List<? extends Mob> $$2 = this.mob.level.getEntitiesOfClass(this.mob.getClass(), $$1, EntitySelector.NO_SPECTATORS);
        Iterator var5 = $$2.iterator();

        while(true) {
            Mob $$3;
            boolean $$4;
            do {
                do {
                    do {
                        do {
                            do {
                                if (!var5.hasNext()) {
                                    return;
                                }

                                $$3 = (Mob)var5.next();
                            } while(this.mob == $$3);
                        } while($$3.getTarget() != null);
                    } while(this.mob instanceof TamableAnimal && ((TamableAnimal)this.mob).getOwner() != ((TamableAnimal)$$3).getOwner());
                } while($$3.isAlliedTo(this.mob.getLastHurtByMob()));

                if (this.toIgnoreAlert == null) {
                    break;
                }

                $$4 = false;
                Class[] var8 = this.toIgnoreAlert;
                int var9 = var8.length;

                for(int var10 = 0; var10 < var9; ++var10) {
                    Class<?> $$5 = var8[var10];
                    if ($$3.getClass() == $$5) {
                        $$4 = true;
                        break;
                    }
                }
            } while($$4);

            this.alertOther($$3, this.mob.getLastHurtByMob());
        }
    }

    protected void alertOther(Mob pMob, LivingEntity pTarget) {
        pMob.setTarget(pTarget);
    }
}

