package org.palemire;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class GenSourceFileVisitor extends SimpleFileVisitor<Path> {

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!file.toString().endsWith("java"))
            return FileVisitResult.CONTINUE;

        System.out.println("Crappy swagger post-processor plugin : visit file : " + file);

        BufferedReader reader = Files.newBufferedReader(file);

        Path filteredFile = Path.of(file.toString() + "x");
        BufferedWriter writer = Files.newBufferedWriter(filteredFile);

        reader.lines().forEach( line -> {
            if ( !line.contains("ApiUtil") &&
            !line.contains("import org.openapitools.jackson.nullable.JsonNullable;") ) {

                try {
                    writer.write(line);
                    writer.newLine();
                } catch(IOException e) {System.err.println(e.getMessage());}
            }
        });
        writer.flush();
        writer.close();
        reader.close();

        try {
            Files.copy(filteredFile, file,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch(FileAlreadyExistsException e) {
            //destination file already exists
        } catch (IOException e) {
            //something else went wrong
            e.printStackTrace();
        }
        Files.deleteIfExists(filteredFile);

        System.out.println("Crappy swagger post-processor plugin : done visiting file : " + file);

        return FileVisitResult.CONTINUE;
    }
}
