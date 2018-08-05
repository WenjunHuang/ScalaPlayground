package threading;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.URL;

public class CheckThreadState {
    public static void main(String[] args) throws InterruptedException {
        Thread mainThread = Thread.currentThread();
        Thread t = new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getState());
                mainThread.join();
            } catch (Exception e) {
            }
        });
        t.start();

        mainThread.sleep(100000);
    }

    private void downloadFile(URL url, OutputStream outputStream, int bufferSize) throws Exception {
        byte[] buffer = new byte[bufferSize];
        try (InputStream in = url.openStream()) {
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            outputStream.close();
        }
    }
}
