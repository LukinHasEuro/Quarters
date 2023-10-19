package net.earthmc.quarters.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;

public class Config {
    public static void init(FileConfiguration config) {
        config.options().setHeader(Collections.singletonList("Quarters"));

        config.addDefault("wand_material", "FLINT"); config.setInlineComments("wand_material", Collections.singletonList("Material of the wand item"));
        config.addDefault("current_selection_particle", "SCRAPE"); config.setInlineComments("current_selection_particle", Collections.singletonList("Particle outline of the currently selected area"));
        config.addDefault("current_cuboids_particle", "WAX_OFF"); config.setInlineComments("current_cuboids_particle", Collections.singletonList("Particle outline of current cuboids added to selection"));
        config.addDefault("max_quarter_volume", 131072); config.setInlineComments("max_quarter_volume", Collections.singletonList("Maximum volume of all cuboids in a quarter combined"));
        config.addDefault("max_quarters_per_town", 0); config.setInlineComments("max_quarters_per_town", Collections.singletonList("Maximum amount of quarters that can be in a single town, set to 0 for no limit"));
        config.addDefault("max_cuboids_per_quarter", 0); config.setInlineComments("max_cuboids_per_quarter", Collections.singletonList("Maximum amount of cuboids that can be in each quarter, set to 0 for no limit"));
        config.addDefault("ticks_between_outline_particles", 5); config.setInlineComments("ticks_between_outline_particles", Collections.singletonList("The number of ticks between when the particle outlines of quarters will appear"));
        config.addDefault("allow_constant_outlines", true); config.setInlineComments("allow_constant_outlines", Collections.singletonList("If set to true, players will be able to toggle quarter outlines to display constantly"));

        config.options().copyDefaults(true);
    }
}
