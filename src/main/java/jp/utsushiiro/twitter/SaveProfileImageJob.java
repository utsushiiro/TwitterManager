package jp.utsushiiro.twitter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import twitter4j.User;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by @utsushiiro
 */
@Slf4j
public class SaveProfileImageJob implements Job
{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        User user = (User) jobDataMap.get("target_user");
        Objects.requireNonNull(user, "'target_user' of JobDataMap should be set.");

        URL profileImageURL = null;
        try {
            profileImageURL = new URL(user.getOriginalProfileImageURLHttps());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Path tmpPath = null;
        try {
            tmpPath = new Downloader(profileImageURL).download2TempFile(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            if (!ProfileImage.exists(user.getId(), tmpPath))
            {
                Path dest = Files.createDirectories(Paths.get("./images"));
                Path filename = Paths.get(
                        UUID.randomUUID() + "." + FilenameUtils.getExtension(tmpPath.toString())
                );
                dest = dest.resolve(filename);
                Files.copy(tmpPath, dest);
                new ProfileImage(user.getId(), dest).save();
                log.info("A new profile image has downloaded.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
