package jp.utsushiiro.twitter;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.dbutils.QueryRunner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by @utsushiiro
 */
public class ProfileImage
{
    @Getter @Setter
    private long userId;

    private String imageHashCode;

    private Path savePath;

    public ProfileImage(long userId, Path savePath)
    {
        setUserId(userId);
        setSavePath(savePath);
    }

    public void save() throws SQLException
    {
        QueryRunner queryRunner = Database.getInstance().getQueryRunner();
        queryRunner.execute(
                "insert into profile_img_hash (user_id, image_hash_code, save_path) values (?, ?, ?)",
                userId,
                imageHashCode,
                savePath.toString()
        );
    }

    public void setSavePath(Path savePath)
    {
        this.savePath = savePath;
        this.imageHashCode = calcSHA1Hashcode(savePath);
    }

    public static boolean exists(long user_id, Path path) throws SQLException
    {
        QueryRunner queryRunner = Database.getInstance().getQueryRunner();
        return queryRunner.query(
                "select * from profile_img_hash where user_id = ? and image_hash_code = ?",
                ResultSet::isBeforeFirst,
                user_id,
                calcSHA1Hashcode(path)
        );
    }

    private static String calcSHA1Hashcode(Path path)
    {
        String hashCode = null;
        try(InputStream inputStream = Files.newInputStream(path)){
            hashCode = DigestUtils.sha1Hex(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return hashCode;
    }
}
