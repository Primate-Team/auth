package ru.samsonium.primate.auth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import ru.samsonium.primate.auth.DB;
import ru.samsonium.primate.auth.PrimateAuth;
import ru.samsonium.primate.auth.Profile;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class RegCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(text("Только игрок может выполнить эту комманду", GOLD));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(text("Не указан пароль", RED));
            return true;
        }

        String pass = strings[0];
        Player p = (Player) commandSender;
        Profile profile = DB.get().getPlayer(p.getUniqueId().toString());
        if (profile != null) {
            p.sendMessage(text("Такой игрок уже существует", GOLD));
            return true;
        }

        DB.get().addPlayer(p.getUniqueId().toString(), pass);
        p.setMetadata("auth", new FixedMetadataValue(PrimateAuth.get(), "ya"));
        p.sendMessage(text("Вы успешно зарегистрировались", GREEN));

        return true;
    }
}
