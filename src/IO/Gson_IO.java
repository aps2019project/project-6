package IO;

import Model.Spell;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Gson_IO {
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        File file = new File("src/Spells");
        Scanner scanner = new Scanner(file);
        String info;
        int index = 1;
        while (scanner.hasNextLine()) {
            info = scanner.nextLine();
            Spell spell = new Spell(index, info);
            gson.toJson(spell, System.out);
            String json = gson.toJson(spell);

            try {
                FileWriter writer = new FileWriter(spell.getName() + ".json");
                writer.write(json);
                writer.close();
            } catch (IOException e0) {
                e0.printStackTrace();
            }
            index++;
        }
    }
}
