package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class GMTest {

    /**
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(final String[] args) throws IOException, InterruptedException {
        final File sourceImage =
                new File("D:\\Data\\device-mgt-refactor\\ics-imagemagick\\"
                        + "images\\src\\yellow-pages_HD.png");
        final File outputImage =
                new File("D:\\Data\\device-mgt-refactor\\ics-imagemagick\\images\\temp.gif");

        final List<String> cmd = new ArrayList<String>();
        cmd.add("gm");
        cmd.add("convert");
        cmd.add("-resize");
        cmd.add("20x%");
        cmd.add("-unsharp");
        cmd.add("0x1");
        cmd.add(sourceImage.getPath());
        cmd.add(outputImage.getPath());
        final ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        final Process process = processBuilder.start();
        final int exitStatus = process.waitFor();
        IOUtils.copy(process.getInputStream(), System.out);
        IOUtils.copy(process.getErrorStream(), System.out);
        if (exitStatus != 0) {
            throw new RuntimeException("exit status was 0 !!!");
        }
    }

}
