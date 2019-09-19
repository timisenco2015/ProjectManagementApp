package ZihronChatApp.ZihronWorkChat.Channels;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.List;

public class ZWCGroupChannel {
    private Activity activity;
    private com.sendbird.android.GroupChannel mChannel;
    private List<Member> sortedUserList;
    public ZWCGroupChannel(final Activity activity)
    {
        this.activity=activity;
    }




    public void createGroupChannel(List<String> allUsersList, String channelName)
    {
        GroupChannelParams params = new GroupChannelParams()
                .setPublic(false)
                .setEphemeral(false)
                .setDistinct(true)
                .addUserIds(allUsersList)
                .setName(channelName);
        com.sendbird.android.GroupChannel.createChannel(params, new com.sendbird.android.GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(com.sendbird.android.GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {    // Error.
                    Toast.makeText(activity,"Unable to create default chat group for this project. Please contact our customer service",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    public void InviteUsers(final List<String> allUsersList,String channelName)
    {


        com.sendbird.android.GroupChannel.getChannel(channelName, new com.sendbird.android.GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(com.sendbird.android.GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                // Then invite the selected members to the channel.
                groupChannel.inviteWithUserIds(allUsersList, new com.sendbird.android.GroupChannel.GroupChannelInviteHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {
                            // Error!
                            return;
                        }

                    }
                });
            }
        });
    }

    public void deleteMulpleUser()
    {

    }

    public void addSingleUser()
    {

    }

    public void deleteSingleUser()
    {

    }


    public void acceptUserInvitation(String channelName)
    {
        Log.e("--++bbd",channelName);
        com.sendbird.android.GroupChannel.getChannel(channelName, new com.sendbird.android.GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(com.sendbird.android.GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    Log.e("--++bbe",e.getMessage());
                    return;
                }
        groupChannel.acceptInvitation(new com.sendbird.android.GroupChannel.GroupChannelAcceptInvitationHandler() {
            @Override
            public void onResult(SendBirdException e) {
                if (e != null) {    // Error.
                    return;
                }
                Log.e("--++bbc","test");
            }
        });
    }
        });
}



    public void declineUserInvitation(String channelName)
    {

        com.sendbird.android.GroupChannel.getChannel(channelName, new com.sendbird.android.GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(com.sendbird.android.GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }
                groupChannel.declineInvitation(new com.sendbird.android.GroupChannel.GroupChannelDeclineInvitationHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {    // Error.
                            return;
                        }
                    }
                });
            }
        });
    }


public List<Member> getChannelMemberList(String urlChannel)
{
    sortedUserList = new ArrayList<>();
    getChannelFromUrl(urlChannel);
    return sortedUserList;
}


    private void getChannelFromUrl(String url) {
        com.sendbird.android.GroupChannel.getChannel(url, new com.sendbird.android.GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(com.sendbird.android.GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                mChannel = groupChannel;
               // mChannel.
                        refreshChannel();
            }
        });
    }

    private void refreshChannel() {
        mChannel.refresh(new com.sendbird.android.GroupChannel.GroupChannelRefreshHandler() {
            @Override
            public void onResult(SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                setMemberList(mChannel.getMembers());
            }
        });
    }

    private void setMemberList(List<Member> memberList) {

        for (Member me : memberList) {
            if (me.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                sortedUserList.add(me);
                break;
            }
        }
        for (Member other : memberList) {
            if (other.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                continue;
            }
            sortedUserList.add(other);
        }


    }

    }

