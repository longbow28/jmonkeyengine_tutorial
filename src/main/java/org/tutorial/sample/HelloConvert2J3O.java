package org.tutorial.sample;

import java.io.File;
import java.io.IOException;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Spatial;

public class HelloConvert2J3O {
    public void covert2J3O(Spatial srcModel, String targetFilePath) throws IOException {
        BinaryExporter exporter = BinaryExporter.getInstance();
        File target = new File(targetFilePath);
        exporter.save(srcModel, target);
    }
}
