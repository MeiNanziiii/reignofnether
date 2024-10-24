package com.solegendary.reignofnether.building.buildings.monsters;

import com.solegendary.reignofnether.building.*;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.util.Faction;
import com.solegendary.reignofnether.util.MyRenderer;
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
import java.util.List;

import static com.solegendary.reignofnether.building.BuildingUtils.getAbsoluteBlockData;

public class PumpkinFarm extends Building {

    public final static String structureName = "pumpkin_farm";
    public final static ResourceCost cost = ResourceCosts.PUMPKIN_FARM;

    private static final int ICE_CHECK_TICKS_MAX = 100;
    private int ticksToNextIceCheck = ICE_CHECK_TICKS_MAX;

    public PumpkinFarm(Level level, BlockPos originPos, Rotation rotation, String ownerName) {
        super(level, originPos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, originPos, rotation), false);
        this.id = structureName;
        this.ownerName = ownerName;
        this.portraitBlock = Blocks.PUMPKIN;
        this.icon = new ResourceLocation("minecraft", "textures/block/pumpkin_side.png");

        this.foodCost = cost.food;
        this.woodCost = cost.wood;
        this.oreCost = cost.ore;
        this.popSupply = cost.population;

        this.startingBlockTypes.add(Blocks.DARK_OAK_LOG);

        this.explodeChance = 0;
    }

    public Faction getFaction() {return Faction.MONSTERS;}

    public static ArrayList<BuildingBlock> getRelativeBlockData(LevelAccessor level) {
        return BuildingBlockData.getBuildingBlocks(structureName, level);
    }

    public static AbilityButton getBuildButton(Keybinding hotkey) {
        List<FormattedCharSequence> tooltip = new ArrayList<>(List.of(
                getKey(structureName).withStyle(Style.EMPTY.withBold(true)).getVisualOrderText(),
                FormattedCharSequence.forward(Component.translatable("lore.reignofnether.farm", "\uE001  " + cost.wood + "  +  " + ResourceCosts.REPLANT_WOOD_COST).getString(), MyRenderer.iconStyle),
                FormattedCharSequence.forward("", Style.EMPTY)
        ));
        tooltip.addAll(getLore(structureName));
        return new AbilityButton(
                getKey(structureName),
                new ResourceLocation("minecraft", "textures/block/pumpkin_side.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == PumpkinFarm.class,
                () -> false,
                () -> BuildingClientEvents.hasFinishedBuilding(Mausoleum.structureName) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                () -> BuildingClientEvents.setBuildingToPlace(PumpkinFarm.class),
                null,
                tooltip,
                null
        );
    }

    @Override
    public void tick(Level tickLevel) {
        super.tick(tickLevel);
        if (!tickLevel.isClientSide()) {
            ticksToNextIceCheck -= 1;
            if (ticksToNextIceCheck <= 0) {
                for (BuildingBlock bb : blocks)
                    if (tickLevel.getBlockState(bb.getBlockPos()).getBlock() == Blocks.ICE)
                        tickLevel.setBlockAndUpdate(bb.getBlockPos(), Blocks.WATER.defaultBlockState());
                ticksToNextIceCheck = ICE_CHECK_TICKS_MAX;
            }
        }
    }
}
