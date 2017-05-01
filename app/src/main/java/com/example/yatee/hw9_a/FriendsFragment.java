package com.example.yatee.hw9_a;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by yatee on 4/19/2017.
 */

public class FriendsFragment extends Fragment {
    DatabaseReference ref2;
    DatabaseReference ref3;
    FirebaseDatabase db;
    User user;
    FirebaseUser firebaseUser;
    ArrayList<User> friends;
    ArrayList<User> received;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_friends, container, false);
        if (getUserVisibleHint()) {
            visibleActions();
        }
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(isResumed()){
                visibleActions();
            }
        }

    }

    private void visibleActions(){

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        db = FirebaseDatabase.getInstance();

        friends=new ArrayList<User>();
        received=new ArrayList<User>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        ref2 = db.getReference("Users").child(firebaseUser.getUid());
        ref3 = db.getReference("Users");

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("UserIS:",snapshot.getValue(User.class).toString());

                user=snapshot.getValue(User.class);
                final ArrayList<String> requestReceivedString=user.getRequestsReceived();
                final ArrayList<String> friendsString=user.getFriends();
                ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot1: dataSnapshot.getChildren()){
                            if(requestReceivedString!=null)
                            for(int i=0;i<requestReceivedString.size();i++){
                                Log.d("Testing:",requestReceivedString.get(i));
                                if(snapshot1.getKey().toString().equals(requestReceivedString.get(i))){
                                    Log.d("Testing:","Entered");
                                    received.add(snapshot1.getValue(User.class));
                                }
                                Log.d("ReceivedList:",received.toString());
                                ListView lv= (ListView) getActivity().findViewById(R.id.received);

                                //ArrayAdapter<Color> adapter=new ArrayAdapter<Color>(this,android.R.layout.simple_list_item_1,colors);
                                RequestAdapter adapter=new RequestAdapter(getActivity(),R.layout.requests,received,user,ref3,1);
                                lv.setAdapter(adapter);
                                adapter.setNotifyOnChange(true);
                            }

                            if(friendsString!=null)
                                for(int i=0;i<friendsString.size();i++){
                                    Log.d("Testing:",friendsString.get(i));
                                    if(snapshot1.getKey().toString().equals(friendsString.get(i))){
                                        Log.d("Testing:","Entered");
                                        friends.add(snapshot1.getValue(User.class));
                                    }
                                    Log.d("FriendsList:",received.toString());
                                    ListView lv2= (ListView) getActivity().findViewById(R.id.friends);
                                    //ArrayAdapter<Color> adapter=new ArrayAdapter<Color>(this,android.R.layout.simple_list_item_1,colors);
                                    RequestAdapter adapter=new RequestAdapter(getActivity(),R.layout.requests,friends,user,ref3,2);
                                    lv2.setAdapter(adapter);
                                    adapter.setNotifyOnChange(true);
                                }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Demo", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }
}
