package org.example;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static javax.imageio.ImageIO.createImageInputStream;

public class PictureProcessor {

    @SneakyThrows
    public SendPhoto getPhoto(String address, String chatId){
        URL url = new URL("https://www.example.com/image.jpg");
/*        URLConnection connection = url.openConnection();
        InputStream in = connection.getInputStream();
        InputFile InputPhoto = new InputFile(in, "guts.jpg");
        return new SendPhoto(chatId, InputPhoto);*/

        BufferedImage image = ImageIO.read(url);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        InputFile photo = new InputFile(is, "image.jpg");

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(photo);
        return sendPhoto;
    }
}
