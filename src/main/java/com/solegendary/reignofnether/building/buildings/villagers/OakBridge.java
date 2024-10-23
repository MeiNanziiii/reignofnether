package com.solegendary.reignofnether.building.buildings.villagers;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.BuildingBlock;
import com.solegendary.reignofnether.building.BuildingBlockData;
import com.solegendary.reignofnether.building.BuildingClientEvents;
import com.solegendary.reignofnether.building.buildings.monsters.Mausoleum;
import com.solegendary.reignofnether.building.buildings.shared.AbstractBridge;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.tutorial.TutorialClientEvents;
import com.solegendary.reignofnether.tutorial.TutorialStage;
import net.minecraft.client.Minecraft;
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

public class OakBridge extends AbstractBridge {

    public final static String structureName = "bridge_oak";
    public final static String structureNameOrthogonal = "bridge_oak_orthogonal";
    public final static String structureNameDiagonal = "bridge_oak_diagonal";
    public final static ResourceCost cost = ResourceCosts.OAK_BRIDGE;

    public OakBridge(Level level, BlockPos originPos, Rotation rotation, String ownerName, boolean diagonal) {
        super(level, originPos, rotation, ownerName, diagonal,
                getCulledBlocks(getAbsoluteBlockData(getRelativeBlockData(level, diagonal), level, originPos, rotation), level));

        this.id = structureName;
        this.ownerName = ownerName;
        this.portraitBlock = Blocks.OAK_FENCE;
        this.icon = new ResourceLocation(ReignOfNether.MOD_ID, "textures/icons/blocks/oak_fence.png");

        this.foodCost = cost.food;
        this.woodCost = cost.wood;
        this.oreCost = cost.ore;
        this.popSupply = cost.population;
        this.buildTimeModifier = 1.0f;

        this.startingBlockTypes.add(Blocks.OAK_LOG);
    }

    public static ArrayList<BuildingBlock> getRelativeBlockData(LevelAccessor level, boolean diagonal) {
        return BuildingBlockData.getBuildingBlocks(diagonal ? structureNameDiagonal : structureNameOrthogonal, level);
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
                new ResourceLocation(ReignOfNether.MOD_ID, "textures/icons/blocks/oak_fence.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == OakBridge.class,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.BUILD_BRIDGE),
                () -> TutorialClientEvents.isAtOrPastStage(TutorialStage.BUILD_BRIDGE) &&
                       (BuildingClientEvents.hasFinishedBuilding(TownCentre.structureName) ||
                        BuildingClientEvents.hasFinishedBuilding(Mausoleum.structureName) ||
                        ResearchClient.hasCheat("modifythephasevariance")),
                () -> BuildingClientEvents.setBuildingToPlace(OakBridge.class),
                null,
                tooltip,
                null
        );
    }
}
