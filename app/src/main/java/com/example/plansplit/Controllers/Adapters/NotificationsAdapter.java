package com.example.plansplit.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Models.Objects.Notification;
import com.example.plansplit.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

    List<Notification> notificationsList;
    Context mCtx;
    OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(NotificationsAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    public NotificationsAdapter(Context mCtx, List<Notification> notificationList) {
        this.mCtx=mCtx;
        this.notificationsList=notificationList;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_notification,
                parent, false);
        NotificationsAdapter.NotificationsViewHolder notificationsViewHolder = new NotificationsAdapter.NotificationsViewHolder(view, mListener);

        return notificationsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        Notification notification = notificationsList.get(position);
        holder.dateText.setText(notification.getDate());
        holder.mainTextView.setText(notification.getMainText());
        holder.secondTextView.setText(notification.getSecondText());
        holder.clockTextView.setText(notification.getClock());

        switch (notification.getImage()){
            case "seyehat":
                holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.ic_trip_radius));
                break;
            case "ev":
                holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.ic_home_black_radius));
                break;
            case "i??":
                holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.ic_suitcase_radius));
                break;
            case "di??er":
                holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.ic_other));
                break;
            default:
                Picasso.with(mCtx).load(notification.getImage()).error(R.drawable.ic_other).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView mainTextView, dateText, secondTextView, clockTextView;

        public NotificationsViewHolder(final View itemView, final NotificationsAdapter.OnItemClickListener listener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.notification_image);
            mainTextView = itemView.findViewById(R.id.notification_main_text);
            dateText = itemView.findViewById(R.id.notification_date_text);
            clockTextView = itemView.findViewById(R.id.notification_clock_text);
            secondTextView = itemView.findViewById(R.id.notification_second_text);

            itemView.findViewById(R.id.notification_card).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
