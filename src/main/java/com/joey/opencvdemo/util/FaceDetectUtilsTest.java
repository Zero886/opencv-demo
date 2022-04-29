package com.joey.opencvdemo.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liangyaoye
 * @date 2022-04-29 16:24:24
 * @since JDK8
 */
public class FaceDetectUtilsTest {

    public static void main(String[] args) throws IOException {
        File file = new File("D:/opencv/36f06b73-3f61-4f72-8fcf-ca83d91169e7.jpg");
        //ImageIO读取的文件格式有限制，有些图片实际格式可能不是后缀名的格式
        BufferedImage image = ImageIO.read(file);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", stream);
        String s = Base64.getEncoder().encodeToString(stream.toByteArray());

        List<String> stringList = FaceDetectUtils.cutOutFace(s);
        String prefix = "data:image/jpeg;base64,";
        List<String> imgList = stringList.stream().map(str -> prefix + str).collect(Collectors.toList());
        imgList.forEach(System.out::println);

    }
}
