package groupup.com.groupup;

import android.content.Intent;
import android.nfc.Tag;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import groupup.com.groupup.Database.DatabaseManager;

public class OutsiderGroupPage extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "OutsiderGroupPage";
    private Group currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outsider_group_page);
        findViewById(R.id.joinButton).setOnClickListener(this);
        findViewById(R.id.waitlistButton).setOnClickListener(this);
        getIncomingIntent();
    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for incoming intents");
        if(getIntent().hasExtra("group"))
        {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            Group currentGroup = (Group) getIntent().getSerializableExtra("group");
            String imageUrl = currentGroup.getPicture();
            String groupName = currentGroup.getName();
            String groupLocation = currentGroup.getLocation();
            String groupActivity = currentGroup.getActivity();

            this.currentGroup = currentGroup;
            setImage(imageUrl, groupName, groupLocation, groupActivity);
        }
    }

    public void onClick(View v) {
        DatabaseReference chatroom = FirebaseDatabase.getInstance().getReference("chatrooms");
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        int i = v.getId();
        boolean isMem = false;


        DatabaseManager manager = DatabaseManager.getInstance();

        if (i == R.id.joinButton) {
            for (String uid : this.currentGroup.getMembers()) {
                if(uid.equals(userID)){
                    isMem = true;
                    Toast.makeText(OutsiderGroupPage.this, "You are already a member of this group", Toast.LENGTH_LONG).show();
                }
            }
            if(!isMem){
                currentGroup.addMember(userID);
                manager.updateGroupWithID(currentGroup.getID(), currentGroup);
            }
            finish();
        }
        else if (i == R.id.waitlistButton) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put(currentGroup.getName(), "");
            chatroom.updateChildren(map);
            Intent intent = new Intent(this, InterviewPage.class);
            startActivity(intent);
        }
    }

    private synchronized void setImage(final String imageUrl, final String groupName, final String groupLocation, final String groupActivity){
        Log.d(TAG, "setImage: setting the image and name to widgets");

        final TextView name = findViewById(R.id.group_description);
        setDisplayText(name, groupName + ": " + groupActivity + "\n\t Location: " +
                groupLocation + "\n\n" + this.currentGroup.getMembers().size() + " Members");

        ImageView image = findViewById(R.id.image);

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);
    }

    private void setDisplayText(TextView view, String text) {
        view.setText(text);
    }
}
