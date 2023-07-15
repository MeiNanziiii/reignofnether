package com.solegendary.reignofnether.research.researchItems;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.BuildingServerboundPacket;
import com.solegendary.reignofnether.building.BuildingUtils;
import com.solegendary.reignofnether.building.ProductionBuilding;
import com.solegendary.reignofnether.building.ProductionItem;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.research.ResearchServer;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.unit.UnitServerEvents;
import com.solegendary.reignofnether.unit.units.villagers.PillagerUnit;
import com.solegendary.reignofnether.util.MyRenderer;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class ResearchPillagerCrossbows extends ProductionItem {

    public final static String itemName = "Multishot Crossbows";
    public final static ResourceCost cost = ResourceCosts.RESEARCH_PILLAGER_CROSSBOWS;

    public ResearchPillagerCrossbows(ProductionBuilding building) {
        super(building, cost.ticks);
        this.onComplete = (Level level) -> {
            if (level.isClientSide())
                ResearchClient.addResearch(ResearchPillagerCrossbows.itemName);
            else {
                ResearchServer.addResearch(this.building.ownerName, ResearchPillagerCrossbows.itemName);
                for (LivingEntity unit : UnitServerEvents.getAllUnits())
                    if (unit instanceof PillagerUnit pUnit && pUnit.getOwnerName().equals(building.ownerName))
                        pUnit.setupEquipmentAndUpgradesServer();
            }
        };
        this.foodCost = cost.food;
        this.woodCost = cost.wood;
        this.oreCost = cost.ore;
    }

    public String getItemName() {
        return ResearchPillagerCrossbows.itemName;
    }

    public static Button getStartButton(ProductionBuilding prodBuilding, Keybinding hotkey) {
        return new Button(
                ResearchPillagerCrossbows.itemName,
                14,
                new ResourceLocation(ReignOfNether.MOD_ID, "textures/icons/items/crossbow.png"),
                new ResourceLocation(ReignOfNether.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                hotkey,
                () -> false,
                () -> ProductionItem.itemIsBeingProduced(ResearchPillagerCrossbows.itemName) ||
                        ResearchClient.hasResearch(ResearchPillagerCrossbows.itemName),
                () -> true,
                () -> BuildingServerboundPacket.startProduction(prodBuilding.originPos, itemName),
                null,
                List.of(
                        FormattedCharSequence.forward(ResearchPillagerCrossbows.itemName, Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedCostTime(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward("Enchants the crossbows of all pillagers with multishot", Style.EMPTY)
                )
        );
    }

    public Button getCancelButton(ProductionBuilding prodBuilding, boolean first) {
        return new Button(
                ResearchPillagerCrossbows.itemName,
                14,
                new ResourceLocation(ReignOfNether.MOD_ID, "textures/icons/items/crossbow.png"),
                new ResourceLocation(ReignOfNether.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                null,
                () -> false,
                () -> false,
                () -> true,
                () -> BuildingServerboundPacket.cancelProduction(prodBuilding.minCorner, itemName, first),
                null,
                null
        );
    }
}
