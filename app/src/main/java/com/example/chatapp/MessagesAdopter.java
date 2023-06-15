package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdopter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Messages> messagesArrayList;
    int Item_Send = 1;
    int Item_Recv =2;

    public MessagesAdopter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == Item_Send){
            View view = LayoutInflater.from(context).inflate(R.layout.senderchatlayout,parent,false);
            return  new senderViewHolder(view);
        }else{

            View view = LayoutInflater.from(context).inflate(R.layout.recieverchatlayout,parent,false);
            return  new receiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
Messages messages = messagesArrayList.get(position);
if(holder.getClass()==senderViewHolder.class){
    senderViewHolder viewHolder = (senderViewHolder) holder;
    viewHolder.message.setText(messages.getMessage());
    viewHolder.timeofmesssage.setText(messages.getCurrenttime());

}else{
    receiverViewHolder viewHolder = (receiverViewHolder) holder;
    viewHolder.message.setText(messages.getMessage());
    viewHolder.timeofmesssage.setText(messages.getCurrenttime());
}
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId()))
        {
            return  Item_Send;
        }else {
            return  Item_Recv;
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }
    class senderViewHolder extends RecyclerView.ViewHolder
    {
        TextView message;
        TextView timeofmesssage;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            message= itemView.findViewById(R.id.sendermessage);
            timeofmesssage = itemView.findViewById(R.id.timeofmessage);
        }
    }
    class receiverViewHolder extends RecyclerView.ViewHolder
    {
        TextView message;
        TextView timeofmesssage;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            message= itemView.findViewById(R.id.sendermessage);
            timeofmesssage = itemView.findViewById(R.id.timeofmessage);
        }
    }
}
