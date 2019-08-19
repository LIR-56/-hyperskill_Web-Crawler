package crawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

class DataSaverToFile {

    private final String fileName;
    private final Vector<Vector> data;

    DataSaverToFile(String fileName, Vector<Vector> dataVector) {
        this.fileName = fileName;
        this.data = dataVector;
    }

    void save() {
        try (FileWriter fw = new FileWriter(fileName, false)) {
            for (Vector i : data) {
                for (Object s : i) {
                    fw.append(s.toString());
                    fw.append('\n');
                }
            }
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
