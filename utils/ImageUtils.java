package me.alithernyx.bot.utils;

import me.alithernyx.bot.exceptions.BlacklistedExtensionException;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Base64;

public class ImageUtils {

    public static final String[] whitelistedExtensions = new String[]{
            "png",
            "jpeg",
            "jpg",
            "gif",
    };

    public static String encodeURLToBase64(String image, boolean stream) throws BlacklistedExtensionException, IOException {
        byte[] data = new byte[] {};
        if(stream) {
            data = IOUtils.toByteArray(ImageUtils.class.getResourceAsStream(image));
        } else {
            data = byteArr(image);
        }
        String uri = "data:image/" + getExtension(image) + ";base64," + Base64.getEncoder().encodeToString(data);

        return uri;
    }

    public static byte[] byteArr(String image) throws BlacklistedExtensionException, IOException {
        byte data[] = new byte[] {};
        System.out.println("Getting content for URl : " + image);
        URL url = new URL(image);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
        uc.connect();
        data = IOUtils.toByteArray(uc.getInputStream());

        return data;
    }

    public static String getExtension(String url) throws BlacklistedExtensionException {
        String ext = url.substring(url.lastIndexOf(".") + 1);
        if (Arrays.asList(whitelistedExtensions).stream().anyMatch(s -> s.equalsIgnoreCase(ext))) {
            return ext;
        } else {
            throw new BlacklistedExtensionException("The extension " + ext + " is not whitelisted!");
        }
    }

    public static byte[] drawString(byte[] data, String text, int x, int y, int size) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        BufferedImage im = ImageIO.read(is);
        Graphics2D g = im.createGraphics();
        g.setFont(new Font("Arial", Font.BOLD, size));
        g.drawString(text, x, y);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(im, "png", baos);

        return baos.toByteArray();
    }

    public static byte[] drawString(byte[] data, String text, int x, int y, int size, float scale) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        BufferedImage im = ImageIO.read(is);
        Graphics2D g = im.createGraphics();
        g.scale(1/scale, 1/scale);
        g.setFont(new Font("Arial", Font.BOLD, (int) (size * scale)));
        g.setColor(Color.DARK_GRAY);
        g.drawString(text, x * scale, y * scale);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(im, "png", baos);

        return baos.toByteArray();
    }

    public static byte[] drawStringWrap(byte[] data, String text, int x, int y, int width, int size, float scale) throws IOException {
        // Ugly code to wrap text
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        BufferedImage im = ImageIO.read(is);
        Graphics2D g = im.createGraphics();
        g.scale(1/scale, 1/scale);
        g.setFont(new Font("Arial", Font.BOLD, (int) (size * scale)));
        g.setColor(Color.DARK_GRAY);
        FontMetrics textMetrics = g.getFontMetrics();
        int lineHeight = textMetrics.getHeight();
        String textToDraw = text;
        String[] arr = textToDraw.split(" ");
        int nIndex = 0;
        int startX = (int) (x * scale);
        int startY = (int) (y * scale);
        while ( nIndex < arr.length )
        {
            String line = arr[nIndex++];
            while ( ( nIndex < arr.length ) && (textMetrics.stringWidth(line + " " + arr[nIndex])  < width * scale) )
            {
                line = line + " " + arr[nIndex];
                nIndex++;
            }
            g.drawString(line, startX, startY);
            startY = startY + lineHeight;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(im, "png", baos);

        return baos.toByteArray();
    }

    public static byte[] drawImage(byte[] orgImg, byte[] newImg, int x, int y, int w, int h) throws IOException {
        ByteArrayInputStream orIs = new ByteArrayInputStream(orgImg);
        ByteArrayInputStream nIs = new ByteArrayInputStream(newImg);
        BufferedImage orIm = ImageIO.read(orIs);
        BufferedImage nIm = resize(ImageIO.read(nIs), w, h);
        Graphics2D g = orIm.createGraphics();
        g.drawImage(nIm, null, x, y);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(orIm, "png", baos);

        return baos.toByteArray();
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static byte[] roundCorner(byte[] data, int cornerRadius) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        BufferedImage image = ImageIO.read(is);
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)

        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(output, "png", baos);

        return baos.toByteArray();
    }
}
