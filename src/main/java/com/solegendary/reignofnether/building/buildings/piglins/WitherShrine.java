package com.solegendary.reignofnether.building.buildings.piglins;

import com.solegendary.reignofnether.building.BuildingBlock;
import com.solegendary.reignofnether.building.BuildingBlockData;
import com.solegendary.reignofnether.building.BuildingClientEvents;
import com.solegendary.reignofnether.building.ProductionBuilding;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.research.researchItems.ResearchWitherClouds;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.util.Faction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.solegendary.reignofnether.building.BuildingUtils.getAbsoluteBlockData;

public class WitherShrine extends ProductionBuilding {

    public final static String structureName = "wither_shrine";
    public final static ResourceCost cost = ResourceCosts.WITHER_SHRINE;

    public WitherShrine(Level level, BlockPos originPos, Rotation rotation, String ownerName) {
        super(level, originPos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, originPos, rotation), false);
        this.id = structureName;
        this.ownerName = ownerName;
        this.portraitBlock = Blocks.GILDED_BLACKSTONE;
        this.icon = new ResourceLocation("minecraft", "textures/block/gilded_blackstone.png");

        this.foodCost = cost.food;
        this.woodCost = cost.wood;
        this.oreCost = cost.ore;
        this.popSupply = cost.population;

        this.canSetRallyPoint = false;

        this.startingBlockTypes.add(Blocks.POLISHED_BLACKSTONE_STAIRS);

        this.explodeChance = 0.2f;

        if (level.isClientSide())
            this.productionButtons = Arrays.asList(
                ResearchWitherClouds.getStartButton(this, Keybindings.keyQ)
            );
    }

    public Faction getFaction() {return Faction.PIGLINS;}

    public static ArrayList<BuildingBlock> getRelativeBlockData(LevelAccessor level) {
        return BuildingBlockData.getBuildingBlocks(structureName, level);
    }

    public static AbilityButton getBuildButton(Keybinding hotkey) {
        List<FormattedCharSequence> tooltip = new ArrayList<>(List.of(
                getKey(structureName).withStyle(Style.EMPTY.withBold(true)).getVisualOrderText(),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY)
        ));
        tooltip.addAll(getLore(structureName));
        return new AbilityButton(
            getKey(structureName),
            new ResourceLocation("minecraft", "textures/block/gilded_blackstone.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == WitherShrine.class,
            () -> false,
            () -> BuildingClientEvents.hasFinishedBuilding(Bastion.structureName) ||
                    ResearchClient.hasCheat("modifythephasevariance"),
            () -> BuildingClientEvents.setBuildingToPlace(WitherShrine.class),
            null,
            tooltip,
            null
        );
    }
}
