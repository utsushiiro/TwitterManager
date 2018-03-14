package jp.utsushiiro.twitter;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * Created by @utsushiiro
 */
public class TweetJob implements Job
{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        Client client = (Client) context.getMergedJobDataMap().get("client");
        List<String> tweets = autoCast(context.getMergedJobDataMap().get("tweets"));
        tweets.forEach(client::tweet);
    }

    @SuppressWarnings("unchecked")
    private static <T> T autoCast(Object obj)
    {
        return (T) obj;
    }
}