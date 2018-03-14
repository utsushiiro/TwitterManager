package jp.utsushiiro.twitter;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by @utsushiiro
 */
public class TwitterManager
{
    private Twitter twitter;

    private TwitterStream twitterStream;

    private User myAccount;

    public static void main(String[] args) throws TwitterException
    {
        TwitterManager twitterManager = new TwitterManager();
        twitterManager.dispatchScheduler();
    }

    public TwitterManager()
    {
        configure();
    }

    private void dispatchScheduler()
    {
        JobDetail saveProfileImageJobDetail = JobBuilder.newJob(SaveProfileImageJob.class)
                .withIdentity("save_profile_image", "save_profile")
                .build();
        saveProfileImageJobDetail.getJobDataMap().put("target_user", myAccount);

        JobDetail autoTweetJob = JobBuilder.newJob(TweetJob.class)
                .withIdentity("auto_tweet")
                .build();
        autoTweetJob.getJobDataMap().put("client", getClient());
        List<String> tweets = new ArrayList<>();
        autoTweetJob.getJobDataMap().put("tweets", tweets);

        Trigger endlessRepeatHourlyTrigger = TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(SimpleScheduleBuilder.repeatHourlyForever())
                .build();

        Trigger oneTimeTrigger = TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();

        Scheduler scheduler = null;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.scheduleJob(saveProfileImageJobDetail, endlessRepeatHourlyTrigger);
            scheduler.scheduleJob(autoTweetJob, oneTimeTrigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void configure()
    {
        if(twitter != null) return;
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder
                .setDebugEnabled(true)
                .setOAuthConsumerKey(System.getenv("TWITTER_OAUTH_CONSUMER_KEY"))
                .setOAuthConsumerSecret(System.getenv("TWITTER_OAUTH_CONSUMER_SECRET"))
                .setOAuthAccessToken(System.getenv("TWITTER_OAUTH_ACCESS_TOKEN"))
                .setOAuthAccessTokenSecret(System.getenv("TWITTER_OAUTH_ACCESS_TOKEN_SECRET"));

        Configuration configuration = configurationBuilder.build();
        twitter = new TwitterFactory(configuration).getInstance();
        twitterStream = new TwitterStreamFactory(configuration).getInstance();

        try {
            myAccount = twitter.verifyCredentials();
        } catch (TwitterException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Database.getInstance().initialize();
    }

    public Client getClient()
    {
        return new Client(twitter, twitterStream);
    }
}
