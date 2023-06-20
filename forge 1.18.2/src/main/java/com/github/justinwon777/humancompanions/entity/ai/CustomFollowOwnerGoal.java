package com.github.justinwon777.humancompanions.entity.ai;

import com.github.justinwon777.humancompanions.entity.AbstractHumanCompanionEntity;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;

public class CustomFollowOwnerGoal extends FollowOwnerGoal {

    public AbstractHumanCompanionEntity companion;

    public CustomFollowOwnerGoal(AbstractHumanCompanionEntity pTamable, double pSpeedModifier, float pStartDistance, float pStopDistance, boolean pCanFly) {
        super(pTamable, pSpeedModifier, pStartDistance, pStopDistance, pCanFly);
        this.companion = pTamable;
    }

    public boolean canUse() {
        if (!companion.isFollowing()) {
            return false;
        }
        return super.canUse();
    }
}
