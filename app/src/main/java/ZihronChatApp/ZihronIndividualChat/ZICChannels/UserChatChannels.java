package ZihronChatApp.ZihronIndividualChat.ZICChannels;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.Member;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.List;

public class UserChatChannels {
    List<Member> members;
    GroupChannel finalGroupChannel;
    public UserChatChannels()
    {

    }

    public void getUserAllChannels()
    {
        GroupChannelListQuery channelListQuery = GroupChannel.createMyGroupChannelListQuery();
        channelListQuery.setIncludeEmpty(true);
        channelListQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                if (e != null) {    // Error.
                    return;
                }
            }
        });
    }


    public GroupChannel  getUserSpecificChannels(String channelName)
    {
        GroupChannel.getChannel(channelName, new GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {    // Error!
                    return;
                }
                else
                {
                    finalGroupChannel = groupChannel;
                }

                // Successfully fetched the channel.
                // Do something with groupChannel.
            }
        });

        return  finalGroupChannel;
    }

    public List<Member> getChannelAllUsers(String channelName)
    {
      GroupChannel groupChannel =  getUserSpecificChannels(channelName);
      return groupChannel.getMembers();
    }

    public User.ConnectionStatus getEachUserOnlineStatus(Member member)
    {
      return member.getConnectionStatus();
    }

}
