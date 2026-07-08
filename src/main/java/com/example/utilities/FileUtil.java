package com.example.utilities;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

@Component
public class FileUtil {

    public void eliminarArchivo(String fileName) {

        Path uploadPath = Paths.get("Files-Upload");
        Path otroPath = uploadPath.resolve(fileName);

        try {
            Files.deleteIfExists(otroPath);
        } catch (Exception e) {
            throw new RuntimeException("No se ha podido eliminar la imagen");
        }
    }
}
