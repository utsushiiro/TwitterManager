package jp.utsushiiro.twitter;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;

/**
 * Created by @utsushiiro
 */
public class Client
{
    private Twitter twitter;

    private TwitterStream twitterStream;

    Client(Twitter twitter, TwitterStream twitterStream)
    {
        this.twitter = twitter;
        this.twitterStream = twitterStream;
    }

    public void tweet(String content)
    {
        try {
            twitter.updateStatus(content);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see <a href="https://developer.twitter.com/en/docs/tweets/search/guides/standard-operators">Using the standard search endpoint</a>
     */
    public QueryResult search(Query query)
    {
        QueryResult result = null;
        try{
            result = twitter.search(query);
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return result;
    }
}
