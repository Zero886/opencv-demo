package com.joey.opencvdemo.util;

import org.apache.commons.lang3.StringUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 图片人脸检测工具，截取图片中的人脸
 *
 * @author liangyaoye
 * @date 2022-04-28 10:30:43
 * @since JDK8
 */
public class FaceDetectUtils {

    private static final Logger log = LoggerFactory.getLogger(FaceDetectUtils.class);

    /**
     * 图片扩展名
     */
    public static final String JPG = ".jpg";
    public static final String JPEG = ".jpeg";
    public static final String PNG = ".png";

    /**
     * opencv xml文件路径
     */
    private static final String XML_FILE_PATH;

    static {
        // 载入opencv的库
        String opencvDllPath = FaceDetectUtils.class.getResource("/opencv/" + Core.NATIVE_LIBRARY_NAME + ".dll").getPath();
        // windows系统，去掉路径前面的/
        if (opencvDllPath !=null && "\\".equals(File.separator)) {
            opencvDllPath = opencvDllPath.substring(1);
        }
        log.info("OpenCV .dll文件路径：{}", opencvDllPath);
        System.load(opencvDllPath);

        String xmlfilePath = FaceDetectUtils.class.getResource("/opencv/haarcascade_frontalface_alt.xml").getPath();
        if (xmlfilePath !=null && "\\".equals(File.separator)) {
            xmlfilePath = xmlfilePath.substring(1);
        }
        log.info("OpenCV .xml文件路径：{}", xmlfilePath);
        XML_FILE_PATH = xmlfilePath;
    }

    /**
     * 截取人脸图片，默认返回图片类型为jpg
     *
     * @param base64Image base64图片字符串，无须包含前缀
     * @return base64人脸图片集合 不为null
     */
    public static List<String> cutOutFace(String base64Image) {
        return cutOutFace(base64Image, JPG);
    }

    /**
     * 截取人脸图片
     *
     * @param base64Image base64图片字符串，无须包含前缀
     * @param imgExt      返回图片类型扩展名，格式为 ".jpg", ".png",".jpeg"
     * @return base64人脸图片集合 不为null
     */
    public static List<String> cutOutFace(String base64Image, String imgExt) {
        if (StringUtils.isBlank(base64Image)) {
            return new ArrayList<>(0);
        }

        byte[] bytes = Base64.getDecoder().decode(base64Image.getBytes());
        Mat image = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
        Rect[] rects = faceDetect(image);

        ArrayList<String> faceImgList = new ArrayList<>(rects.length);
        if (rects.length > 0) {
            for (Rect rect : rects) {
                //裁剪人脸
                Mat sub = new Mat(image, rect);
                byte[] bs = mat2Byte(sub, imgExt);
                String s = Base64.getEncoder().encodeToString(bs);
                faceImgList.add(s);
            }
        }
        return faceImgList;
    }

    /**
     * 截取人脸图片，默认返回图片格式为jpg
     *
     * @param bytes 图片byte数组
     * @return 人脸图片byte数组集合 不为null
     */
    public static List<byte[]> cutOutFace(byte[] bytes) {
        return cutOutFace(bytes, JPG);
    }

    /**
     * 截取人脸图片
     *
     * @param bytes  图片byte数组
     * @param imgExt 返回图片类型扩展名，格式为 ".jpg", ".png",".jpeg"
     * @return 人脸图片byte数组集合 不为null
     */
    public static List<byte[]> cutOutFace(byte[] bytes, String imgExt) {
        if (bytes == null || bytes.length < 1) {
            return new ArrayList<>(0);
        }

        Mat image = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
        Rect[] rects = faceDetect(image);

        ArrayList<byte[]> faceImgList = new ArrayList<>(rects.length);
        if (rects.length > 0) {
            for (Rect rect : rects) {
                //裁剪人脸
                Mat sub = new Mat(image, rect);
                byte[] bs = mat2Byte(sub, imgExt);
                faceImgList.add(bs);
            }
        }
        return faceImgList;
    }

    /**
     * 检测人脸
     *
     * @param image Mat图片对象
     * @return 人脸在图片中的区域信息数组
     */
    private static Rect[] faceDetect(Mat image) {
        // 从配置文件 haarcascade_frontalface_alt.xml中创建一个人脸识别器
        CascadeClassifier faceDetector = new CascadeClassifier(XML_FILE_PATH);
        // 在图片中检测人脸
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);
        Rect[] rects = faceDetections.toArray();
        log.info("检测到{}张人脸", rects.length);
        return rects;
    }

    /**
     * Mat转换成byte数组
     *
     * @param img           要转换的mat
     * @param fileExtension 格式为 ".jpg", ".png",".jpeg"
     */
    private static byte[] mat2Byte(Mat img, String fileExtension) {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(fileExtension, img, mob);
        return mob.toArray();
    }

}
