package me.alithernyx.bot.managers;

import me.alithernyx.bot.utils.ImageUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CharacterLoader {

    protected File path;

    public CharacterLoader(String path) {
        this.path = new File(path);
    }

    public HashMap<String, String> loadCharacters() {
        try {
            HashMap<String, String> imageList = new HashMap<>();
            for (String file : getResourceFiles("imgs")) {
                System.out.println(file);
                InputStream is = CharacterLoader.class.getResourceAsStream("/imgs/" + file);
                //System.out.println(is);
                String base64 = ImageUtils.encodeURLToBase64("/imgs/" + file, true);
                imageList.put(base64, file);
            }

            return imageList;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try(
            InputStream in = getResourceAsStream(path);
            BufferedReader br = new BufferedReader( new InputStreamReader(in))) {
            String resource;

            while( (resource = br.readLine()) != null ) {
                filenames.add( resource );
            }
        }

        return filenames;
    }

    private InputStream getResourceAsStream( String resource ) {
        final InputStream in = getContextClassLoader().getResourceAsStream( resource );

        return in == null ? getClass().getResourceAsStream( resource ) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
