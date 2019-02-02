package com.mygate.mahendran.mygatedemo.view.adaptor;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mygate.mahendran.mygatedemo.R;
import com.mygate.mahendran.mygatedemo.controller.GlideApp;
import com.mygate.mahendran.mygatedemo.model.dao.RealmUser;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmViewHolder;

public class UsersListAdaptor extends RealmRecyclerViewAdapter<RealmUser, UsersListAdaptor.MyHolder>  {

    private OrderedRealmCollection<RealmUser> objects;
    private Activity activity;

    public UsersListAdaptor(Activity activity, OrderedRealmCollection<RealmUser> realmResults, boolean automaticUpdate, boolean animateResults) {
        super(realmResults, automaticUpdate);

        this.objects = realmResults;
        this.activity = activity;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View child = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_user, viewGroup, false);

        return new MyHolder(child);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        RealmUser realmUser = objects.get(position);
        holder.bind(realmUser);
        GlideApp.with(holder.itemView)
                .load(realmUser.getUserPic())
                .circleCrop()
                .into(holder.ivUser);
        holder.tvUserName.setText(realmUser.getUserName());
        holder.tvUserPasscode.setText("# "+ realmUser.getPasscode());

    }


    @Override
    public int getItemCount() {
        if(objects != null) {
            if (objects.size() > 0) {
                return this.objects.size();
            }
        }
        return  0;
    }


    class MyHolder extends RealmViewHolder {

        RealmUser realmUser;
        ImageView ivUser;
        TextView tvUserName;
        TextView tvUserPasscode;
        void bind(RealmUser realmUser) {
            this.realmUser = realmUser;
        }
        MyHolder(View itemView) {
            super(itemView);
            ivUser = itemView.findViewById(R.id.ivUser);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserPasscode = itemView.findViewById(R.id.tvUserPasscode);
        }
    }

}
