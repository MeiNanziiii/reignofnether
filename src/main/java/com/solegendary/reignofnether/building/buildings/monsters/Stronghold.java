package com.solegendary.reignofnether.building.buildings.monsters;

import com.solegendary.reignofnether.building.*;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.unit.units.monsters.WardenProd;
import com.solegendary.reignofnether.util.Faction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.solegendary.reignofnether.building.BuildingUtils.getAbsoluteBlockData;

public class Stronghold extends ProductionBuilding implements GarrisonableBuilding {

    public final static String structureName = "stronghold";
    public final static ResourceCost cost = ResourceCosts.STRONGHOLD;
    public final static int nightRange = 60;

    private final static int MAX_OCCUPANTS = 7;

    public Stronghold(Level level, BlockPos originPos, Rotation rotation, String ownerName) {
        super(level, originPos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, originPos, rotation), false);
        this.id = structureName;
        this.ownerName = ownerName;
        this.portraitBlock = Blocks.REINFORCED_DEEPSLATE;
        this.icon = new ResourceLocation("minecraft", "textures/block/reinforced_deepslate_side.png");

        this.foodCost = cost.food;
        this.woodCost = cost.wood;
        this.oreCost = cost.ore;
        this.popSupply = cost.population;
        this.buildTimeModifier = 0.5f;

        this.startingBlockTypes.add(Blocks.POLISHED_BLACKSTONE);
        this.startingBlockTypes.add(Blocks.DEEPSLATE_TILE_SLAB);
        this.startingBlockTypes.add(Blocks.POLISHED_BLACKSTONE_WALL);
        this.startingBlockTypes.add(Blocks.DEEPSLATE);

        if (level.isClientSide())
            this.productionButtons = Arrays.asList(
                WardenProd.getStartButton(this, Keybindings.keyQ)
            );
    }

    public Faction getFaction() {return Faction.MONSTERS;}

    // don't use this for abilities as it may not be balanced
    public int getAttackRange() { return 30; }
    // bonus for units attacking garrisoned units
    public int getExternalAttackRangeBonus() { return 15; }

    public static ArrayList<BuildingBlock> getRelativeBlockData(LevelAccessor level) {
        return BuildingBlockData.getBuildingBlocks(structureName, level);
    }

    public boolean canDestroyBlock(BlockPos relativeBp) {
        return relativeBp.getY() != 13 &&
                relativeBp.getY() != 14;
    }

    public static AbilityButton getBuildButton(Keybinding hotkey) {
        List<FormattedCharSequence> tooltip = new ArrayList<>(List.of(
                getKey(structureName).withStyle(Style.EMPTY.withBold(true)).getVisualOrderText(),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY)
        ));
        tooltip.addAll(getLore(structureName, MAX_OCCUPANTS, nightRange));
        return new AbilityButton(
            getKey(structureName),
            new ResourceLocation("minecraft", "textures/block/reinforced_deepslate_side.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Stronghold.class,
            () -> false,
            () -> (BuildingClientEvents.hasFinishedBuilding(Graveyard.structureName) &&
                    BuildingClientEvents.hasFinishedBuilding(SpiderLair.structureName) &&
                    BuildingClientEvents.hasFinishedBuilding(Dungeon.structureName)) ||
                    ResearchClient.hasCheat("modifythephasevariance"),
            () -> BuildingClientEvents.setBuildingToPlace(Stronghold.class),
            null,
            tooltip,
            null
        );
    }

    @Override
    public BlockPos getIndoorSpawnPoint(ServerLevel level) {
        return this.originPos.offset(getExitPosition());
    }

    @Override
    public BlockPos getEntryPosition() {
        if (this.rotation == Rotation.NONE) {
            return new BlockPos(5,14,5);
        } else if (this.rotation == Rotation.CLOCKWISE_90) {
            return new BlockPos(-5,14,5);
        } else if (this.rotation == Rotation.CLOCKWISE_180) {
            return new BlockPos(-5,14,-5);
        } else {
            return new BlockPos(5,14,-5);
        }
    }

    @Override
    public BlockPos getExitPosition() {
        if (this.rotation == Rotation.NONE) {
            return new BlockPos(5,2,6);
        } else if (this.rotation == Rotation.CLOCKWISE_90) {
            return new BlockPos(-5,2,6);
        } else if (this.rotation == Rotation.CLOCKWISE_180) {
            return new BlockPos(-5,2,-6);
        } else {
            return new BlockPos(5,2,-6);
        }
    }

    @Override
    public boolean isFull() { return GarrisonableBuilding.getNumOccupants(this) >= MAX_OCCUPANTS; }
}
