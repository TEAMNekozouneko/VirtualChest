package com.nekozouneko.virtualchest.listener;

import com.google.gson.Gson;
import com.nekozouneko.virtualchest.VirtualChest;
import com.nekozouneko.virtualchest.files.SaveData;
import com.nekozouneko.virtualchest.files.VChestData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class InventoryClose implements Listener {

    private VirtualChest instance = VirtualChest.getInstance();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().startsWith("仮想チェスト")) {
            int id = Integer.parseInt(e.getView().getTitle().split("#")[1]);

            try {
                Gson gson = new Gson();

                File data_dir = new File(instance.getDataFolder(), "data/");

                if (!instance.getDataFolder().exists()) {
                    instance.getDataFolder().mkdir();
                }

                if (!data_dir.exists()) {
                    data_dir.mkdir();
                }

                Path dataFile = Paths.get(data_dir.toString(), e.getPlayer().getUniqueId()+".json");

                BufferedReader reader;
                try {
                    try {
                        Files.createFile(dataFile);
                    } catch (FileAlreadyExistsException ignored) {}
                    reader = Files.newBufferedReader(dataFile);
                } catch (IOException ignored) {
                    return;
                }

                SaveData data = gson.fromJson(reader, SaveData.class);

                if (data == null) {
                    return;
                }

                ByteArrayOutputStream bytes2 = new ByteArrayOutputStream();
                BukkitObjectOutputStream out = new BukkitObjectOutputStream(bytes2);

                out.writeObject(e.getInventory().getContents());

                byte[] byteArr = bytes2.toByteArray();

                List<VChestData> chests = data.getChests();
                VChestData edit = data.getChests().get(id - 1);
                edit.setContent(Base64Coder.encodeLines(byteArr));

                data.getChests().set(id-1, edit);

                PrintWriter writer = new PrintWriter(dataFile.toString());

                gson.toJson(data, SaveData.class, writer);
                writer.flush();
                writer.close();
            } catch (IOException er) {
                er.printStackTrace();
            }
        }
    }

}
