package com.example.utilities;

import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadUtil {

    
    public String saveFile(String fileName, MultipartFile multipartFile) throws IOException {

        //String fileCode = null;

        Path uploadPath = Paths.get("Files-Upload");

        // comprobar si existe la ruta = carpeta.

        if (!Files.exists(uploadPath)) {

            Files.createDirectories(uploadPath);

        }

        // generar código de 8 caracteres
/*
        RandomStringGenerator generator = RandomStringGenerator.builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .get();

                fileCode = generator.generate(8);

*/
        String caracteres = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom random = new SecureRandom();

        String fileCode = random.ints(8, 0, caracteres.length())
                .mapToObj(caracteres::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
 
        

        // el try with resources pone entre parentesis los recursos que tienen que ser cerrados = implementan autoCloseable
        try (InputStream inputStream = multipartFile.getInputStream()) {

            Path destino = uploadPath.resolve(fileCode + '-' + fileName);
            Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ioe) {
            
            throw new IOException("error al guardar la imagen recibida" + fileName, ioe);
        }


        return fileCode;
    }
}
