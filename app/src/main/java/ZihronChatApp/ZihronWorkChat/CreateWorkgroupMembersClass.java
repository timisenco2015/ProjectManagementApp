package ZihronChatApp.ZihronWorkChat;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;
import android.widget.RelativeLayout;

import com.sendbird.android.Member;
import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.R;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateWorkgroupMembersClass {

    private Activity activity;
    private RelativeLayout groupChatMemebrsRelativeLayout;
    private S3ImageClass s3ImageClass;
    List<Member>  getChannelMemberList;
    public CreateWorkgroupMembersClass(Activity activity, RelativeLayout groupChatMemebrsRelativeLayout, List<Member>  getChannelMemberList)
    {
        this.activity = activity;
        this.s3ImageClass = new S3ImageClass();
        this.groupChatMemebrsRelativeLayout = groupChatMemebrsRelativeLayout;
        this.getChannelMemberList = getChannelMemberList;
        int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                groupMembersListVwLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:

                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                // friendsSuggestionsLayout.friendSuggestnListVwXLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                break;

            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                // friendsSuggestionsLayout.friendSuggestnListVwSmallScreenSizeLT();
                break;
            default:

        }
    }

    public void groupMembersListVwLargeScrnSizeLT()
    {
        for(int i=0; i<getChannelMemberList.size();i++) {

            Member user = (Member) getChannelMemberList.get(i);
            if (user.isBlockedByMe() || user.isBlockingMe()) {

            } else {
                CircleImageView profileImageVW = new CircleImageView(activity);
                RelativeLayout.LayoutParams llpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(120, 120);
                llpInnerProfileImageVWLayout.setMargins(50, 20, 0, 0);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                profileImageVW.setClickable(true);
                profileImageVW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });
                profileImageVW.setId(R.id.memberLogoId);
                Picasso.with(activity).load(user.getProfileUrl()).into(profileImageVW);
                groupChatMemebrsRelativeLayout.addView(profileImageVW);
            }
        }
        }
    }

