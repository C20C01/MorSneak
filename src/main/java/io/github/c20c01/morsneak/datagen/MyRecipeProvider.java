package io.github.c20c01.morsneak.datagen;

import io.github.c20c01.morsneak.MorSneak;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = MorSneak.ID)
public class MyRecipeProvider extends RecipeProvider {
    public MyRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        var generator = event.getGenerator();
        generator.addProvider(true, new Runner(generator.getPackOutput(), event.getLookupProvider()));
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.REDSTONE, MorSneak.RADIO_RECEIVER_BLOCK.get())
                .define('R', Items.REDSTONE)
                .define('L', Items.LODESTONE)
                .pattern(" R ").pattern("RLR").pattern(" R ")
                .unlockedBy("has_compass", has(Items.COMPASS))
                .save(output);
        shaped(RecipeCategory.REDSTONE, MorSneak.RADIO_TRANSMITTER_BLOCK.get())
                .define('Q', Items.QUARTZ)
                .define('R', MorSneak.RADIO_RECEIVER_BLOCK.get())
                .pattern(" Q ").pattern("QRQ").pattern(" Q ")
                .unlockedBy("has_compass", has(Items.COMPASS))
                .save(output);
        shaped(RecipeCategory.TOOLS, MorSneak.PORTABLE_RADIO.get())
                .define('R', MorSneak.RADIO_RECEIVER_BLOCK.get())
                .define('C', Items.COMPASS)
                .define('T', MorSneak.RADIO_TRANSMITTER_BLOCK.get())
                .pattern(" R ").pattern(" C ").pattern(" T ")
                .unlockedBy("has_radio_receiver", has(MorSneak.RADIO_RECEIVER_BLOCK.get()))
                .save(output);
    }

    @SuppressWarnings("NullableProblems")
    private static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new MyRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return MorSneak.ID + " Recipes";
        }
    }
}