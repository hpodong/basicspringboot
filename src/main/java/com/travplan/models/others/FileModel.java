package com.travplan.models.others;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travplan.annotations.BSColumn;
import com.travplan.annotations.BSTable;
import com.travplan.models._BSModel;
import jakarta.servlet.http.Part;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@BSTable(
        name = "file",
        primaryKey = "f_idx",
        createdAt = "f_crdt"
)
public class FileModel extends _BSModel {

    @BSColumn(name = "f_idx")
    private Long idx;
    @BSColumn(name = "f_folder")
    private String folder;
    @BSColumn(name = "f_name")
    private String name;
    @BSColumn(name = "f_enc_name")
    private String enc_name;
    @BSColumn(name = "f_url")
    private String url;
    @BSColumn(name = "f_size")
    private Long size;
    @BSColumn(name = "f_type")
    private String type;
    @BSColumn(name = "f_class")
    private String clazz;
    @BSColumn(name = "f_crdt")
    private Timestamp createdAt;

    @JsonIgnore
    @ToString.Exclude
    private Path path;
    @JsonIgnore
    @ToString.Exclude
    private byte[] bytes;

    public FileModel(ResultSet rs, int row_num) {
        super(rs, row_num);
    }

    public FileModel(ResultSet rs, String alias) {
        try {
            url = rs.getString(alias);
        } catch (SQLException ignored) {
        }
    }

    private String getUploadDir() {
        return System.getProperty("user.dir");
    }

    public FileModel(String fd, Part part) throws IOException {
        this.size = part.getSize();
        if(this.size > 0) {
            final String user_dir = getUploadDir();

            this.folder = getFolder(fd, part.getContentType());
            this.name = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            this.type = getExtension();
            this.enc_name = System.currentTimeMillis()+"_"+(int)(Math.random() * 1000);
            this.url = this.folder + "/" + this.enc_name + "." + type;

            path = Paths.get(user_dir, this.url);
            bytes = part.getInputStream().readAllBytes();
        }
    }

    private Path createFile() {
        try {
            final String user_dir = getUploadDir();
            final Path directory = Paths.get(user_dir, this.folder);
            if(!Files.exists(directory)) Files.createDirectories(directory);
            return Files.write(path, bytes);
        } catch (IOException e) {
            return null;
        }
    }

    public boolean create() {
        return createFile() != null;
    }

    public boolean create(Integer width, Integer height) {
        if(create() && Files.exists(path)) {
            try {
                final BufferedImage originalImage = ImageIO.read(path.toFile().toURI().toURL());
                final BufferedImage thumbnailImage = new BufferedImage(width, height, originalImage.getType());
                final Graphics2D graphics = thumbnailImage.createGraphics();
                graphics.drawImage(originalImage, 0, 0, width, height, null);
                graphics.dispose();

                return ImageIO.write(thumbnailImage, type, path.toFile());
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean removeFile(String url) {
        final Path path = Paths.get(getUploadDir(), url);
        final File file = path.toFile();
        return file.delete();
    }

    public boolean remove() {
        return removeFile(url);
    }

    public boolean remove(int width, int height) {
        return removeFile(url) && removeFile(toThumbnail(width, height));
    }

    public void createThumbnail(Integer width, Integer height) {
        try {
            if(Files.exists(path)) {
                final File originFile = path.toFile();
                final BufferedImage originalImage = ImageIO.read(originFile.toURI().toURL());
                final BufferedImage thumbnailImage = new BufferedImage(width, height, originalImage.getType());
                final Graphics2D graphics = thumbnailImage.createGraphics();
                graphics.drawImage(originalImage, 0, 0, width, height, null);
                graphics.dispose();

                final Path thumbnailPath = Paths.get(path.toString().replace(enc_name, enc_name+"_"+width+"x"+height));
                ImageIO.write(thumbnailImage, type, thumbnailPath.toFile());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getExtension() {
        return name.substring(name.lastIndexOf(".")).replace(".", "");
    }

    private String getFolder(String folder, String contentType) {
        final String type;
        if(contentType != null) {
            if(contentType.startsWith("image/")) {
                type = "image";
            } else if(contentType.startsWith("application/")) {
                type = "doc";
            } else if(contentType.startsWith("video/")) {
                type = "video";
            } else {
                type = "other";
            }
        } else {
            type = "other";
        }
        clazz = type;
        return "/uploads" + folder + "/" + type;
    }

    public boolean hasFile() {
        return size != null && size > 0;
    }

    public String toThumbnail(int width, int height) {
        if(url == null) return null;
        return url.replace(enc_name, enc_name+"_"+width+"x"+height);
    }

    public void refreshThumbnail(int width, int height) {
        if(url != null) {
            setUrl(toThumbnail(width, height));
        }
    }
}
