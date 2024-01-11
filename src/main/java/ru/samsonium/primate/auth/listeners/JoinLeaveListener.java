package ru.samsonium.primate.auth.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import ru.samsonium.primate.auth.DB;
import ru.samsonium.primate.auth.PrimateAuth;
import ru.samsonium.primate.auth.Profile;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.joinMessage(text().append(text(p.getName(), WHITE), text(" зашёл", YELLOW)).build());

        Profile profile = DB.get().getPlayer(p.getUniqueId().toString());
        if (profile == null) {
            p.sendMessage(text().append(text("Добро пожаловать! Зарегистрируйтесь с помощью команды: ", YELLOW),
                    text("/reg <", GRAY),
                    text("пароль", WHITE),
                    text(">", GRAY)));
        } else {
            if (profile.isInGame) {
                p.kick(text("Игрок с таким же ником уже играет на сервере"));
                return;
            }

            if (profile.isAuthorized()) {
                profile.updateLastLogin();
                p.sendMessage(text("С возвращением!", YELLOW));
                p.setMetadata("auth", new FixedMetadataValue(PrimateAuth.get(), "ya"));
                if (!profile.isInGame)
                    profile.setInGameState(true);

            } else p.sendMessage(text().append(text("С возвращением! Войдите с помощью команды: ", YELLOW),
                    text("/login <", GRAY),
                    text("пароль", WHITE),
                    text(">", GRAY)));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.quitMessage(text().append(text(p.getName(), WHITE), text(" вышел", YELLOW)).build());

        Profile profile = DB.get().getPlayer(p.getUniqueId().toString());
        if (profile == null) return;

        if (profile.isInGame)
            profile.setInGameState(false);
    }
}
