package me.railplus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Locale;

public final class RailPlus extends JavaPlugin implements Listener {

    // Each bonus also gives a small push per block: push = bonus * BOOST_RATIO
    private static final double BOOST_RATIO = 0.15;

    private NamespacedKey baseKey;

    @Override
    public void onEnable() {
        baseKey = new NamespacedKey(this, "base_speed");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        // Once per second: make furnace cart fuel last longer
        getServer().getScheduler().runTaskTimer(this, this::fuelTick, 20L, 20L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(VehicleMoveEvent e) {
        if (!(e.getVehicle() instanceof Minecart cart)) return;
        Location f = e.getFrom(), t = e.getTo();
        // Only do work when the cart enters a new block (keeps it lag-free)
        if (f.getBlockX() == t.getBlockX() && f.getBlockY() == t.getBlockY() && f.getBlockZ() == t.getBlockZ()) return;
        apply(cart, true);
    }

    @EventHandler
    public void onEnter(VehicleEnterEvent e) {
        if (e.getVehicle() instanceof Minecart c) getServer().getScheduler().runTask(this, () -> apply(c, false));
    }

    @EventHandler
    public void onExit(VehicleExitEvent e) {
        if (e.getVehicle() instanceof Minecart c) getServer().getScheduler().runTask(this, () -> apply(c, false));
    }

    private void apply(Minecart cart, boolean moved) {
        var cfg = getConfig();
        var pdc = cart.getPersistentDataContainer();

        // Save the normal speed once so we always ADD to it
        Double base = pdc.get(baseKey, PersistentDataType.DOUBLE);
        if (base == null) {
            base = cart.getMaxSpeed();
            pdc.set(baseKey, PersistentDataType.DOUBLE, base);
        }

        double bonus = 0;

        if (cfg.getBoolean("rider") && hasPlayer(cart)) {
            bonus += cfg.getDouble("rider-speed");
        }

        if (cfg.getBoolean("blocks")) {
            switch (cart.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()) {
                case GOLD_BLOCK -> bonus += cfg.getDouble("gold-block");
                case IRON_BLOCK -> bonus += cfg.getDouble("iron-block");
                case REDSTONE_BLOCK -> bonus += cfg.getDouble("redstone-block");
                default -> { }
            }
        }

        if (cfg.getBoolean("names")) {
            Component n = cart.customName();
            if (n != null) {
                String name = PlainTextComponentSerializer.plainText().serialize(n).toLowerCase(Locale.ROOT).trim();
                switch (name) {
                    case "fast" -> bonus += cfg.getDouble("name-fast");
                    case "turbo" -> bonus += cfg.getDouble("name-turbo");
                    case "slow" -> bonus += cfg.getDouble("name-slow");
                    default -> { }
                }
            }
        }

        if (cfg.getBoolean("furnace") && cart instanceof PoweredMinecart pc && pc.getFuel() > 0) {
            bonus += cfg.getDouble("furnace-speed");
        }

        double max = Math.max(cfg.getDouble("max-speed", 1.5), base);
        double target = Math.max(0.05, Math.min(base + bonus, max));
        if (Math.abs(cart.getMaxSpeed() - target) > 0.001) cart.setMaxSpeed(target);

        if (moved && bonus != 0) {
            Vector v = cart.getVelocity();
            double len = v.length();
            if (len > 0.05) cart.setVelocity(v.multiply(Math.max(0, len + bonus * BOOST_RATIO) / len));
        }
    }

    private boolean hasPlayer(Minecart cart) {
        for (Entity p : cart.getPassengers()) if (p instanceof Player) return true;
        return false;
    }

    private void fuelTick() {
        var cfg = getConfig();
        double mult = cfg.getDouble("furnace-fuel", 3);
        if (!cfg.getBoolean("furnace") || mult <= 1) return;
        int give = (int) Math.round(20 * (1 - 1 / mult));
        for (World w : getServer().getWorlds()) {
            for (PoweredMinecart pc : w.getEntitiesByClass(PoweredMinecart.class)) {
                if (pc.getFuel() > 0) pc.setFuel(pc.getFuel() + give);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (a.length == 1 && a[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            s.sendMessage("RailPlus reloaded.");
        } else {
            s.sendMessage("Usage: /railplus reload");
        }
        return true;
    }
}
