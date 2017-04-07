import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Taylor on 4/6/2017.
 */

/**
 * Ugly code
 */
public class GraphicAssets implements Runnable {

    private boolean loaded;

    private int canvas_width;
    private float width_scale;

    private int total_assets = 0;
    private int assets_loaded = 0;
    private Map<String, Image> asset_cache;

    public GraphicAssets(int width) {
        canvas_width = width;
        asset_cache = new HashMap<>();
        new Thread(this).start();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public float getProgress() {
        return (float) assets_loaded / total_assets;
    }

    public Image getImage(String name) {
        return asset_cache.get(name);
    }

    /**
     * This is bad OOP
     */
    public void load() {
        File graphic_assets_folder = new File("graphic_assets");
        File[] graphic_assets = graphic_assets_folder.listFiles();

        if (graphic_assets == null) {
            System.out.println("Folder graphic_assets not found.");
            return;
        }

        total_assets = graphic_assets.length;

        // we're given the canvas dimensions based off user screen dimensions, use this to scale each image
        BufferedImage bufferedImage = null;
        Image image = null;

        int scaled_width;
        int scaled_height;

        // grab the loading screen first, the remaining images will scale based off of it
        try {
            bufferedImage = ImageIO.read(getClass().getResource("menu_Screen_Landscape.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bufferedImage == null) {
            width_scale = 1F;
        } else {
            width_scale = (float) canvas_width / (float) bufferedImage.getWidth();

            if (width_scale > 1F) {
                width_scale = 1F;
            }
        }

        scaled_width = scaledCoordinate(bufferedImage.getWidth());
        scaled_height = scaledCoordinate(bufferedImage.getHeight());
        image = new ImageIcon(bufferedImage.getScaledInstance(scaled_width, scaled_height, 5)).getImage();
        ++assets_loaded;
        asset_cache.put("menu_Screen_Landscape.png", image);

        for (File file : graphic_assets) {
            if (!asset_cache.containsKey(file.getName()) && file.getName().contains(".png")) {
                try {
                    bufferedImage = ImageIO.read(getClass().getResource(file.getName()));

                    scaled_width = scaledCoordinate(bufferedImage.getWidth());
                    scaled_height = scaledCoordinate(bufferedImage.getHeight());

                    image = new ImageIcon(bufferedImage.getScaledInstance(scaled_width, scaled_height, 5)).getImage();
                    ++assets_loaded;
                    asset_cache.put(file.getName(), image);
                } catch (IOException e) {
                    System.out.printf("Failed to load %s\n", file.getName());
                    e.printStackTrace();
                }
            }
        }

        loaded = true;
    }

    public int scaledCoordinate(int c) {
        return (int) (c * width_scale);
    }

    @Override
    public void run() {
        load();
    }
}
