package com.solegendary.reignofnether.building.buildings.villagers;

import com.solegendary.reignofnether.building.*;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.research.researchItems.ResearchGolemSmithing;
import com.solegendary.reignofnether.research.researchItems.ResearchPillagerCrossbows;
import com.solegendary.reignofnether.research.researchItems.ResearchVindicatorAxes;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.tutorial.TutorialClientEvents;
import com.solegendary.reignofnether.tutorial.TutorialStage;
import com.solegendary.reignofnether.unit.units.villagers.IronGolemProd;
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

public class Blacksmith extends ProductionBuilding {

    public final static String structureName = "blacksmith";
    public final static ResourceCost cost = ResourceCosts.BLACKSMITH;

    public Blacksmith(Level level, BlockPos originPos, Rotation rotation, String ownerName) {
        super(level, originPos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, originPos, rotation), false);
        this.id = structureName;
        this.ownerName = ownerName;
        this.portraitBlock = Blocks.SMITHING_TABLE;
        this.icon = new ResourceLocation("minecraft", "textures/block/smithing_table_front.png");

        this.foodCost = cost.food;
        this.woodCost = cost.wood;
        this.oreCost = cost.ore;
        this.popSupply = cost.population;
        this.buildTimeModifier = 0.85f;

        this.startingBlockTypes.add(Blocks.OAK_PLANKS);
        this.startingBlockTypes.add(Blocks.COBBLESTONE);

        if (level.isClientSide())
            this.productionButtons = Arrays.asList(
                IronGolemProd.getStartButton(this, Keybindings.keyQ),
                ResearchVindicatorAxes.getStartButton(this, Keybindings.keyW),
                ResearchPillagerCrossbows.getStartButton(this, Keybindings.keyE),
                ResearchGolemSmithing.getStartButton(this, Keybindings.keyR)
            );
    }

    public Faction getFaction() {return Faction.VILLAGERS;}

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
                new ResourceLocation("minecraft", "textures/block/smithing_table_front.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Blacksmith.class,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.ATTACK_ENEMY_BASE),
                () -> BuildingClientEvents.hasFinishedBuilding(Barracks.structureName) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                () -> BuildingClientEvents.setBuildingToPlace(Blacksmith.class),
                null,
                tooltip,
                null
        );
    }
}
