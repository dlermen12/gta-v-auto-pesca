import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Denner {

  static Robot robot;

  static boolean CAN_CONTINUE = true;

  static int vezes = 0;

  public static void main(String[] args) throws AWTException, IOException {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    robot = new Robot();
//
//    Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
//    System.out.println("mat = " + mat.dump());

    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            tiraFoto();
            Thread.sleep(15L);
            if (!CAN_CONTINUE) {
              return;
            }
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
      }
    }).start();
  }


  public static void tiraFoto() throws Exception {
    System.out.println("Tira foto");

//    BufferedImage image = robot.createScreenCapture(new Rectangle(795, 800, 320, 5));
    BufferedImage image = robot.createScreenCapture(new Rectangle(810, 800, 300, 10));

//    var file = new File("myScreenShot.png");
//    ImageIO.write(image, "png", file);

    Mat src = BufferedImage2Mat(image);
    Mat srcDist = new Mat();
    Mat srcCanny = new Mat();
    int threshold = 190;

    Imgproc.cvtColor(src, srcDist, Imgproc.COLOR_RGB2GRAY);
    Imgproc.Canny(srcDist, srcCanny, threshold, threshold * 2);

//    Imgproc.threshold(srcDist, srcCanny, 127, 255, 2);

//    ImageIO.write(image, "jpg", new File("test-or.jpg"));

//    BufferedImage image666 = Mat2BufferedImage(srcDist);
//    ImageIO.write(image666, "jpg", new File("test-gray.jpg"));
//
//
//    BufferedImage image2 = Mat2BufferedImage(srcCanny);
//    ImageIO.write(image2, "jpg", new File("test-canny.jpg"));
//ee
//
//    Mat mat2 = new Mat();
//    Core.normalize(src,mat2);
//    BufferedImage image777 = Mat2BufferedImage(mat2);
//    ImageIO.write(image777, "jpg", new File("test-normalized.jpg"));

    var mat = srcCanny;

//    HighGuiee
    List<MatOfPoint> whiteContours = new ArrayList<>();
    MatOfPoint heirarchy = new MatOfPoint();
    Imgproc.findContours(mat, whiteContours, heirarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
    System.out.println("Quantidade de polignos: " + whiteContours.size());

    if (whiteContours.size() == 1) {
      pressE();
      Thread.sleep(500L);
      pressE();
    } else if (whiteContours.size() == 0) {
      vezes++;
      if (vezes > 5) {
        pressE();
        vezes = 0;
      }
    }


  }

  public static void pressE() throws InterruptedException {
    robot.keyPress(KeyEvent.VK_E);
    Thread.sleep(30);
    robot.keyRelease(KeyEvent.VK_E);
  }

  public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(image, "jpg", byteArrayOutputStream);
    byteArrayOutputStream.flush();
    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
  }

  public static BufferedImage Mat2BufferedImage(Mat matrix) throws IOException {
    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".jpg", matrix, mob);
    return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
  }


}
