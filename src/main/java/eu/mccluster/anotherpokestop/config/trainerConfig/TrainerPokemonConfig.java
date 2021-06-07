package eu.mccluster.anotherpokestop.config.trainerConfig;

import eu.mccluster.dependency.configmanager.api.Config;
import eu.mccluster.dependency.configmanager.api.annotations.Order;

import java.io.File;

public class TrainerPokemonConfig extends Config {

    @Order(1)
    public String name = "Metagross";

    @Order(2)
    public String nickname = "Ubergross";

    @Order(3)
    public boolean shiny = true;

    @Order(4)
    public String nature = "Adamant";

    @Order(5)
    public int level = 100;

    @Order(6)
    public String item = "leftovers";

    @Order(7)
    public String ability = "Sturdy";

    @Order(8)
    public String growth = "Runt";

    @Order(9)
    public boolean canDynamax = true;

    @Order(10)
    public PokemonStatConfig ivs = new PokemonStatConfig(31, 31, 31 ,0, 31, 31);

    @Order(11)
    public PokemonStatConfig evs = new PokemonStatConfig(0, 252, 6, 0, 0, 252);

    @Order(12)
    public PokemonMoveConfig moves = new PokemonMoveConfig();

    @Override
    public File getFile() {
        return null;
    }
}