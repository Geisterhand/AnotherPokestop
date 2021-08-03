package eu.mccluster.anotherpokestop.config.lang;

import eu.mccluster.dependency.configmanager.api.Config;
import eu.mccluster.dependency.configmanager.api.annotations.Comment;
import eu.mccluster.dependency.configmanager.api.annotations.Order;

import java.io.File;

public class LangDropMenu extends Config {

    @Order(1)
    @Comment("Headline of the DropMenu")
    public String header = "Pokestop-Drops";

    @Order(2)
    @Comment("Close Button of the DropMenu")
    public String buttonText = "Close";

    @Override
    public File getFile() {
        return null;
    }
}
