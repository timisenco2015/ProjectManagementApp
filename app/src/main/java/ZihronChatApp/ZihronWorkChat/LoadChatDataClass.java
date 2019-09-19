package ZihronChatApp.ZihronWorkChat;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;
import com.sendbird.syncmanager.MessageCollection;
import com.sendbird.syncmanager.MessageEventAction;
import com.sendbird.syncmanager.MessageFilter;
import com.sendbird.syncmanager.handler.CompletionHandler;
import com.sendbird.syncmanager.handler.MessageCollectionCreateHandler;
import com.sendbird.syncmanager.handler.MessageCollectionHandler;

import java.util.List;

public class LoadChatDataClass {

    private Activity activity;
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private ImageView mSendButton;
    private String groupChannelUrl;
    GroupChannel mChannel;
    private MessageCollection mMessageCollection;
    GroupChatAdapter chatAdapter;
    final MessageFilter mMessageFilter = new MessageFilter(BaseChannel.MessageTypeFilter.ALL, null, null);

    public LoadChatDataClass(Activity activity, RecyclerView mRecyclerView,final EditText mEditText,final ImageView mSendButton, String groupChannelUrl)
    {
        this.activity=activity;
        this.mRecyclerView=mRecyclerView;
        this.mEditText =mEditText;
        this.mSendButton=mSendButton;
       this.groupChannelUrl=groupChannelUrl;
       chatAdapter = new GroupChatAdapter(activity);
        createMessageCollection(groupChannelUrl);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }
        });


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = mEditText.getText().toString();
                sendUserMessage(userInput);
                mEditText.setText("");
            }
        });
    }

    private void sendUserMessage(String text) {
        if (mChannel == null) {
            return;
        }

        mChannel.sendUserMessage(text, new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!

                    Toast.makeText(
                            activity,
                            "Send failed with error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT)
                            .show();

                    // remove preview message from MessageCollection
                    if (mMessageCollection != null) {
                        mMessageCollection.deleteMessage(userMessage);
                    }

                    // add failed message to adapter
                    chatAdapter.addFirst(userMessage);
                    chatAdapter.markMessageFailed(userMessage.getRequestId());
                    return;
                }

                // append sent message.
                if (mMessageCollection != null) {
                    mMessageCollection.appendMessage(userMessage);
                }
            }
        });


    }

    private void createMessageCollection(String channelUrl) {
        if (SendBird.getConnectionState() != SendBird.ConnectionState.OPEN) {
            MessageCollection.create(channelUrl, mMessageFilter, Long.MAX_VALUE, new MessageCollectionCreateHandler() {
                @Override
                public void onResult(MessageCollection messageCollection, SendBirdException e) {
                    if (e == null) {
                        if (mMessageCollection == null) {
                            mMessageCollection = messageCollection;
                            mMessageCollection.setCollectionHandler(getMessageHandler());
                            mChannel = mMessageCollection.getChannel();

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatAdapter.setChannel(mChannel);

                                }
                            });

                            mMessageCollection.fetch(MessageCollection.Direction.PREVIOUS, new CompletionHandler() {
                                @Override
                                public void onCompleted(SendBirdException e) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatAdapter.markAllMessagesAsRead();
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            });
        } else {
            GroupChannel.getChannel(channelUrl, new GroupChannel.GroupChannelGetHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e == null) {
                        mChannel = groupChannel;
                        chatAdapter.setChannel(mChannel);
                        if (mMessageCollection == null) {
                            mMessageCollection = new MessageCollection(groupChannel, mMessageFilter, Long.MAX_VALUE);
                            mMessageCollection.setCollectionHandler(getMessageHandler());

                            mMessageCollection.fetch(MessageCollection.Direction.PREVIOUS, new CompletionHandler() {
                                @Override
                                public void onCompleted(SendBirdException e) {


                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatAdapter.markAllMessagesAsRead();
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    public MessageCollectionHandler getMessageHandler()
    {
        MessageCollectionHandler mMessageCollectionHandler = new MessageCollectionHandler() {
            @Override
            public void onMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {


                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (action) {
                            case INSERT:
                                chatAdapter.insert(messages);
                                chatAdapter.markAllMessagesAsRead();
                                break;

                            case REMOVE:
                                chatAdapter.remove(messages);
                                break;

                            case UPDATE:
                                chatAdapter.update(messages);
                                break;

                            case CLEAR:
                                chatAdapter.clear();
                                break;
                        }
                    }
                });
            }
        };
        return mMessageCollectionHandler;
    }

}
