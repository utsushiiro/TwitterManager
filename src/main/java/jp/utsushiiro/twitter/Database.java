package jp.utsushiiro.twitter;

import lombok.Getter;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by @utsushiiro
 */
public class Database
{
    @Getter
    private static Database instance = new Database();

    @Getter
    private QueryRunner queryRunner;

    private Database(){}

    public synchronized void initialize()
    {
        Properties properties = new Properties();
        InputStream is = ClassLoader.getSystemResourceAsStream("dbcp.secret.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        DataSource dataSource = null;
        try {
            dataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        queryRunner = new QueryRunner(dataSource);
    }
}