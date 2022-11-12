package com.nekozouneko.virtualchest.cmd;

import com.google.gson.Gson;
import com.nekozouneko.virtualchest.VirtualChest;
import com.nekozouneko.virtualchest.files.SaveData;
import com.nekozouneko.virtualchest.files.VChestData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCmd implements CommandExecutor, TabCompleter {

    private VirtualChest instance = VirtualChest.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(args.length == 0)) {
            if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
                runHelpCmd(sender, command, label, args);
            } else if (args[0].equalsIgnoreCase("add")) {
                runAddCmd(sender, command, label, args);
            } else if (args[0].equalsIgnoreCase("list")) {
                runListCmd(sender, command, label, args);
            } else if (args[0].equalsIgnoreCase("clean")) {
                runCleanCmd(sender, command, label, args);
            } else if (args[0].equalsIgnoreCase("open")) {
                runOpenCmd(sender, command, label, args);
            } else if (args[0].equalsIgnoreCase("remove")) {
                runRemoveCmd(sender, command, label, args);
            } else {
                sender.sendMessage(ChatColor.RED + "そのようなサブコマンドは存在していません。");
            }
        } else {
            runHelpCmd(sender, command, label, args);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabList = new ArrayList<>();

        if (args.length == 1) {
            List<String> defaultArgs = Arrays.asList("?", "add", "clean", "help", "list", "open", "remove");

            for (String arg : defaultArgs) {
                if (arg.toLowerCase().startsWith(args[0].toLowerCase())) {
                    tabList.add(arg);
                }
            }

            return tabList;
        }

        return new ArrayList<>();
    }

    public void runHelpCmd(CommandSender s, Command c, String l, String[] a) {
        CommandSender.Spigot sender = s.spigot();

        BaseComponent info = new TextComponent(
                ChatColor.RESET + "> " + ChatColor.AQUA +"仮想チェスト" + ChatColor.WHITE + " - 使用方法 " + ChatColor.GRAY + "(/" + l + ")"
        );

        BaseComponent helpCmd = new TextComponent(
                ChatColor.RESET + "・" + ChatColor.GRAY + "/" + l + " ? " + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + " " + ChatColor.WHITE + " 仮想チェストの使い方を表示します。"
        );

        BaseComponent addCmd = new TextComponent(
                ChatColor.RESET + "・" + ChatColor.GRAY + "/" + l + " add " + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + " " + ChatColor.WHITE + " 仮想チェストを購入します。"
        );

        BaseComponent listCmd = new TextComponent(
                ChatColor.RESET + "・" + ChatColor.GRAY + "/" + l + " list " + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + " " + ChatColor.WHITE + " 仮想チェストのリストを表示します。"
        );

        BaseComponent openCmd = new TextComponent(
                ChatColor.RESET + "・" + ChatColor.GRAY + "/" + l + " open " +  ChatColor.AQUA+ "<id> " + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + " " + ChatColor.WHITE + " 仮想チェストを開きます。"
        );

        BaseComponent removeCmd = new TextComponent(
                ChatColor.RESET + "・" + ChatColor.GRAY + "/" + l + " remove " + ChatColor.AQUA + "<id> " + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + " " + ChatColor.WHITE + " 仮想チェストを削除します。"
        );

        BaseComponent cleanCmd = new TextComponent(
                ChatColor.RESET + "・" + ChatColor.GRAY + "/" + l + " clean " + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + " " + ChatColor.WHITE + " 仮想チェストを全部削除します。"
        );

        helpCmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+l+" ?"));
        helpCmd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY+"/"+l+" ?\n" + ChatColor.WHITE + "仮想チェストの使い方を表示します。")));

        addCmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+l+" add"));
        addCmd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY+"/"+l+" add\n" + ChatColor.WHITE + "仮想チェストを購入します。")));

        listCmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+l+" list"));
        listCmd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY+"/"+l+" list\n" + ChatColor.WHITE + "仮想チェストのリストを表示します。")));

        openCmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+l+" open <id>"));
        openCmd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY+"/"+l+" menu " + ChatColor.AQUA + "<id>\n" + ChatColor.WHITE + "仮想チェストを開きます。")));

        removeCmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+l+" remove <id>"));
        removeCmd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY+"/"+l+" remove "+ ChatColor.AQUA + "<id>" +"\n" + ChatColor.WHITE + "仮想チェストを削除します。")));

        cleanCmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+l+" clean"));
        cleanCmd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY+"/"+l+" clean\n" + ChatColor.WHITE + "仮想チェストを全部削除します。")));

        sender.sendMessage(info);
        sender.sendMessage(helpCmd);
        sender.sendMessage(addCmd);
        sender.sendMessage(cleanCmd);
        sender.sendMessage(listCmd);
        sender.sendMessage(openCmd);
        sender.sendMessage(removeCmd);
    }

    public void runAddCmd(CommandSender s, Command c, String l, String[] a) {
        Player player;
        try {
            player = (Player) s;
        } catch (ClassCastException e) {
            s.sendMessage(ChatColor.RED + "プレイヤーとしてのみ実行可能です。");

            return;
        }

        Gson gson = new Gson();

        File data_dir = new File(instance.getDataFolder(), "data/");

        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdir();
        }

        if (!data_dir.exists()) {
            data_dir.mkdir();
        }

        Path dataFile = Paths.get(data_dir.toString(), player.getUniqueId()+".json");

        BufferedReader reader;
        try {
            try {
                Files.createFile(dataFile);
            } catch (FileAlreadyExistsException ignored) {}
            reader = Files.newBufferedReader(dataFile);
        } catch (IOException ignored) {
            s.sendMessage(ChatColor.RED + "プレイヤーデータの読み込みに失敗しました。");
            return;
        }

        SaveData data = gson.fromJson(reader, SaveData.class);
        if (data == null) {
            data = new SaveData(player.getUniqueId().toString());
        }

        if (!(instance.getConfig().getString("buy-type").equalsIgnoreCase("vault"))) {

            long ingots = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == Material.GOLD_INGOT) {
                    ingots += item.getAmount();
                }
            }

            int buy = 32;
            int buy2 = 0;

            if (ingots >= 32) {
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() == Material.GOLD_INGOT && !(buy == 0)) {
                        if (item.getAmount() >= buy) {
                            buy2 = item.getAmount() - buy;
                            buy = 0;
                            item.setAmount(buy2);
                        } else {
                            buy2 = buy - item.getAmount();
                            buy -= buy2;
                            item.setAmount(0);
                        }
                    }
                }
            } else {
                s.sendMessage(ChatColor.RED + "金インゴットが不足しています。");
                return;
            }
        } else {
            Economy eco = VirtualChest.getVE();
            double price = instance.getConfig().getDouble("vault.price");

            if (eco.getBalance(player) >= price) {
                eco.withdrawPlayer(player, price);
            } else {
                player.sendMessage("> §a仮想チェスト §c最低でも" + price + eco.currencyNameSingular()+"必要です");
                return;
            }
        }

        Inventory emptyChest = Bukkit.createInventory(null, 27, "VirtualChest-temp");

        ByteArrayOutputStream bytes;
        BukkitObjectOutputStream out;
        try {
            bytes = new ByteArrayOutputStream();
            out = new BukkitObjectOutputStream(bytes);

            out.writeObject(emptyChest.getContents());
        } catch (IOException e) {
            e.printStackTrace();

            return;
        }

        VChestData vcd = new VChestData(Base64Coder.encodeLines(bytes.toByteArray()));
        List<VChestData> chests =  data.getChests() ;
        List<VChestData> cd = new ArrayList<>(chests);
        cd.add(vcd);

        data.setChests(cd);

        try {
            PrintWriter writer = new PrintWriter(dataFile.toString());

            gson.toJson(data, SaveData.class, writer);

            writer.flush();
            writer.close();
        } catch (IOException e) {
            s.sendMessage(ChatColor.RED + "書き込みに失敗しました。");
        }

        s.sendMessage(ChatColor.GREEN + "購入しました。");
    }

    public void runListCmd(CommandSender s, Command c, String l, String[] a) {
        Player player;
        try {
            player = (Player) s;
        } catch (ClassCastException e) {
            s.sendMessage(ChatColor.RED + "プレイヤーとしてのみ実行可能です。");

            return;
        }

        int i = 1;

        Gson gson = new Gson();

        File data_dir = new File(instance.getDataFolder(), "data/");

        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdir();
        }

        if (!data_dir.exists()) {
            data_dir.mkdir();
        }

        Path dataFile = Paths.get(data_dir.toString(), player.getUniqueId()+".json");

        BufferedReader reader;
        try {
            try {
                Files.createFile(dataFile);
            } catch (FileAlreadyExistsException ignored) {}
            reader = Files.newBufferedReader(dataFile);
        } catch (IOException ignored) {
            s.sendMessage(ChatColor.RED + "プレイヤーデータの読み込みに失敗しました。");
            ignored.printStackTrace();
            return;
        }

        SaveData data = gson.fromJson(reader, SaveData.class);

        if (data == null) {
            data = new SaveData(player.getUniqueId().toString());
        }

        s.sendMessage("> " + ChatColor.AQUA + "仮想チェスト " + ChatColor.WHITE + ChatColor.STRIKETHROUGH + " " + ChatColor.WHITE + " 仮想チェストリスト");

        for (VChestData d : data.getChests()) {
            ItemStack[] contents;

            try {
                ByteArrayInputStream bytes = new ByteArrayInputStream(Base64Coder.decodeLines(d.getContent()));
                BukkitObjectInputStream in = new BukkitObjectInputStream(bytes);

                contents = (ItemStack[]) in.readObject();
            } catch (IOException | ClassNotFoundException | ClassCastException er) {
                s.sendMessage(ChatColor.RED + "チェストのデータの読み込みに失敗しました。");
                er.printStackTrace();
                i++;
                continue;
            }

            int counts = 0;

            for (ItemStack stack : contents) {
                if (stack != null) {
                    counts += stack.getAmount();
                }
            }

            s.sendMessage("#"+i+" "+counts+"個のアイテムが格納済み");

            i++;
        }
    }

    public void runCleanCmd(CommandSender s, Command c, String l, String[] a) {
        Player player;
        try {
            player = (Player) s;
        } catch (ClassCastException e) {
            s.sendMessage(ChatColor.RED + "プレイヤーとしてのみ実行可能です。");

            return;
        }

        Gson gson = new Gson();

        File data_dir = new File(instance.getDataFolder(), "data/");

        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdir();
        }

        if (!data_dir.exists()) {
            data_dir.mkdir();
        }

        Path dataFile = Paths.get(data_dir.toString(), player.getUniqueId()+".json");

        BufferedReader reader;
        try {
            try {
                Files.createFile(dataFile);
            } catch (FileAlreadyExistsException ignored) {}
            reader = Files.newBufferedReader(dataFile);
        } catch (IOException er) {
            s.sendMessage(ChatColor.RED + "プレイヤーデータの読み込みに失敗しました。");
            return;
        }

        SaveData data = gson.fromJson(reader, SaveData.class);

        if (data == null) {
            s.sendMessage(ChatColor.RED + "すでにリセット済みです。");
            return;
        }

        if (a.length <= 1 || !a[1].equalsIgnoreCase("confirm")) {
            s.sendMessage(ChatColor.RED + "本当に仮想チェストを全部削除しますか? 一度削除したら元に戻すことはできません。");
            s.sendMessage(ChatColor.RED + "本当に消す場合は" + ChatColor.GRAY + "/" + l + " clean confirm" + ChatColor.RED + "を実行してください");
        } else {
            try {
                PrintWriter writer = new PrintWriter(dataFile.toString());

                gson.toJson(new SaveData(player.getUniqueId().toString()), SaveData.class, writer);

                writer.flush();
                writer.close();

                s.sendMessage(ChatColor.GREEN + "リセットが完了しました。");
            } catch (IOException e) {
                s.sendMessage(ChatColor.RED + "書き込みに失敗しました。");
            }
        }
    }

    public void runOpenCmd(CommandSender s, Command c, String l, String[] a) {
        Player player;
        try {
            player = (Player) s;
        } catch (ClassCastException e) {
            s.sendMessage(ChatColor.RED + "プレイヤーとしてのみ実行可能です。");

            return;
        }

        Gson gson = new Gson();

        File data_dir = new File(instance.getDataFolder(), "data/");

        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdir();
        }

        if (!data_dir.exists()) {
            data_dir.mkdir();
        }

        Path dataFile = Paths.get(data_dir.toString(), player.getUniqueId()+".json");

        BufferedReader reader;
        try {
            try {
                Files.createFile(dataFile);
            } catch (FileAlreadyExistsException ignored) {}
            reader = Files.newBufferedReader(dataFile);
        } catch (IOException er) {
            s.sendMessage(ChatColor.RED + "プレイヤーデータの読み込みに失敗しました。");
            return;
        }

        SaveData data = gson.fromJson(reader, SaveData.class);

        if (data == null) {
            s.sendMessage(ChatColor.RED + "データが存在していないため開くことができません。");
            return;
        }

        if (a.length == 1) {
            s.sendMessage(ChatColor.GRAY + "/"+l+" list" + ChatColor.RED + "でIDを確認することができます。");
            return;
        }

        int i;
        try {
            i = Integer.parseInt(a[1].replaceAll("#", ""));
        } catch (NumberFormatException er) {
            s.sendMessage(ChatColor.RED + "数字として読み込めませんでした。");
            return;
        }

        if (data.getChests().size() < i) {
            s.sendMessage(ChatColor.RED + "そのIDのチェストは存在していません。");
            return;
        }

        try {
            VChestData chest = data.getChests().get(i-1);
            ByteArrayInputStream bytes = new ByteArrayInputStream(Base64Coder.decodeLines(chest.getContent()));
            BukkitObjectInputStream in = new BukkitObjectInputStream(bytes);

            Inventory temp = Bukkit.createInventory(null, 27, "仮想チェスト #"+i);

            temp.setContents((ItemStack[]) in.readObject());

            player.openInventory(temp);
        } catch (IOException | ClassNotFoundException  er) {
            s.sendMessage(ChatColor.RED + "読み書きでエラーが発生しました。");
        } catch (IndexOutOfBoundsException er) {
            s.sendMessage(ChatColor.RED + "そのようなチェストが見つかりませんでした。");
        }
    }

    public void runRemoveCmd(CommandSender s, Command c, String l, String[] a) {
        Player player;
        try {
            player = (Player) s;
        } catch (ClassCastException e) {
            s.sendMessage(ChatColor.RED + "プレイヤーとしてのみ実行可能です。");

            return;
        }

        Gson gson = new Gson();

        File data_dir = new File(instance.getDataFolder(), "data/");

        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdir();
        }

        if (!data_dir.exists()) {
            data_dir.mkdir();
        }

        Path dataFile = Paths.get(data_dir.toString(), player.getUniqueId()+".json");

        BufferedReader reader;
        try {
            try {
                Files.createFile(dataFile);
            } catch (FileAlreadyExistsException ignored) {}
            reader = Files.newBufferedReader(dataFile);
        } catch (IOException er) {
            s.sendMessage(ChatColor.RED + "プレイヤーデータの読み込みに失敗しました。");
            return;
        }

        SaveData data = gson.fromJson(reader, SaveData.class);

        if (data == null) {
            s.sendMessage(ChatColor.RED + "データが存在していないため開くことができません。");
            return;
        }

        if (a.length == 1) {
            s.sendMessage(ChatColor.GRAY + "/"+l+" list" + ChatColor.RED + "でIDを確認することができます。");
            return;
        }

        if (a.length <= 2 || !a[2].equalsIgnoreCase("confirm")) {
            s.sendMessage(ChatColor.RED + "本当に仮想チェストを全部削除しますか? 一度削除したら元に戻すことはできません。");
            s.sendMessage(ChatColor.RED + "本当に消す場合は" + ChatColor.GRAY + "/" + l + " remove " + ChatColor.AQUA +  a[1] + ChatColor.GRAY + " confirm" + ChatColor.RED + "を実行してください");
            return;
        }

        int i;
        try {
            i = Integer.parseInt(a[1].replaceAll("#", ""));
        } catch (NumberFormatException er) {
            s.sendMessage(ChatColor.RED + "数字として読み込めませんでした。");
            return;
        }

        if (data.getChests().size() < i) {
            s.sendMessage(ChatColor.RED + "そのIDのチェストは存在していません。");
            return;
        }

        List<VChestData> chests = data.getChests();

        chests.remove(i-1);

        data.setChests(chests);

        try {
            PrintWriter writer = new PrintWriter(dataFile.toString());

            gson.toJson(data, SaveData.class, writer);

            writer.flush();
            writer.close();
        } catch (IOException er) {

        }
        s.sendMessage(ChatColor.GREEN + "削除が完了しました");
    }
}
