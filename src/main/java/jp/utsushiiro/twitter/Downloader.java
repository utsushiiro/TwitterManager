package jp.utsushiiro.twitter;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by @utsushiiro
 */
public class Downloader
{
    private URL url;

    public Downloader(URL url)
    {
        this.url = url;
    }

    public void download(Path path) throws IOException
    {
        URLConnection url_connection =  url.openConnection();

        HttpURLConnection http_url_connection = null;
        if (url_connection instanceof  HttpURLConnection)
            http_url_connection = (HttpURLConnection)url_connection;
        else
        {
            System.err.println("supports only HTTP(S) URLs");
            System.exit(1);
        }

        http_url_connection.connect();

        int httpStatusCode = http_url_connection.getResponseCode();
        if (httpStatusCode != HttpURLConnection.HTTP_OK)
            throw new RuntimeException();

        try(InputStream in = http_url_connection.getInputStream())
        {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public Path download2TempFile(boolean isSameExtension) throws IOException
    {
        String extension;
        if (isSameExtension)
            extension = "." + FilenameUtils.getExtension(url.getPath());
        else
            extension = ".tmp";

        Path tempFilePath = Files.createTempFile("", extension);

        download(tempFilePath);

        return tempFilePath;
    }
}
