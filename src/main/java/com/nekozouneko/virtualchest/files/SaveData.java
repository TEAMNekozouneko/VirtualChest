package com.nekozouneko.virtualchest.files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaveData {

    public String UUID;
    public List<VChestData> Chests = new ArrayList<>();

    public SaveData(String UUID) {
        this.UUID = UUID;
    }

    public List<VChestData> getChests() {
        return Chests;
    }

    public void addChest(VChestData chest) {
        this.Chests.add(chest);
    }

    public void removeChest(VChestData chest) {
        this.Chests.remove(chest);
    }

    public void removeChest(int index) {
        this.Chests.remove(index);
    }

    public void clearChest() {
        this.Chests.clear();
    }

    public void setChests(List<VChestData> chests) {
        this.Chests = chests;
    }

}
